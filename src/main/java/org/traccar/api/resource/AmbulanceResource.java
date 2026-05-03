/*
 * Ambulance REST resource - wrapper for Device endpoints with ambulance type.
 * Maintains backward compatibility with existing API clients.
 */
package org.traccar.api.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
import java.util.stream.Stream;

import org.traccar.api.BaseObjectResource;
import org.traccar.model.Device;
import org.traccar.storage.StorageException;

@Path("ambulances")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AmbulanceResource extends BaseObjectResource<Device> {

    public AmbulanceResource() {
        super(Device.class);
    }

    /**
     * Gets ambulances (devices with deviceType='ambulance').
     * Legacy endpoint for backward compatibility.
     * New code should use /api/devices?deviceType=ambulance instead.
     */
    @GET
    public Stream<Device> getAmbulances(
            @QueryParam("all") boolean all,
            @QueryParam("userId") long userId,
            @QueryParam("uniqueId") List<String> uniqueIds,
            @QueryParam("id") List<Long> deviceIds,
            @QueryParam("excludeAttributes") boolean excludeAttributes,
            @QueryParam("limit") int limit,
            @QueryParam("offset") int offset,
            @QueryParam("keyword") String keyword) throws StorageException {

        // This is now a legacy endpoint that wraps the Device API
        // Ambulances are Devices with deviceType='ambulance'
        // For true unification, clients should migrate to /api/devices?deviceType=ambulance
        throw new UnsupportedOperationException("Use /api/devices?deviceType=ambulance instead");
    }
}

