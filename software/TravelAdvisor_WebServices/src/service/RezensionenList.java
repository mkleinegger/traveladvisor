package service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.google.gson.Gson;

import dal.BrancheDAL;
import dal.RezensionenDAL;

@Path("rezensionenList")
public class RezensionenList {
	@Context
    private UriInfo context;
	
	public RezensionenList() {
    }
    
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response getAll(@QueryParam("location") String location, @QueryParam("besucher") String besucher) {
    	System.out.println("======================webservice GET called");
        Response.ResponseBuilder response = Response.status(Response.Status.OK);
        try {
        	if(location == null && besucher == null)
        		response.entity(new Gson().toJson(RezensionenDAL.getAll()));
        	else if(location != null)
    		response.entity(new Gson().toJson(RezensionenDAL.getByLocation(location)));
        	else if(besucher != null)
        		response.entity(new Gson().toJson(RezensionenDAL.getByBesucher(besucher)));
        } catch (Exception e) {
            response.status(Response.Status.BAD_REQUEST);
            response.entity("[ERROR] " + e.getMessage());
        }
        return response.build();
    }
}
