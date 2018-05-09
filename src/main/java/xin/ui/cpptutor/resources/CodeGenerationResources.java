/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xin.ui.cpptutor.resources;

import com.google.gson.Gson;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import xin.ui.cpptutor.Generation.GenerateCode;
import xin.ui.cpptutor.matching.MatchingService;
import xin.ui.cpptutor.obj.Feedback;
import static xin.ui.cpptutor.resources.TestResource.getAssignmentConstraints;
import static xin.ui.cpptutor.resources.TestResource.getAssignmentPatterns;

/**
 *
 * @author mou1609
 */
@Path("code")
public class CodeGenerationResources {

    Gson gson;

    @Inject
    public CodeGenerationResources(Gson gson) {
        this.gson = gson;
    }

    @GET
    public Response status() {
        return Response.status(200)
                .entity("I am good")
                .build();

    }

    @GET
    @Path("/assignment")
    public Response generateAssignment() throws Exception {

        GenerateCode.getAssignmentOne();

        return Response.status(200)
                .entity("I am good")
                .build();

    }
}
