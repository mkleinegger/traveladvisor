package service;

import java.sql.Timestamp;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.google.gson.Gson;

import bll.Besuch;
import bll.Location;
import bll.Rezension;
import dal.BesuchDAL;
import dal.LocationDAL;
import dal.RezensionenDAL;

@Path("besucheList")
public class BesucheList {
	@Context
    private UriInfo context;
	
	
	public BesucheList() {
    }
	
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response getAll(@QueryParam("locationId") String locationid, @QueryParam("besucherId") String besucherid) {
        Response.ResponseBuilder response = Response.status(Response.Status.OK);
        try {
        		response.entity(new Gson().toJson(BesuchDAL.getAll(besucherid, locationid)));
        
        } catch (Exception e) {
            response.status(Response.Status.BAD_REQUEST);
            response.entity("[ERROR] " + e.getMessage());
        }
        
        System.out.println("======================webservice GET called");
        return response.build();
    }
    
    
    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public Response newBook(Besuch new_besuch) throws Exception {
        Response.ResponseBuilder response = Response.status(Response.Status.OK);
        new_besuch.generateId();
        new_besuch.setZeitpunkt(new Timestamp(System.currentTimeMillis()));
        
        try {
        	BesuchDAL.create(new_besuch);
        	response.status(Response.Status.CREATED);
            response.entity(new_besuch);
        } catch (Exception e) {
            response.status(Response.Status.BAD_REQUEST);
            response.entity("[ERROR] " + e.getMessage());
        }

        return response.build();
    }

    
    
}
