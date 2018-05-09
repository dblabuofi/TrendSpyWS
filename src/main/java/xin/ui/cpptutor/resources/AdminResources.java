/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xin.ui.cpptutor.resources;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import xin.ui.cpptutor.dao.CourseData;
import xin.ui.cpptutor.dao.MyClass;
import xin.ui.cpptutor.dao.MySQLCon;

/**
 *
 * @author mou1609
 */
@Path("admin")
public class AdminResources {

    MySQLCon mySQLCon;
    Gson gson;

    @Inject
    public AdminResources(MySQLCon mySQLCon, Gson gson) {
        this.mySQLCon = mySQLCon;
        this.gson = gson;
    }

    @GET
    public Response status() {
        return Response.status(200)
                .entity("I am good")
                .build();
    }

    @GET
    @Path("register")
    public Response register(
            @QueryParam("username") String username,
            @QueryParam("password") String password,
            @QueryParam("email") String email
    ) {
        System.out.println(username);
        System.out.println(password);
        System.out.println(email);
        int userId = mySQLCon.getUserId(username);
        if (userId != -1) {//we have before
            return Response.status(200)
                    .entity("Duplicated Value")
                    .build();
        }
        mySQLCon.addUser(username, password, email);
        userId = mySQLCon.getUserId(username);
        mySQLCon.addpermission(userId, false, false, false, true);

        return Response.status(200)
                .entity("I am good")
                .build();
    }

    @GET
    @Path("login")
    public Response login(
            @QueryParam("username") String username,
            @QueryParam("password") String password
    ) {
        System.out.println(username);
        System.out.println(password);
        String pass = mySQLCon.getUserPassword(username);
        if (!pass.equals(password)) {//we have before
            return Response.status(200)
                    .entity("password not match")
                    .build();
        }
        //return permission
        String permission = mySQLCon.getPermission(username);
        System.out.println(permission);
        return Response.status(200)
                .entity(permission)
                .build();
    }

    @GET
    @Path("permissions")
    public Response getPermissions(@QueryParam("level") String level) {
        System.out.println(level);
        //return permission
        String permissions = mySQLCon.getPermissions(level);
        System.out.println(permissions);
        return Response.status(200)
                .entity(permissions)
                .build();
    }

    @GET
    @Path("updatePermission")
    public Response updatePermission(
            @QueryParam("userid") String userid,
            @QueryParam("field") String field,
            @QueryParam("value") Boolean value
    ) {
        System.out.println(userid);
        System.out.println(field);
        System.out.println(value);
        mySQLCon.updatePermission(userid, field, value);

        return Response.status(200)
                .entity("ggod")
                .build();
    }

    @GET
    @Path("addCourse")
    public Response addCourse(
            @QueryParam("semester") String semester,
            @QueryParam("name") String name
    ) {
        System.out.println(semester);
        System.out.println(name);
        int id = mySQLCon.getCourseID(semester, name);
        if (id != -1) {
            return Response.status(200)
                    .entity("Duplicated Course")
                    .build();
        }
        mySQLCon.addCourse(semester, name);

        return Response.status(200)
                .entity("ggod")
                .build();
    }

    @GET
    @Path("addPersonToClass")
    public Response addPersonToClass(
            @QueryParam("classid") String classid,
            @QueryParam("username") String username
    ) {
        System.out.println(classid);
        System.out.println(username);
        int id = mySQLCon.getCourseUserID(classid, username);
        if (id != -1) {
            return Response.status(200)
                    .entity("Duplicated User")
                    .build();
        }
        mySQLCon.addCourseUser(classid, username);

        return Response.status(200)
                .entity("ggod")
                .build();
    }

    @GET
    @Path("getCourses")
    public Response getCourses() {
        List<CourseData> res = new ArrayList<>();
        List<MyClass> courses = mySQLCon.getCourses();

        for (MyClass c : courses) {
            int classid = c.getId();
            List<String> teacher = mySQLCon.getClassPerson(classid, "teacher");
            List<String> tutor = mySQLCon.getClassPerson(classid, "tutor");
            List<String> student = mySQLCon.getClassPerson(classid, "student");

            res.add(new CourseData(c.getSemester() + " " + c.getName(), classid, teacher, tutor, student));

        }

        String resStr = gson.toJson(res);
        return Response.status(200)
                .entity(resStr)
                .build();
    }

    @GET
    @Path("getTypeList")
    public Response getTypeList(@QueryParam("type") String type) {
        List<String> res = mySQLCon.getAllType(type);
        System.out.println(type);
        String resStr = gson.toJson(res);
        return Response.status(200)
                .entity(resStr)
                .build();
    }


}
