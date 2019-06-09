package com.ficus.face.product.spring.tutorial.demo.service;

import com.ficus.face.product.spring.tutorial.demo.pojo.Score;
import com.ficus.face.product.spring.tutorial.demo.pojo.Student;
import com.ficus.face.product.spring.tutorial.demo.pojo.Subject;
import com.ficus.face.product.spring.tutorial.demo.repository.ScoreRepository;
import com.ficus.face.product.spring.tutorial.demo.repository.StudentRepository;
import com.ficus.face.product.spring.tutorial.demo.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class ScoreService {

    private final StudentRepository studentRepository;

    private final SubjectRepository subjectRepository;

    private final ScoreRepository scoreRepository;

    @Autowired
    public ScoreService(StudentRepository studentRepository, SubjectRepository subjectRepository, ScoreRepository scoreRepository) {
        this.studentRepository = studentRepository;
        this.subjectRepository = subjectRepository;
        this.scoreRepository = scoreRepository;
    }


    public void deleteStudent(String studentId) {
        studentRepository.deleteById(studentId);
        List<Score> scores = scoreRepository.findByStudentId(studentId);
        for (Score s : scores) {
            Subject subject = subjectRepository.findById(s.getSubjectId()).orElse(null);
            Iterator<Score> iterator = subject.getScores().iterator();
            while(iterator.hasNext()){
                Score sc = iterator.next();
                if(studentId.equals(sc.getStudentId()))
                    iterator.remove();
            }
            subjectRepository.save(subject);
        }
        scoreRepository.deleteAll(scores);
    }

    public void deleteSubject(String subjectId) {
        subjectRepository.deleteById(subjectId);
        List<Score> scores = scoreRepository.findBySubjectId(subjectId);
        for (Score s : scores) {
            Student student = studentRepository.findById(s.getStudentId()).orElse(null);
            Iterator<Score> iterator = student.getScores().iterator();
            while(iterator.hasNext()){
                Score sc = iterator.next();
                if(subjectId.equals(sc.getSubjectId()))
                    iterator.remove();
            }
            studentRepository.save(student);
        }
        scoreRepository.deleteAll(scores);
    }

    public Score addScore(Score score) {
        Subject subject = subjectRepository.findById(score.getSubjectId()).orElse(null);
        Student student = studentRepository.findById(score.getStudentId()).orElse(null);
        score.setSubjectName(subject.getSubjectName());
        score.setStudentName(student.getStudentName());
        scoreRepository.save(score);
        subject.getScores().add(score);
        student.getScores().add(score);
        subjectRepository.save(subject);
        studentRepository.save(student);
        return score;
    }

    public Map<String, Integer> batchAddScore(String subjectId, Map<String, Integer> scores) {
        Subject subject = subjectRepository.findById(subjectId).orElse(null);
        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
            Student student = studentRepository.findById(entry.getKey()).orElse(null);
            Score s = new Score(subjectId, subject.getSubjectName(), student.getId(), student.getStudentName(), entry.getValue());
            scoreRepository.save(s);
            subject.getScores().add(s);
            student.getScores().add(s);
            studentRepository.save(student);
        }
        subjectRepository.save(subject);
        return subjectScores(subjectId);
    }

    public Map<String, Integer> studentScores(String studentId) {
        Student student = studentRepository.findById(studentId).orElse(null);
        if (student.getScores().isEmpty())
            return null;
        TreeMap<String, Integer> scores = new TreeMap<>();
        for (Score s : student.getScores()) {
            scores.put(s.getSubjectName(), s.getScore());
        }
        return scores;
    }

    public Map<String, Integer> subjectScores(String subjectId) {
        Subject subject = subjectRepository.findById(subjectId).orElse(null);
        if (subject.getScores().isEmpty())
            return null;
        TreeMap<Integer, String> treeMap = new TreeMap<>(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o2.compareTo(o1);
            }
        });
        Integer average = 0;
        for (Score s : subject.getScores()) {
            treeMap.put(s.getScore(), s.getStudentName());
            average += s.getScore();
        }
        average /= subject.getScores().size();
        LinkedHashMap<String, Integer> scores = new LinkedHashMap<>();
        for (Map.Entry<Integer, String> entry : treeMap.entrySet()) {
            scores.put(entry.getValue(), entry.getKey());
        }
        scores.put("MIN", treeMap.lastKey());
        scores.put("MAX", treeMap.firstKey());
        scores.put("AVG", average);
        return scores;
    }

}
