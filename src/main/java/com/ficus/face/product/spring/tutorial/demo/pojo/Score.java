package com.ficus.face.product.spring.tutorial.demo.pojo;

import org.hibernate.validator.constraints.Range;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;

@Document
public class Score {

    @Id
    private String id;
    @NotBlank
    private String subjectId;
    private String subjectName;
    @NotBlank
    private String studentId;
    private String studentName;
    @Range(min = 0, max = 100)
    private Integer score;

    public Score() {
    }

    public Score(@NotBlank String subjectId, String subjectName, @NotBlank String studentId, String studentName, @Range(min = 0, max = 100) Integer score) {
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.studentId = studentId;
        this.studentName = studentName;
        this.score = score;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
}
