package com.ficus.face.product.spring.tutorial.demo.repository;

import com.ficus.face.product.spring.tutorial.demo.pojo.Subject;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SubjectRepository extends MongoRepository<Subject, String> {

    public boolean existsBySubjectName(String subjectName);

    public Subject findBySubjectName(String subjectName);

    public void deleteBySubjectName(String subjectName);
}
