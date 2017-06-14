package test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunlei on 2017/6/12.
 */
public class TestBean {
    private String name;
    private Integer age;
    private Boolean isStudent;
    private List<String> hobbies;


    public TestBean() {
        this.name = "shawn";
        this.age = 12;
        this.isStudent = true;
        List<String> hobbies = new ArrayList<String>();
        hobbies.add("reading");
        hobbies.add("writing");
        this.hobbies = hobbies;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public List<String> getHobbies() {
        return hobbies;
    }

    public void setHobbies(List<String> hobbies) {
        this.hobbies = hobbies;
    }

    public Boolean getStudent() {
        return isStudent;
    }

    public void setStudent(Boolean student) {
        isStudent = student;
    }
}
