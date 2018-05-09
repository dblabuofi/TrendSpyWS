/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xin.ui.cpptutor.dao;

import com.google.gson.Gson;
import com.google.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.joda.time.DateTime;

/**
 *
 * @author jupiter
 */
public class MySQLCon {

    @Inject
    Gson gson;

    Connection connection;
    SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy HH:mm:ssXXX");

    public MySQLCon() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Where is your MySQL JDBC Driver?");
            e.printStackTrace();
        }
        try {
            //sessionVariables=wait_timeout=600
            connection = DriverManager
                    .getConnection("jdbc:mysql://localhost:3306/mindreader?relaxAutoCommit=true&sessionVariables=wait_timeout=2147483&autoReconnect=true", "root", "hasan!xin");

        } catch (SQLException e) {
            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
        }

        if (connection == null) {
            System.out.println("Failed to make connection!");
        }
    }

    public Connection getConn() {
        return connection;
    }

    public void addUser(String username, String password, String email) {
        try {
            PreparedStatement preparedStatement = connection
                    .prepareStatement("insert into users values (default, ?, ?, ?)");
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, password);
            preparedStatement.executeUpdate();
            connection.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addClass(String name, String semester, String sectnum) {
        try {
            PreparedStatement preparedStatement = connection
                    .prepareStatement("insert into classname values (default, ?, ?, ?)");
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, semester);
            preparedStatement.setString(3, sectnum);
            preparedStatement.executeUpdate();
            connection.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addpermission(int userid, boolean admin, boolean teacher, boolean tutor, boolean student) {
        try {
            PreparedStatement preparedStatement = connection
                    .prepareStatement("insert into permission values (default, ?, ?, ?, ?, ?)");
            preparedStatement.setInt(1, userid);
            preparedStatement.setBoolean(2, admin);
            preparedStatement.setBoolean(3, teacher);
            preparedStatement.setBoolean(4, tutor);
            preparedStatement.setBoolean(5, student);
            preparedStatement.executeUpdate();
            connection.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addClassuser(int classid, int userid) {
        try {
            PreparedStatement preparedStatement = connection
                    .prepareStatement("insert into classuser values (default, ?, ?)");
            preparedStatement.setInt(1, classid);
            preparedStatement.setInt(2, userid);
            preparedStatement.executeUpdate();
            connection.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addCourse(String semester, String name) {
        try {
            PreparedStatement preparedStatement = connection
                    .prepareStatement("insert into classname values (default, ?, ?)");
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, semester);
            preparedStatement.executeUpdate();
            connection.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void addCourseUser(String classid, String username) {
        try {
            Integer userId = getUserId(username);
            PreparedStatement preparedStatement = connection
                    .prepareStatement("insert into classuser values (default, ?, ?)");
            preparedStatement.setString(1, classid);
            preparedStatement.setString(2, userId.toString());
            preparedStatement.executeUpdate();
            connection.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getCourseID(String semester, String name) {
        int res = -1;
        try {
            Statement postStmt = connection.createStatement();
            ResultSet rs = postStmt.executeQuery("select id from classname where semester='" + semester + "' and name='" + name + "'");
            while (rs.next()) {
                res = rs.getInt("id");
            }
        } catch (Exception e) {
        }
        return res;
    }
    
    public int getCourseUserID(String classid, String username) {
        int res = -1;
        try {
            Statement postStmt = connection.createStatement();
            ResultSet rs = postStmt.executeQuery("select c.id from classuser c, users u where c.classid='" + classid + "' and c.userid = u.id and u.username='" + username + "'");
            while (rs.next()) {
                res = rs.getInt("id");
            }
        } catch (Exception e) {
        }
        return res;
    }
    public List<MyClass> getCourses() {
        List<MyClass> res = new ArrayList<>();
        try {
            Statement postStmt = connection.createStatement();
            ResultSet rs = postStmt.executeQuery("select * from classname");
            while (rs.next()) {
                res.add(new MyClass(rs.getString("name"), rs.getString("semester"), rs.getInt("id"))) ;
            }
        } catch (Exception e) {
        }
        return res;
    }
    
    public List<String> getClassPerson(int classid, String type) {
        List<String> res = new ArrayList<>();
        try {
            Statement postStmt = connection.createStatement();
            ResultSet rs = postStmt.executeQuery("select u.username from users u, classuser c, permission p where c.classid=" + classid + " and c.userid=p.userid and u.id = p.userid and p." + type + "=true");
            while (rs.next()) {
                res.add(rs.getString("username"));
            }
        } catch (Exception e) {
        }
        return res;
    }
    public List<String> getAllType(String type) {
        List<String> res = new ArrayList<>();
        try {
            Statement postStmt = connection.createStatement();
//            ResultSet rs = postStmt.executeQuery("select u.username from users u, permission p where c.userid=p.userid and p." + type + "=true");
            ResultSet rs = postStmt.executeQuery("select u.username from users u, permission p where u.id=p.userid and p." + type + "=true");
            while (rs.next()) {
                res.add(rs.getString("username"));
            }
        } catch (Exception e) {
        }
        return res;
    }

    public int getUserId(String username) {
        int res = -1;
        try {
            Statement postStmt = connection.createStatement();
            ResultSet rs = postStmt.executeQuery("select id from users where username='" + username + "'");
            while (rs.next()) {
                res = rs.getInt("id");
            }
        } catch (Exception e) {
        }
        return res;
    }

    public String getUserPassword(String username) {
        String res = null;
        try {
            Statement postStmt = connection.createStatement();
            ResultSet rs = postStmt.executeQuery("select password from users where username='" + username + "'");
            while (rs.next()) {
                res = rs.getString("password");
            }
        } catch (Exception e) {
        }
        return res;
    }

    public String getPermission(String username) {
        String res = null;
        try {
            Statement postStmt = connection.createStatement();
            ResultSet rs = postStmt.executeQuery("select p.admin, p.teacher, p.tutor, p.student from users u, permission p where u.id = p.id and u.username='" + username + "'");
            while (rs.next()) {
                res = "";
                if (rs.getBoolean("admin")) {
                    res += "admin";
                }
                if (rs.getBoolean("teacher")) {
                    res += "teacher";
                }
                if (rs.getBoolean("tutor")) {
                    res += "tutor";
                }
                if (rs.getBoolean("student")) {
                    res += "student";
                }
            }
        } catch (Exception e) {
        }
        return res;
    }

    public String getPermissions(String level) {
        String res = null;
        List<SQLData> list = new ArrayList<>();
        try {
            Statement postStmt = connection.createStatement();
            String basic = "select u.id, u.username, p.admin, p.teacher, p.tutor, p.student from users u, permission p where u.id = p.id";
            if (level.equals("admin")) {
                basic += " and p.admin = false";
            } else if (level.equals("teacher")) {
                basic += " and p.admin = false and p.teacher = false";
            }
            ResultSet rs = postStmt.executeQuery(basic);
            while (rs.next()) {
                int id = rs.getInt("id");
                String userName = rs.getString("username");
                Boolean admin = rs.getBoolean("admin");
                Boolean teacher = rs.getBoolean("teacher");
                Boolean tutor = rs.getBoolean("tutor");
                Boolean student = rs.getBoolean("student");
                list.add(new SQLData(id, userName, admin, teacher, tutor, student));
            }
        } catch (Exception e) {
        }
        res = gson.toJson(list);
        return res;
    }

    public void updatePermission(String userid, String field, Boolean value) {
        try {
            PreparedStatement preparedStatement = connection
                    .prepareStatement("update permission set " + field + "=? where userid=" + userid);
            preparedStatement.setBoolean(1, value);
            preparedStatement.executeUpdate();
            connection.commit();
        } catch (Exception e) {
            System.out.println("update failed");
            e.printStackTrace();
        }
    }

}
