import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class Course {
    private String courseName;
    private String courseID;
    private List<Student> enrolledStudents;
    private Map<Student, String> grades;
    private List<Observer> observers;
    private GradingStrategy gradingStrategy;

    public Course(String courseName,String courseID,GradingStrategy gradingStrategy) {
        this.courseName = courseName;
        this.courseID = courseID;
        this.gradingStrategy = gradingStrategy;
        this.enrolledStudents = new ArrayList<>();
        this.grades = new HashMap<>();
        this.observers = new ArrayList<>();
    }

    public String getCourseName() { return courseName; }
    public List<Student> getEnrolledStudents() { return enrolledStudents; }

    public void addStudent(Student student) {
        this.enrolledStudents.add(student);
    }
}