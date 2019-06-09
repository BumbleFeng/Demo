package com.ficus.face.product.spring.tutorial.demo.repository;

import com.ficus.face.product.spring.tutorial.demo.pojo.Student;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StudentRepository extends MongoRepository<Student, String> { }
