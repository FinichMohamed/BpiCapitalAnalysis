package com.bpifrance.capitaldetection.repository;

import com.bpifrance.capitaldetection.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Long> {
}