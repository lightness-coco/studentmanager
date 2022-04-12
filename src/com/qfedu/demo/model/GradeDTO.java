package com.qfedu.demo.model;

import java.util.List;

public class GradeDTO extends Grade{
    private List<Course> courses;

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }
}
