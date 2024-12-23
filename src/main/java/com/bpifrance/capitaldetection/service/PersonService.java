package com.bpifrance.capitaldetection.service;

import com.bpifrance.capitaldetection.dto.PersonDto;
import com.bpifrance.capitaldetection.entity.Person;
import com.bpifrance.capitaldetection.exception.ResourceNotFoundException;
import com.bpifrance.capitaldetection.repository.PersonRepository;
import org.springframework.stereotype.Service;

@Service
public class PersonService {
    private final PersonRepository personRepository;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public Person createPerson(PersonDto personDto) {
        Person person = new Person();
        person.setFirstName(personDto.firstName());
        person.setLastName(personDto.lastName());
        person.setBirthDate(personDto.birthDate());
        return personRepository.save(person);
    }

    public Person getPersonById(Long id) {
        return personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Person not found with id: " + id));
    }
}