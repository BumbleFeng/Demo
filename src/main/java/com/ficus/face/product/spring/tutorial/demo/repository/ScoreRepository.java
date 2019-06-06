package com.ficus.face.product.spring.tutorial.demo.repository;

import com.ficus.face.product.spring.tutorial.demo.pojo.Score;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ScoreRepository extends MongoRepository<Score, String> {

    public boolean existsBySubjectNameAndStudentName(String subjectName, String studentName);

    public List<Score> findByStudentName(String studentName);

    public List<Score> findBySubjectName(String subjectName);

    public void deleteByStudentName(String studentName);

    public void deleteBySubjectName(String subjectName);
}
