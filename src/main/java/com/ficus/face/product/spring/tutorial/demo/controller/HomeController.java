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

    @GetMapping(value = "/", produces = "application/json")
    public String index() {
        return "abc";
    }

    @GetMapping(value = "/student", produces = "application/json")
    public ResponseEntity getStudent(@PathVariable("name") String studentName) {
        if (!studentRepository.existsByStudentName(studentName))
            return studentNotExist(studentName);
        Map<String, Integer> scores = scoreService.studentScores(studentName);
        return new ResponseEntity<>(scores, HttpStatus.OK);
    }

    @PostMapping(value = "/student", produces = "application/json")
    public ResponseEntity addStudent(@PathVariable("name") String name) {
        if (studentRepository.existsByStudentName(name))
            return studentExist(name);
        Student student = new Student(name);
        studentRepository.save(student);
        return new ResponseEntity<>(student, HttpStatus.OK);
    }

    @DeleteMapping(value = "/student", produces = "application/json")
    public ResponseEntity deleteStudent(@PathVariable("name") String name) {
        if (!studentRepository.existsByStudentName(name))
            return studentNotExist(name);
        scoreService.deleteStudent(name);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping(value = "/student", produces = "application/json")
    public ResponseEntity getSubject(@PathVariable("name") String name) {
        if (!subjectRepository.existsBySubjectName(name))
            return subjectNotExist(name);
        Map<String, Integer> scores = scoreService.subjectScores(name);
        return new ResponseEntity<>(scores, HttpStatus.OK);
    }

    @PostMapping(value = "/subject", produces = "application/json")
    public ResponseEntity addSubject(@PathVariable("name") String name) {
        if (subjectRepository.existsBySubjectName(name))
            return subjectExist(name);
        Subject subject = new Subject(name);
        subjectRepository.save(subject);
        return new ResponseEntity<>(subject, HttpStatus.OK);
    }

    @DeleteMapping(value = "/subject", produces = "application/json")
    public ResponseEntity deleteSubject(@PathVariable("name") String name) {
        if (!subjectRepository.existsBySubjectName(name))
            return subjectNotExist(name);
        scoreService.deleteSubject(name);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping(value = "/score", produces = "application/json")
    public ResponseEntity addScore(@RequestBody Score score) {
        if (!subjectRepository.existsBySubjectName(score.getSubjectName()))
            return subjectNotExist(score.getSubjectName());
        if (!studentRepository.existsByStudentName(score.getStudentName()))
            return studentNotExist(score.getStudentName());
        if (scoreRepository.existsBySubjectNameAndStudentName(score.getSubjectName(), score.getStudentName()))
            return scoreExist(score.getSubjectName(), score.getSubjectName());
        Score s = scoreService.addScore(score);
        return new ResponseEntity<>(s, HttpStatus.OK);
    }

    @PostMapping(value = "/batchAdd", produces = "application/json")
    public ResponseEntity batchAdd(@PathVariable("name") String subjectName, @RequestBody Map<String, Integer> scores) {
        if (!subjectRepository.existsBySubjectName(subjectName))
            return subjectNotExist(subjectName);
        for (String studentName : scores.keySet()) {
            if (!studentRepository.existsByStudentName(studentName))
                return studentNotExist(studentName);
        }
        Subject subject = subjectRepository.findBySubjectName(subjectName);
        for (Score s : subject.getScores()) {
            if (scores.containsKey(s.getStudentName()))
                return scoreExist(subjectName, s.getStudentName());
        }
        Map<String, Integer> s = scoreService.batchAddScore(subjectName, scores);
        return new ResponseEntity<>(s, HttpStatus.OK);
    }

    public ResponseEntity studentNotExist(String name) {
        ErrorMessage err = new ErrorMessage();
        err.setError("404");
        err.setMessage("Student Name: " + name + " Not Exist");
        return new ResponseEntity<>(err, HttpStatus.OK);
    }

    public ResponseEntity studentExist(String name) {
        ErrorMessage err = new ErrorMessage();
        err.setError("409");
        err.setMessage("Student Name: " + name + "Already Existed");
        return new ResponseEntity<>(err, HttpStatus.OK);
    }

    public ResponseEntity subjectNotExist(String name) {
        ErrorMessage err = new ErrorMessage();
        err.setError("404");
        err.setMessage("Subject Name: " + name + "Not Exist");
        return new ResponseEntity<>(err, HttpStatus.OK);
    }

    public ResponseEntity subjectExist(String name) {
        ErrorMessage err = new ErrorMessage();
        err.setError("409");
        err.setMessage("Subject Name: " + name + "Already Existed");
        return new ResponseEntity<>(err, HttpStatus.OK);
    }

    public ResponseEntity scoreExist(String subjectName, String studentName) {
        ErrorMessage err = new ErrorMessage();
        err.setError("409");
        err.setMessage("Score: " + subjectName + " - " + studentName + "Already Existed");
        return new ResponseEntity<>(err, HttpStatus.OK);
    }

}