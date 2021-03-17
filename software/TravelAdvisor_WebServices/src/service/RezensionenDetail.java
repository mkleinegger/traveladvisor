package service;

import java.io.IOException;
import java.sql.Timestamp;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;

import bll.Aktion;
import bll.Error404;
import bll.Rezension;
import dal.PraemienDAL;
import dal.RezensionenDAL;

@Path("rezensionenDetail")
public class RezensionenDetail {
	@GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getById(@PathParam("id") String id) {
        Response.ResponseBuilder response = Response.status(Response.Status.OK);
        try {
            response.entity(new Gson().toJson(RezensionenDAL.getById(id)));
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
    public Response newBook(Rezension new_rez) throws Exception {
        Response.ResponseBuilder response = Response.status(Response.Status.OK);
        System.out.println("======================NEW Rezension: ");
        new_rez.generateId();
        new_rez.setTimestamp(new Timestamp(System.currentTimeMillis()));
        
        try {
        	RezensionenDAL.create(new_rez);
        	response.status(Response.Status.CREATED);
            response.entity(RezensionenDAL.getById(new_rez.getId().toString()));
        } catch (Exception e) {
            response.status(Response.Status.BAD_REQUEST);
            response.entity("[ERROR] " + e.getMessage());
        }

        return response.build();
    }

    
    
    @PUT
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_JSON})
    public Response updateBook(@PathParam("id") String id, Rezension upd_rez) throws IOException {
        Response.ResponseBuilder response = Response.status(Response.Status.OK);
        
        try {                
        	RezensionenDAL.getById(id);
        	upd_rez.setTimestamp(new Timestamp(System.currentTimeMillis()));
        	RezensionenDAL.update(id, upd_rez);
        	response.status(Response.Status.OK);
            response.entity(RezensionenDAL.getById(id));
        } 
        catch(Error404 e) {
        	response.status(Response.Status.NOT_FOUND);
        	response.entity("[ERROR] " + e.getMessage());
        }
        catch (Exception e) {
            response.status(Response.Status.BAD_REQUEST);
            response.entity("[ERROR] " + e.getMessage());
        }

        return response.build();
    }
    
    @DELETE
    @Path("{id}")
    public Response deleteArticle(@PathParam("id") String id)throws IOException {
        Response.ResponseBuilder response = Response.status(Response.Status.OK);

        try {
        	RezensionenDAL.getById(id);
        	RezensionenDAL.delete(id);
        	response.status(Response.Status.NO_CONTENT);
            response.entity("Rezension deleted");
        } catch (Error404 e) {
            response.status(Response.Status.NOT_FOUND);
            response.entity("[ERROR] " + e.getMessage());
        }
        catch (Exception e) {
            response.status(Response.Status.NOT_FOUND);
            response.entity("[ERROR] " + e.getMessage());
        }

        return response.build();
    }
    
    
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
