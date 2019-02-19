package com.xt.springdata.dao.impl;

import com.xt.springdata.dao.PersonDao;
import com.xt.springdata.entity.Person;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * @author xt
 * @date 2019/2/19 - 11:27
 * @description
 */
public class PersonRepositoryImpl implements PersonDao{

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void test() {
        Person person = entityManager.find(Person.class, 10);
        System.out.println("-->" + person);
    }
}
