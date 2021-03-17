package service;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

import com.google.gson.Gson;

import bll.Branche;
import bll.Location;
import dal.BrancheDAL;
import dal.Database;
import dal.LocationDAL;
import dal.PraemienDAL;
import dal.RezensionenDAL;

@Path("locationDetail")
public class LocationDetail {

	@Context
    private UriInfo context;
	
	@GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getById(@PathParam("id") String id) {
        Response.ResponseBuilder response = Response.status(Response.Status.OK);
        try {	
        		response.entity(new Gson().toJson(LocationDAL.getById(id)));
        } catch (Exception e) {
            response.status(Response.Status.NOT_FOUND);
            response.entity("[ERROR] " + e.getMessage());
        }
        System.out.println("======================webservice GET called");
        return response.build();
    }
	
	@GET
    @Path("{id}/praemien")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getPraemienByLocation(@PathParam("id") String loc_id) {
        Response.ResponseBuilder response = Response.status(Response.Status.OK);
        try {
            response.entity(new Gson().toJson(PraemienDAL.getByLocation(loc_id)));
        } catch (Exception e) {
            response.status(Response.Status.NOT_FOUND);
            response.entity("[ERROR] " + e.getMessage());
        }
        System.out.println("======================webservice GET called");
        return response.build();
    }
    
	
	@GET
    @Path("{id}/rezensionen")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getRezensionenByLocation(@PathParam("id") String loc_id) {
        Response.ResponseBuilder response = Response.status(Response.Status.OK);
        try {
            response.entity(new Gson().toJson(RezensionenDAL.getByLocation(loc_id)));
        } catch (Exception e) {
            response.status(Response.Status.NOT_FOUND);
            response.entity("[ERROR] " + e.getMessage());
        }
        System.out.println("======================webservice GET called");
        return response.build();
    }
	
	@GET
    @Path("{id}/branchen")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getBranchenByLocation(@PathParam("id") String loc_id) {
        Response.ResponseBuilder response = Response.status(Response.Status.OK);
        try {
            response.entity(new Gson().toJson(BrancheDAL.getByLocationId(loc_id)));
        } catch (Exception e) {
            response.status(Response.Status.NOT_FOUND);
            response.entity("[ERROR] " + e.getMessage());
        }
        System.out.println("======================webservice GET called");
        return response.build();
    }
	
	@GET
	@Path("withinDistance")
    @Produces({MediaType.APPLICATION_JSON})
    public Response locationWithinDistance(@QueryParam("distanz") double distanz, @QueryParam("x") double x, @QueryParam("y") double y) {
        Response.ResponseBuilder response = Response.status(Response.Status.OK);
        try {
            response.entity(new Gson().toJson(LocationDAL.getWithinDistance(distanz, x, y, false)));
        } catch (Exception e) {
            response.status(Response.Status.NOT_FOUND);
            response.entity("[ERROR] " + e.getMessage());
        }
        System.out.println("======================webservice GET called");
        return response.build();
    }
	
	
    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public Response newBook(Location new_loc) throws Exception {
        Response.ResponseBuilder response = Response.status(Response.Status.OK);
        
        
        /*
         * Pflichfelder
         * Besitzer mit gültiger UUID
         * 
         */
        new_loc.generateUUID();
        
        try {
        	if(new_loc.getBesitzer() == null)
        		throw new Exception("Location braucht einen Besitzer");
        	System.out.println("Location wird eingefügt . . . ");
        	LocationDAL.create(new_loc);
        	response.status(Response.Status.CREATED);
            response.entity(LocationDAL.getById(new_loc.getId().toString()));
            
        } catch (Exception e) {
            response.status(Response.Status.BAD_REQUEST);
            response.entity("[ERROR] " + e.getMessage());
        }

        return response.build();
    }
        
    
    @PUT
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_JSON})
    public Response updateBook(@PathParam("id") String update_id, Location new_loc) throws IOException {
        Response.ResponseBuilder response = Response.status(Response.Status.OK);
        try {
        	LocationDAL.update(update_id, new_loc);
        	response.status(Response.Status.OK);
            response.entity(LocationDAL.getById(update_id));
        } catch (Exception e) {
            response.status(Response.Status.BAD_REQUEST);
            response.entity("[ERROR] " + e.getMessage());
        }

        return response.build();
    }
    
    @DELETE
    @Path("{id}")
    public Response deleteArticle(@PathParam("id") String delete_id)throws IOException {
        Response.ResponseBuilder response = Response.status(Response.Status.OK);

        try {
        	LocationDAL.delete(delete_id);
        	response.status(Response.Status.NO_CONTENT);
            response.entity("Location deleted");
        } catch (Exception e) {
            response.status(Response.Status.BAD_REQUEST);
            response.entity("[ERROR] " + e.getMessage());
        }

        return response.build();
    }
    /*
    
	@POST
	@Path("bildupload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadFile(@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail
			) {
		
		return Response.status(200)
				.entity("File received").build();
	}
*/
    @OPTIONS
    @Path("/{id}")
    public Response preflightWithId() {
    	Response.ResponseBuilder response = Response.status(Response.Status.OK);

        return response.build();
    }
    
    @OPTIONS
    public Response preflight() {
    	Response.ResponseBuilder response = Response.status(Response.Status.OK);

        return response.build();
    }
    
}
