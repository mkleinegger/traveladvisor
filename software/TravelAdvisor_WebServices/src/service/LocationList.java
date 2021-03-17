package service;

import java.net.http.HttpHeaders;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.google.gson.Gson;

import bll.Location;
import dal.BrancheDAL;
import dal.LocationDAL;

@Path("locationList")
public class LocationList {
	@Context
    private UriInfo context;
	
	
	public LocationList() {
    }
	
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response getAll(@QueryParam("distanz") Double distanz, @QueryParam("x") Double x, 
    		@QueryParam("y") Double y, @QueryParam("loadBranchen") Boolean loadBranchen, @QueryParam("besitzer") String id_besitzer) {
        Response.ResponseBuilder response = Response.status(Response.Status.OK);
        try {
        	if(distanz != null && x != null && y != null)
        		response.entity(new Gson().toJson(LocationDAL.getWithinDistance(distanz, x, y, loadBranchen)));
        	else if (id_besitzer != null)
    		response.entity(new Gson().toJson(LocationDAL.getByBesitzer(loadBranchen, id_besitzer)));
        	else {
        		if(loadBranchen == null)
        			response.entity(new Gson().toJson(LocationDAL.getAll(true)));
        		else
        			response.entity(new Gson().toJson(LocationDAL.getAll(loadBranchen)));
        	}
        	
        } catch (Exception e) {
            response.status(Response.Status.BAD_REQUEST);
            response.entity("[ERROR] " + e.getMessage());
        }
        
        System.out.println("======================webservice GET called");
        return response.build();
    }
    
    

}
