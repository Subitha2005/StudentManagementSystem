public class Student {
    private int id;
    private String name;
    private String roll;
    private String grade;
    private String gender;
    private int year;

    public Student() {}

    public Student(int id, String name, String roll, String grade, String gender, int year) {
        this.id = id; this.name=name; this.roll=roll; this.grade=grade; this.gender=gender; this.year=year;
    }

    public Student(String name, String roll, String grade, String gender, int year) {
        this(0,name,roll,grade,gender,year);
    }

    // getters and setters
    public int getId() {return id;} public void setId(int id) {this.id=id;}
    public String getName() {return name;} public void setName(String name){this.name=name;}
    public String getRoll(){return roll;} public void setRoll(String roll){this.roll=roll;}
    public String getGrade(){return grade;} public void setGrade(String grade){this.grade=grade;}
    public String getGender(){return gender;} public void setGender(String gender){this.gender=gender;}
    public int getYear(){return year;} public void setYear(int year){this.year=year;}
}
