package com.xt.springdata;

import com.xt.springdata.entity.Person;
import com.xt.springdata.repository.PersonRepository;
import com.xt.springdata.service.PersonService;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author xt
 * @date 2019/2/18 - 9:34
 * @description
 */
public class SpringDataTest {

    private ApplicationContext ctx = null;
    private PersonRepository personRepository = null;
    private PersonService personService;

    {
        ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
        personRepository = ctx.getBean(PersonRepository.class);
        personService = ctx.getBean(PersonService.class);
    }

    @Test
    public void testPersonRepositoryMethod() {
        personRepository.test();
    }

    /**
     * 目标: 实现带查询条件的分页。 id > 5 的条件
     * 调用 JpaSpecificationExecutor 的 Page<T> findAll(Specification<T> spec, Pageable pageable);
     * Specification: 封装了 JPA Criteria 查询的查询条件
     * Pageable: 封装了请求分页的信息: 例如 pageNo, pageSize, Sort
     */
    @Test
    public void testJpaSpecificationExecutor() {
        int pageNo = 3-1;
        int pageSize = 5;
        PageRequest pageable = new PageRequest(pageNo, pageSize);

        // 通常使用 Specification 的匿名内部类
        Specification specification = new Specification() {
            /**
             *
             * @param *root: 代表查询的实体类
             * @param criteriaQuery: 可以从中得到 Root 对象，即告知 JPA Criteria 查询要查询哪一个实体类，
             *                     还可以来添加查询条件，还可以结合 EntityManager 对象得到最终查询的 TypedQuery 对象。
             * @param *criteriaBuilder: 对象，用于创建 Criteria 相关对象的工厂。当然可以从中获取到 Predicate 对象
             * @return *Predicate 类型，代表一个查询条件。
             */
            @Override
            public Predicate toPredicate(Root root, CriteriaQuery criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Path path = root.get("id");
                Predicate predicate = criteriaBuilder.gt(path, 5);
                return predicate;
            }
        };

        Page page = personRepository.findAll(specification, pageable);

        System.out.println("总记录数: " + page.getTotalElements());
        System.out.println("总页数: " + page.getTotalPages());
        System.out.println("当前第几页: " + (page.getNumber() + 1));
        System.out.println("当前页面的List: " + page.getContent());
        System.out.println("当前页面的记录数: " + page.getNumberOfElements());
    }

    @Test
    public void testJpaRepository() {
        Person person = new Person();
        person.setLastName("XYZ");
        person.setEmail("xyz@163.com");
        person.setBirth(new Date());
        person.setId(28);

        Person person2 = personRepository.saveAndFlush(person);
        System.out.println(person == person2); // false
    }

    @Test
    public void testPagingAndSortingRepository() {
        // pageNo 从 0 开始
        int pageNo = 6 - 1;
        int pageSize = 5;

        // Pageable 接口通常使用其 PageRequest 实现类。其中封装了需要分页的信息
        // 排序相关 Sort 封装了排序的信息
        // Order 是具体针对于某一个属性进行升序还是降序
        Sort.Order order1 = new Sort.Order(Sort.Direction.DESC, "id");
        Sort.Order order2 = new Sort.Order(Sort.Direction.ASC, "email");
        Sort sort = new Sort(order1, order2);
//        Pageable pageable = new PageRequest(pageNo, pageSize);
        Pageable pageable = new PageRequest(pageNo, pageSize, sort);
        Page<Person> page = personRepository.findAll(pageable);

        System.out.println("总记录数: " + page.getTotalElements());
        System.out.println("当前第几页: " + (page.getNumber() + 1));
        System.out.println("总页数:" + page.getTotalPages());
        System.out.println("当前页面的List: " + page.getContent());
        System.out.println("当前页面的记录数: " + page.getNumberOfElements());
    }

    
    @Test
    public void testCrudRepository() {
        List<Person> personList = new ArrayList<>();

        for(int i='a'; i<='z'; i++) {
            Person p = new Person();
            p.setAddressId(i+1);
            p.setBirth(new Date());
            p.setEmail((char)i+""+(char)i+"@163.com");
            p.setLastName((char)i + "" + (char)i);
            personList.add(p);
        }

        personService.savePersons(personList);
    }

    @Test
    public void testModifying() {
        personService.updatePersonEmail("abc@163.com", 1);
    }

    @Test
    public void testNativeQuery() {
        long count = personRepository.getTotalCount();
        System.out.println(count);
    }

    @Test
    public void testQueryAnnotationLikeParam2() {
        List<Person> persons = personRepository.testQueryAnnotionLikeParam2("b", "B");
        System.out.println(persons);
    }

    @Test
    public void testQueryAnnotationLikeParam1() {
        List<Person> persons = personRepository.testQueryAnnotionLikeParam1("A", "a");
        System.out.println(persons);
    }

    @Test
    public void testQueryAnnotationParam2() {
        List<Person> persons = personRepository.testQueryAnnotionParam2("aa@163.com", "AA");
        System.out.println(persons);
    }

    @Test
    public void testQueryAnnotationParam1() {
        List<Person> persons = personRepository.testQueryAnnotionParam1("AA", "aa@163.com");
        System.out.println(persons);
    }

    @Test
    public void testQueryAnnotation() {
        Person person = personRepository.getMaxIdPerson();
        System.out.println(person);
    }

    @Test
    public void testKeyWords2() {
        List<Person> persons = personRepository.getByAddress_IdGreaterThan(0);
        System.out.println(persons);
    }

    @Test
    public void testKeyWords() {
        List<Person> personList = personRepository.getByLastNameStartingWithAndIdLessThan("A", 3);
        System.out.println(personList);

        List<Person> personList1 = personRepository.getByLastNameEndingWithAndIdGreaterThan("B", 1);
        System.out.println(personList1);

        List<Person> persons = personRepository.getByEmailInOrBirthBefore(Arrays.asList("aa@163.com", "ab@163.com"), new Date());
        System.out.println(persons);
    }

    @Test
    public void testHelloWorldSpringData() {

        System.out.println(personRepository.getClass().getName());

        Person person = personRepository.getByLastName("AA");
        System.out.println(person);
    }

    @Test
    public void testJPA() {

    }

    @Test
    public void testDataSource() throws Exception {
        DataSource dataSource = ctx.getBean(DataSource.class);
        System.out.println(dataSource.getConnection());
    }


}
