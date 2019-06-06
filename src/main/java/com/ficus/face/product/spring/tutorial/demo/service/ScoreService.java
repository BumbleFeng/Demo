package com.ficus.face.product.spring.tutorial.demo.service;

import com.ficus.face.product.spring.tutorial.demo.pojo.Score;
import com.ficus.face.product.spring.tutorial.demo.pojo.Student;
import com.ficus.face.product.spring.tutorial.demo.pojo.Subject;
import com.ficus.face.product.spring.tutorial.demo.repository.ScoreRepository;
import com.ficus.face.product.spring.tutorial.demo.repository.StudentRepository;
import com.ficus.face.product.spring.tutorial.demo.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ScoreService {

    private final StudentRepository studentRepository;

    private final SubjectRepository subjectRepository;

    private final ScoreRepository scoreRepository;

    @Autowired
    public ScoreService(StudentRepository studentRepository, SubjectRepository subjectRepository, ScoreRepository scoreRepository){
        this.studentRepository = studentRepository;
        this.subjectRepository = subjectRepository;
        this.scoreRepository = scoreRepository;
    }

    public void deleteStudent(String studentName){
        studentRepository.deleteByStudentName(studentName);
        List<Score> scores = scoreRepository.findByStudentName(studentName);
        for(Score s:scores){
            Subject subject = subjectRepository.findBySubjectName(s.getSubjectName());
            for(Score sc : subject.getScores()){
                if(studentName.equals(sc.getStudentName())) {
                    subject.getScores().remove(sc);
                }
            }
            subjectRepository.save(subject);
        }
        scoreRepository.deleteAll(scores);
    }

    public void deleteSubject(String subjectName){
        subjectRepository.deleteBySubjectName(subjectName);
        List<Score> scores = scoreRepository.findBySubjectName(subjectName);
        for(Score s:scores){
            Student student = studentRepository.findByStudentName(s.getStudentName());
            for(Score sc : student.getScores()){
                if(subjectName.equals(sc.getSubjectName())) {
                    student.getScores().remove(sc);
                }
            }
            studentRepository.save(student);
        }
        scoreRepository.deleteAll(scores);
    }

    public Score addScore(Score score){
        scoreRepository.save(score);
        Subject subject = subjectRepository.findBySubjectName(score.getSubjectName());
        Student student = studentRepository.findByStudentName(score.getStudentName());
        subject.getScores().add(score);
        student.getScores().add(score);
        subjectRepository.save(subject);
        studentRepository.save(student);
        return score;
    }

    public Map<String,Integer> batchAddScore(String subjectName, Map<String,Integer> scores){
        Subject subject = subjectRepository.findBySubjectName(subjectName);
        for(Map.Entry<String, Integer> entry:scores.entrySet()){
            Score s = new Score(subjectName,entry.getKey(),entry.getValue());
            scoreRepository.save(s);
            Student student = studentRepository.findByStudentName(entry.getKey());
            subject.getScores().add(s);
            student.getScores().add(s);
            studentRepository.save(student);
        }
        subjectRepository.save(subject);
        return subjectScores(subjectName);
    }

    public Map<String,Integer> studentScores(String studentName){
        Student student = studentRepository.findByStudentName(studentName);
        TreeMap<String,Integer> scores = new TreeMap<>();
        for(Score s:student.getScores()){
            scores.put(s.getSubjectName(),s.getScore());
        }
        return scores;
    }

    public Map<String,Integer> subjectScores(String subjectName){
        Subject subject = subjectRepository.findBySubjectName(subjectName);
        TreeMap<Integer,String> treeMap = new TreeMap<>(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return  o2.compareTo(o1);
            }
        });
        Integer average = 0;
        for(Score s:subject.getScores()){
            treeMap.put(s.getScore(),s.getStudentName());
            average += s.getScore();
        }
        average /= subject.getScores().size();
        LinkedHashMap<String,Integer> scores = new LinkedHashMap<>();
        for(Map.Entry<Integer,String> entry:treeMap.entrySet()){
            scores.put(entry.getValue(),entry.getKey());
        }
        scores.put("MIN",treeMap.lastKey());
        scores.put("MAX",treeMap.firstKey());
        scores.put("AVG",average);
        return scores;
    }
}
