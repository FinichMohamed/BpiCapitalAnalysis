package com.bpifrance.capitaldetection.controller;

import com.bpifrance.capitaldetection.dto.PersonDto;
import com.bpifrance.capitaldetection.entity.Person;
import com.bpifrance.capitaldetection.service.PersonService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/persons")
public class PersonController {
    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    // POST /api/persons : Créer une nouvelle personne physique
    @PostMapping
    public ResponseEntity<Void> createPerson(@Valid @RequestBody PersonDto personDto) {
        Person person = personService.createPerson(personDto);
        return ResponseEntity.created(URI.create("/api/persons/" + person.getId())).build();
    }

    // GET /api/persons/{id} : Récupérer une personne physique
    @GetMapping("/{id}")
    public ResponseEntity<Person> getPerson(@PathVariable Long id) {
        Person person = personService.getPersonById(id);
        return ResponseEntity.ok(person);
    }
}