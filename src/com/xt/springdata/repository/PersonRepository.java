package com.xt.springdata.repository;


import com.xt.springdata.dao.PersonDao;
import com.xt.springdata.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

/**
 * 1. Repository 是一个空接口，即是一个标记接口
 * 2. 若我们定义的接口继承了 Repository, 则该接口会被 IOC 容器识别为一个 Repository Bean，
 * 纳入到 IOC 容器中，进而可以在该接口中定义满足一定规范的方法。
 * 3. 实际上，也可以通过 @RepositoryDefinition 注解来替代继承 Repository 接口。
 *
 * 在 Repository 子接口中声明方法
 * 1. 不是随便声明的，而需要符合一定的规范
 * 2. 查询方法以 find | read | get 开头
 * 3. 涉及条件查询时，条件的属性用条件关键字连接: 条件的属性名称与个数要与参数的位置与个数一一对应
 * 4. 要注意的是：条件属性与首字母大写
 * 5. 支持属性的级联查询。若当前类有符合条件的属性，则优先使用，而不使用级联属性。
 * 若需要使用级联属性，则属性之间使用 _ 进行连接。
 * @author xt
 * @date 2019/2/18 - 10:14
 * @description
 */
//@RepositoryDefinition(domainClass = Person.class, idClass = Integer.class)
//public interface PersonRepository extends Repository<Person, Integer> {
//public interface PersonRepository extends CrudRepository<Person, Integer> {
//public interface PersonRepository extends PagingAndSortingRepository<Person, Integer> {
//public interface PersonRepository extends JpaRepository<Person, Integer> {
public interface PersonRepository extends JpaRepository<Person, Integer>, JpaSpecificationExecutor<Person>, PersonDao {

    /**
     * 根据 lastName 来获取对应的Person
     * @param lastname
     * @return
     */
    Person getByLastName(String lastname);

    /**
     * WHERE lastName LIKE ?% AND id < ?
     */
    List<Person> getByLastNameStartingWithAndIdLessThan(String lastName, Integer id);

    /**
     * WHERE lastName LIKE %? AND id > ?
     */
    List<Person> getByLastNameEndingWithAndIdGreaterThan(String lastName, Integer id);

    /**
     * WHERE email IN (?, ?, ?) OR birth < ?
     */
    List<Person> getByEmailInOrBirthBefore(List<String> emails, Date birth);

    /**
     * WHERE address.id > ?
     */
    List<Person> getByAddress_IdGreaterThan(Integer id);

    /**
     * 查询 id 值最大的那个 Person
     * 使用 @Query 注解可以自定义 JPQL 语句以实现更灵活的查询
     */
    @Query("select p from Person p where p.id = (select max(id) from Person)")
    Person getMaxIdPerson();

    /**
     * 为 @Query 注解传递参数的方式1: 使用占位符
     */
    @Query("select p from Person p where p.lastName = ?1 and p.email = ?2")
    List<Person> testQueryAnnotionParam1(String lastName, String email);

    /**
     * 为 @Query 注解传递参数的方式2: 命名参数的方式
     */
    @Query("select p from Person p where p.lastName = :lastName and p.email =:email")
    List<Person> testQueryAnnotionParam2(@Param("email") String email, @Param("lastName") String lastName);

    /**
     *SpringData 允许在占位符上添加%%
     */
    @Query("select p from Person p where p.lastName like %?1% and p.email like %?2%")
    List<Person> testQueryAnnotionLikeParam1(String lastName, String email);

    /**
     *SpringData 允许在命名参数上添加%%
     */
    @Query("select p from Person p where p.lastName like %:lastName% and p.email like %:email%")
    List<Person> testQueryAnnotionLikeParam2(@Param("email") String email, @Param("lastName") String lastName);

    /**
     * 设置 nativeQuery=true 即可以使用原生的 SQL 查询
     */
    @Query(value = "select count(id) from jpa_persons", nativeQuery = true)
    long getTotalCount();

    /**
     * 可以通过自定义的 JPQL 完成 UPDATE 和 DELETE 操作，注意：JPQL 不支持使用 INSERT
     * 在 @Query 注解中编写 JPQL 语句，但必须使用 @Modifying 进行修饰，以通知 SpringData，这是一个 UPDATE 或 DELETE 操作
     * UPDATE 或 DELETE 操作需要使用事务，此时需要定义 Service 层，在 Service 层的方法上添加事务操作。
     * 默认情况下，SpringData 的每个方法上有事务，但都是一个只读事务，它们不能完成修改操作!
     */
    @Modifying
    @Query("update Person p set p.email = :email where p.id = :id")
    void updatePersonEmail(@Param("email") String email, @Param("id") Integer id);
}
