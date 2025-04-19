package com.masterprojekat.music_online_classes.models;

import java.time.LocalDate;
import java.time.LocalTime;

public class Term {
    private int termId;
    private LocalDate date;
    private LocalTime time;
    private User professor;
    private User student;
    private TermStatus status;

    public int getTermId() {
        return termId;
    }

    public void setTermId(int termId) {
        this.termId = termId;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
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
}
