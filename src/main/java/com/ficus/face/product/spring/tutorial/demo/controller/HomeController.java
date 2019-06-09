package com.ficus.face.product.spring.tutorial.demo.controller;

import com.ficus.face.product.spring.tutorial.demo.pojo.ErrorMessage;
import com.ficus.face.product.spring.tutorial.demo.pojo.Score;
import com.ficus.face.product.spring.tutorial.demo.pojo.Student;
import com.ficus.face.product.spring.tutorial.demo.pojo.Subject;
import com.ficus.face.product.spring.tutorial.demo.repository.ScoreRepository;
import com.ficus.face.product.spring.tutorial.demo.repository.StudentRepository;
import com.ficus.face.product.spring.tutorial.demo.repository.SubjectRepository;
import com.ficus.face.product.spring.tutorial.demo.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
public class HomeController {

    private final StudentRepository studentRepository;

    private final SubjectRepository subjectRepository;

    private final ScoreRepository scoreRepository;

    private final ScoreService scoreService;

    @Autowired
    public HomeController(StudentRepository studentRepository, SubjectRepository subjectRepository, ScoreRepository scoreRepository, ScoreService scoreService) {
        this.studentRepository = studentRepository;
        this.subjectRepository = subjectRepository;
        this.scoreRepository = scoreRepository;
        this.scoreService = scoreService;
    }

    @GetMapping(value = "/student/{id}", produces = "application/json")
    public ResponseEntity getStudent(@PathVariable("id") String id) {
        Student student = studentRepository.findById(id).orElse(null);
        if (student == null)
            return studentNotExist(id);
        Map<String, Integer> scores = scoreService.studentScores(id);
        if (scores == null)
            return studentEmptyScore(student);
        return new ResponseEntity<>(scores, HttpStatus.OK);
    }

    @PostMapping(value = "/student", produces = "application/json")
    public ResponseEntity addStudent(@RequestBody @Valid Student student) {
        Student s = studentRepository.findById(student.getId()).orElse(null);
        if (s != null)
            return studentExist(s);
        studentRepository.save(student);
        return new ResponseEntity<>(student, HttpStatus.OK);
    }

    @DeleteMapping(value = "/student/{id}", produces = "application/json")
    public ResponseEntity deleteStudent(@PathVariable("id") String id) {
        if (!studentRepository.existsById(id))
            return studentNotExist(id);
        scoreService.deleteStudent(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping(value = "/subject/{id}", produces = "application/json")
    public ResponseEntity getSubject(@PathVariable("id") String id) {
        Subject subject = subjectRepository.findById(id).orElse(null);
        if (subject == null)
            return subjectNotExist(id);
        Map<String, Integer> scores = scoreService.subjectScores(id);
        if (scores == null)
            return subjectEmptyScore(subject);
        return new ResponseEntity<>(scores, HttpStatus.OK);
    }

    @PostMapping(value = "/subject", produces = "application/json")
    public ResponseEntity addSubject(@RequestBody @Valid Subject subject) {
        Subject s = subjectRepository.findById(subject.getId()).orElse(null);
        if (s != null)
            return subjectExist(s);
        subjectRepository.save(subject);
        return new ResponseEntity<>(subject, HttpStatus.OK);
    }

    @DeleteMapping(value = "/subject/{id}", produces = "application/json")
    public ResponseEntity deleteSubject(@PathVariable("id") String id) {
        if (!subjectRepository.existsById(id))
            return subjectNotExist(id);
        scoreService.deleteSubject(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping(value = "/score", produces = "application/json")
    public ResponseEntity addScore(@RequestBody @Valid Score score) {
        Subject subject = subjectRepository.findById(score.getSubjectId()).orElse(null);
        if (subject == null)
            return subjectNotExist(score.getSubjectId());
        Student student = studentRepository.findById(score.getStudentId()).orElse(null);
        if (student == null)
            return subjectNotExist(score.getStudentId());
        if (scoreRepository.existsBySubjectIdAndStudentId(score.getSubjectId(), score.getStudentId()))
            return scoreExist(subject, student);
        Score s = scoreService.addScore(score);
        return new ResponseEntity<>(s, HttpStatus.OK);
    }

    @PostMapping(value = "/batchAdd/{id}", produces = "application/json")
    public ResponseEntity batchAdd(@PathVariable("id") String subjectId, @RequestBody Map<String, Integer> scores) {
        if (!subjectRepository.existsById(subjectId))
            return subjectNotExist(subjectId);
        for (String studentId : scores.keySet()) {
            if (!studentRepository.existsById(studentId))
                return studentNotExist(studentId);
        }
        Subject subject = subjectRepository.findById(subjectId).orElse(null);
        for (Score s : subject.getScores()) {
            System.out.println(s.getStudentId());
            if (scores.containsKey(s.getStudentId()))
                return scoreExist(subject, studentRepository.findById(s.getStudentId()).orElse(null));
        }
        Map<String, Integer> s = scoreService.batchAddScore(subjectId, scores);
        return new ResponseEntity<>(s, HttpStatus.OK);
    }

    private ResponseEntity studentNotExist(String id) {
        ErrorMessage err = new ErrorMessage();
        err.setError("404");
        err.setMessage("Student Id: " + id + " Not Exist");
        return new ResponseEntity<>(err, HttpStatus.OK);
    }

    private ResponseEntity studentExist(Student student) {
        ErrorMessage err = new ErrorMessage();
        err.setError("409");
        err.setMessage("Student Id: " + student.getId() + " Already Existed For: " + student.getStudentName());
        return new ResponseEntity<>(err, HttpStatus.OK);
    }

    private ResponseEntity subjectNotExist(String id) {
        ErrorMessage err = new ErrorMessage();
        err.setError("404");
        err.setMessage("Subject Id: " + id + " Not Exist");
        return new ResponseEntity<>(err, HttpStatus.OK);
    }

    private ResponseEntity subjectExist(Subject subject) {
        ErrorMessage err = new ErrorMessage();
        err.setError("409");
        err.setMessage("Subject Id: " + subject.getId() + " Already Existed For: " + subject.getSubjectName());
        return new ResponseEntity<>(err, HttpStatus.OK);
    }

    private ResponseEntity scoreExist(Subject subject, Student student) {
        ErrorMessage err = new ErrorMessage();
        err.setError("409");
        err.setMessage("Score: " + subject.getSubjectName() + " For " + student.getStudentName() + " Already Existed");
        return new ResponseEntity<>(err, HttpStatus.OK);
    }

    private ResponseEntity studentEmptyScore(Student student) {
        ErrorMessage err = new ErrorMessage();
        err.setError("404");
        err.setMessage("Student: " + student.getStudentName() + " Doesn't Have Any Scores");
        return new ResponseEntity<>(err, HttpStatus.OK);
    }

    private ResponseEntity subjectEmptyScore(Subject subject) {
        ErrorMessage err = new ErrorMessage();
        err.setError("404");
        err.setMessage("Subject: " + subject.getSubjectName() + " Doesn't Have Any Scores");
        return new ResponseEntity<>(err, HttpStatus.OK);
    }

}