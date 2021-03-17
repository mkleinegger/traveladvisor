package service;

import java.io.IOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.google.gson.Gson;

import bll.Branche;
import bll.Location;
import dal.BrancheDAL;
import dal.Database;

@Path("brancheDetail")
public class BrancheDetail {
	
    
    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getById(@PathParam("id") String id) {
        Response.ResponseBuilder response = Response.status(Response.Status.OK);
        try {
            response.entity(new Gson().toJson(BrancheDAL.getById(id)));
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
    public Response newBook(Branche new_bra) throws Exception {
        Response.ResponseBuilder response = Response.status(Response.Status.OK);
        System.out.println("======================NEW Branche: ");

        new_bra.generateUUID();
        
        System.out.println("ID: " + new_bra.getId());
        try {
        	BrancheDAL.create(new_bra);
        	response.status(Response.Status.CREATED);
            response.entity(BrancheDAL.getById(new_bra.getId().toString()));
        } catch (Exception e) {
            response.status(Response.Status.BAD_REQUEST);
            response.entity("[ERROR] " + e.getMessage());
        }

        return response.build();
    }

    @POST
    @Path("test")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public Response test(Branche new_bra) throws Exception {
        Response.ResponseBuilder response = Response.status(Response.Status.OK);

        new_bra.generateUUID();
        
        System.out.println("ID: " + new_bra.getId());
        System.out.println("ID: " + new_bra.getBezeichnung());

        try {
        	response.status(Response.Status.CREATED);
            response.entity(new_bra);
        } catch (Exception e) {
            response.status(Response.Status.BAD_REQUEST);
            response.entity("[ERROR] " + e.getMessage());
        }

        return response.build();
    }
    
    
    @PUT
    @Consumes({MediaType.APPLICATION_JSON})
    public Response updateBook(Branche upd_bra) throws IOException {
        Response.ResponseBuilder response = Response.status(Response.Status.OK);
        
        try {            
        	if(upd_bra.getBezeichnung() == "" || upd_bra.getBezeichnung() == null) 
        		throw new Exception("Bezeichnung nicht gesetzt");
        	if(upd_bra.getId() == null || upd_bra.getId().toString() == "")
        		throw new Exception("Update ohne ID nicht möglich");
        	
        	BrancheDAL.update(upd_bra.getId().toString(), upd_bra);
        	response.status(Response.Status.OK);
            response.entity(BrancheDAL.getById(upd_bra.getId().toString()));
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
        	BrancheDAL.delete(delete_id);
        	response.status(Response.Status.NO_CONTENT);
            response.entity("Branche deleted");
        } catch (Exception e) {
            response.status(Response.Status.BAD_REQUEST);
            response.entity("[ERROR] " + e.getMessage());
        }

        return response.build();
    }
    
}
