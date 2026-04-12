public class Student extends User {
    private String major;
    private boolean hasScholarship;

    private Student(StudentBuilder builder) {
        super(builder.name, builder.id);
        this.major = builder.major;
        this.hasScholarship = builder.hasScholarship;
    }

    public String getMajor() { return major; }
    public boolean hasScholarship() { return hasScholarship; }

    public static class StudentBuilder {
        protected String name;
        protected String id;

        private String major = "Undeclared";
        private boolean hasScholarship = false;

        public StudentBuilder(String name, String id) {
            this.name = name;
            this.id = id;
        }

        public StudentBuilder setMajor(String major) {
            this.major = major;
            return this;
        }

        public StudentBuilder setScholarship(boolean hasScholarship) {
            this.hasScholarship = hasScholarship;
            return this;
        }

        public Student build() {
            return new Student(this);
        }
    }
}
