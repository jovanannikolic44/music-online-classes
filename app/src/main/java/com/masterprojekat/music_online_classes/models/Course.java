package com.masterprojekat.music_online_classes.models;

import java.io.Serializable;
import java.util.List;

public class Course implements Serializable {
    private int courseId;
    private User professor;
    private String name;
    private String level;
    private String instrument;
    private String description;
    private float rating;
    private float price;
    private String content;
    private String courseImage;
    private transient boolean isSelected = false;
    private List<Comment> comments;
    private int progress;
    private int numberOfClasses;
    private CourseStatus status;

    public Course() {
        this.professor = null;
        this.name = "";
        this.level = "";
        this.instrument = "";
        this.description = "";
        this.rating = 0;
        this.price = 0;
        this.content = "";
        this.courseImage = "";
        this.comments = null;
        this.isSelected = false;
        this.numberOfClasses = 0;
        this.status = CourseStatus.ZAHTEV_POSLAT;
        this.progress = 0;
    }

    public Course(User professor, String name, String level, String instrument, String description, float price, String content, int numberOfClasses) {
        this.professor = professor;
        this.name = name;
        this.level = level;
        this.instrument = instrument;
        this.description = description;
        this.rating = 0;
        this.price = price;
        this.content = content;
        this.courseImage = "";
        this.comments = null;
        this.isSelected = false;
        this.numberOfClasses = numberOfClasses;
        this.status = CourseStatus.ZAHTEV_POSLAT;
        this.progress = 0;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public User getProfessor() {
        return professor;
    }

    public void setProfessor(User professor) {
        this.professor = professor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getInstrument() {
        return instrument;
    }

    public void setInstrument(String instrument) {
        this.instrument = instrument;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getCourseImage() {
        return courseImage;
    }

    public void setCourseImage(String courseImage) {
        this.courseImage = courseImage;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getNumberOfClasses() {
        return numberOfClasses;
    }

    public void setNumberOfClasses(int numberOfClasses) {
        this.numberOfClasses = numberOfClasses;
    }
}
