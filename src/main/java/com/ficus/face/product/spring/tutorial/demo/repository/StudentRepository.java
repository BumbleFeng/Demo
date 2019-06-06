package com.ficus.face.product.spring.tutorial.demo.repository;

import com.ficus.face.product.spring.tutorial.demo.pojo.Student;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StudentRepository extends MongoRepository<Student, String> {

    public boolean existsByStudentName(String studentName);

    public Student findByStudentName(String studentName);

    public void deleteByStudentName(String studentName);
}
