package test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunlei on 2017/6/14.
 */
public class School {
    private String name;
    private String address;
    private List<Student> students;

    private int i=92225;

    public School() {
        this.name = "shawn";
        this.address = "洛川路16号";
        List<Student> students = new ArrayList<Student>();
        Student student1 = new Student();
        Student student2 = new Student();
        students.add(student1);
        students.add(student2);
        this.students = students;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }
}
