import java.sql.*;
import java.util.*;

public class StudentDAO {
    public void addStudent(Student s) throws SQLException {
        String sql = "INSERT INTO students (name, roll_number, grade, gender, year) VALUES (?,?,?,?,?)";
        try(Connection conn=DbUtil.getConnection();
            PreparedStatement ps=conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            ps.setString(1,s.getName());
            ps.setString(2,s.getRoll());
            ps.setString(3,s.getGrade());
            ps.setString(4,s.getGender());
            ps.setInt(5,s.getYear());
            ps.executeUpdate();
            try(ResultSet rs=ps.getGeneratedKeys()){ 
                if(rs.next()) s.setId(rs.getInt(1)); 
            }
        }
    }

    public void updateStudent(Student s) throws SQLException {
        String sql = "UPDATE students SET name=?, roll_number=?, grade=?, gender=?, year=? WHERE id=?";
        try(Connection conn=DbUtil.getConnection();
            PreparedStatement ps=conn.prepareStatement(sql)){
            ps.setString(1,s.getName()); ps.setString(2,s.getRoll());
            ps.setString(3,s.getGrade()); ps.setString(4,s.getGender());
            ps.setInt(5,s.getYear()); ps.setInt(6,s.getId());
            ps.executeUpdate();
        }
    }

    public void deleteStudent(int id) throws SQLException {
        String sql = "DELETE FROM students WHERE id=?";
        try(Connection conn=DbUtil.getConnection();
            PreparedStatement ps=conn.prepareStatement(sql)){
            ps.setInt(1,id); ps.executeUpdate();
        }
    }

    public List<Student> getAllStudents() throws SQLException {
        List<Student> list=new ArrayList<>();
        String sql = "SELECT id,name,roll_number,grade,gender,year FROM students ORDER BY name";
        try(Connection conn=DbUtil.getConnection();
            Statement st=conn.createStatement();
            ResultSet rs=st.executeQuery(sql)){
            while(rs.next()){
                list.add(new Student(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("roll_number"),
                    rs.getString("grade"),
                    rs.getString("gender"),
                    rs.getInt("year")
                ));
            }
        }
        return list;
    }

    public Map<Integer,Integer> getStudentsByYear() throws SQLException {
        Map<Integer,Integer> map=new TreeMap<>();
        String sql="SELECT year, COUNT(*) cnt FROM students GROUP BY year";
        try(Connection conn=DbUtil.getConnection();
            Statement st=conn.createStatement();
            ResultSet rs=st.executeQuery(sql)){
            while(rs.next()) 
                map.put(rs.getInt("year"), rs.getInt("cnt"));
        }
        return map;
    }

    public Map<String,Integer> getGenderCounts() throws SQLException {
        Map<String,Integer> map=new HashMap<>();
        String sql="SELECT gender, COUNT(*) cnt FROM students GROUP BY gender";
        try(Connection conn=DbUtil.getConnection();
            Statement st=conn.createStatement();
            ResultSet rs=st.executeQuery(sql)){
            while(rs.next()) 
                map.put(rs.getString("gender"), rs.getInt("cnt"));
        }
        return map;
    }
}
