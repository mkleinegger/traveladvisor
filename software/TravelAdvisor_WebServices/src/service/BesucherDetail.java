package service;

import java.io.IOException;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;

import bll.Besitzer;
import bll.Besucher;
import dal.BesitzerDAL;
import dal.BesuchDAL;
import dal.BesucherDAL;
import dal.LocationDAL;

@Path("besucherDetail")
public class BesucherDetail {
	
	@GET
    @Path("verlauf/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getVerlaufById(@PathParam("id") String id_besucher) {
        Response.ResponseBuilder response = Response.status(Response.Status.OK);
        try {	
        		response.entity(new Gson().toJson(BesuchDAL.getByBesucherId(id_besucher)));
        } catch (Exception e) {
            response.status(Response.Status.NOT_FOUND);
            response.entity("[ERROR] " + e.getMessage());
        }
        System.out.println("======================webservice GET called");
        return response.build();
    }
	
	
	@GET
    @Path("{id}/punkte")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getPunkteById(@PathParam("id") String id_besucher) {
        Response.ResponseBuilder response = Response.status(Response.Status.OK);
        try {	
        		response.entity(new Gson().toJson(BesucherDAL.getPunkte(id_besucher)));
        } catch (Exception e) {
            response.status(Response.Status.NOT_FOUND);
            response.entity("[ERROR] " + e.getMessage());
        }
        System.out.println("======================webservice GET called");
        return response.build();
    }
	
	@POST
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public Response newBesitzer(Besucher new_bes) throws Exception {
        Response.ResponseBuilder response = Response.status(Response.Status.OK);
        
        try {
        	BesucherDAL.create(new_bes);
        	response.status(Response.Status.CREATED);
            response.entity(new_bes);
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
        	BesucherDAL.delete(delete_id);
        	response.status(Response.Status.NO_CONTENT);
            response.entity("Branche deleted");
        } catch (Exception e) {
            response.status(Response.Status.BAD_REQUEST);
            response.entity("[ERROR] " + e.getMessage());
        }

        return response.build();
    }
    
    
    @OPTIONS
    public Response preflight() {
    	Response.ResponseBuilder response = Response.status(Response.Status.OK);

        return response.build();
    }
}
