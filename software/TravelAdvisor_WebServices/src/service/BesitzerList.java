package service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.google.gson.Gson;

import dal.BesitzerDAL;
import dal.BrancheDAL;

@Path("besitzerList")
public class BesitzerList {
	@Context
    private UriInfo context;
	
	public BesitzerList() {
    }
    
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response getAll() {
    	System.out.println("======================webservice GET called");
        Response.ResponseBuilder response = Response.status(Response.Status.OK);
        try {
            response.entity(new Gson().toJson(BesitzerDAL.getAll()));
        } catch (Exception e) {
            response.status(Response.Status.BAD_REQUEST);
            response.entity("[ERROR] " + e.getMessage());
        }
        System.out.println("======================webservice GET called");
        return response.build();
    }
}
