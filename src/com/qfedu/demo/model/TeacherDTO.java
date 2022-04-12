package com.qfedu.demo.model;

import java.util.List;
import java.util.Map;

public class TeacherDTO extends Teacher{
    /**
     * Teacher t = new Teacher();
     * t.setName("张三");
     * t.setXXX
     *
     * List<Map<String,Object>> courses = new ArrayList();
     * Map<String,Object> map = new HashMap();
     * map.put("grade",new Grade(1,"五年级"));
     * map.put("clazz",new Clazz(2,"五年级1班"));
     * map.put("course",new Course(3,"英语"));
     * courses.add(map);
     */
    private List<Map<String,Object>> courses;

    public List<Map<String, Object>> getCourses() {
        return courses;
    }

    public void setCourses(List<Map<String, Object>> courses) {
        this.courses = courses;
    }

    private List<String> course;

    public List<String> getCourse() {
        return course;
    }

    public void setCourse(List<String> course) {
        this.course = course;
    }
}
