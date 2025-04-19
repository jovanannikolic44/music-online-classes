package com.masterprojekat.music_online_classes.models;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Term implements Serializable {
    private int termId;
    private String date;
    private String time;
    private User professor;
    private User student;
    private Course course;
    private TermStatus status;

    public int getTermId() {
        return termId;
    }

    public void setTermId(int termId) {
        this.termId = termId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public User getProfessor() {
        return professor;
    }

    public void setProfessor(User professor) {
        this.professor = professor;
    }

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
    }

    public TermStatus getStatus() {
        return status;
    }

    public void setStatus(TermStatus status) {
        this.status = status;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    @NonNull
    @Override
    public String toString() {
        return date + " " + time;
    }
}
