package com.masterprojekat.music_online_classes.models;

import android.os.Build;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Notification {
    private int notificationId;
    private String message;
    @SerializedName("createdAt")
    private String dateAndTime;
    private User student;

    private User professor;

    private Course course;

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDateAndTime() {
        return dateAndTime;
    }

    public void setDateAndTime(String dateAndTime) {
        this.dateAndTime = dateAndTime;
    }

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
    }

    public User getProfessor() {
        return professor;
    }

    public void setProfessor(User professor) {
        this.professor = professor;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public String getDate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDateTime dateTime = LocalDateTime.parse(dateAndTime);
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
            return dateTime.format(dateFormat);
        }
        return "";
    }

    public String getTime() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDateTime dateTime = LocalDateTime.parse(dateAndTime);
            DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");
            return dateTime.format(timeFormat);
        }
        return "";
    }
}
