package com.xt.springdata.service;

import com.xt.springdata.entity.Person;
import com.xt.springdata.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author xt
 * @date 2019/2/18 - 17:16
 * @description
 */
@Service
public class PersonService {

    @Autowired
    private PersonRepository personRepository;

    @Transactional
    public void savePersons(List<Person> personList) {
        personRepository.save(personList);
    }

    @Transactional
    public void updatePersonEmail(String email, Integer id) {
        personRepository.updatePersonEmail(email, id);
    }
}
