package com.ficus.face.product.spring.tutorial.demo.repository;

import com.ficus.face.product.spring.tutorial.demo.pojo.Score;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ScoreRepository extends MongoRepository<Score, String> {

    public boolean existsBySubjectIdAndStudentId(String subjectId, String studentId);

    public List<Score> findByStudentId(String studentId);

    public List<Score> findBySubjectId(String subjectId);
}
