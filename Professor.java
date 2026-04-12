public class Professor extends User {
    private String department;
    
    public Professor(String name, String id, String department) {
        super(name, id);
        this.department = department;
    }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
}
