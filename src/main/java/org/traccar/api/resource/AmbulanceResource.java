/*
 * REST resource for Ambulance entities.
 */
package org.traccar.api.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

import org.traccar.api.ExtendedObjectResource;
import org.traccar.model.Ambulance;

@Path("ambulances")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AmbulanceResource extends ExtendedObjectResource<Ambulance> {

    public AmbulanceResource() {
        super(Ambulance.class, "name", List.of("name", "uniqueId"));
    }
}
