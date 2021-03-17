package service;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

import com.sun.research.ws.wadl.Application;

@ApplicationPath("TravelGuide")
public class ApplicationConfig extends ResourceConfig {
	
	public ApplicationConfig() {
		packages("bll","dal", "service"); 
		
		register(CORSResponseFilter.class);
		register("rest");
	}
	

	/*
	 public Set<Class<?>> getClasses() {
	        final Set<Class<?>> resources = new HashSet<Class<?>>();

	        // Add your resources.
	        resources.add(LocationDetail.class);
	        resources.add(LocationList.class);
	        // Add additional features such as support for Multipart.
	        resources.add(MultiPartFeature.class);

	        return resources;
	    }
	*/
}
