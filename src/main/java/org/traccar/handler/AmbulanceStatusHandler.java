/*
 * Copyright 2024 - 2025 Anton Tananaev (anton@traccar.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.traccar.handler;

import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.traccar.model.Device;
import org.traccar.model.Driver;
import org.traccar.model.Geofence;
import org.traccar.model.Position;
import org.traccar.model.UserGeofence; // Import UserGeofence
import org.traccar.session.cache.CacheManager;
import org.traccar.storage.Storage; // Correct import
import org.traccar.storage.StorageException; // Correct import
import org.traccar.storage.query.Columns;
import org.traccar.storage.query.Condition;
import org.traccar.storage.query.Request;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class AmbulanceStatusHandler extends BasePositionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(AmbulanceStatusHandler.class);

    private final CacheManager cacheManager;
    private final Storage storage;

    // Define required driver types for a full crew
    // This can be configured based on specific requirements (e.g., "Paramedic", "EMT", "Driver")
    private static final Set<String> REQUIRED_DRIVER_TYPES = Set.of("Paramedic", "Driver"); // Example types

    @Inject
    public AmbulanceStatusHandler(CacheManager cacheManager, Storage storage) {
        this.cacheManager = cacheManager;
        this.storage = storage;
    }

    @Override
    public void onPosition(Position position, Callback callback) {
        Device device = cacheManager.getObject(Device.class, position.getDeviceId());

        if (device != null && "ambulance".equalsIgnoreCase(device.getDeviceType())) {
            String newAmbulanceStatus = Device.AMBULANCE_STATUS_OFFLINE;

            boolean hasCrew = false;
            Set<String> driverUniqueIds = device.getDriverUniqueIds();

            if (!driverUniqueIds.isEmpty()) {
                try {
                    // Fetch all drivers associated with the device
                    Collection<Driver> assignedDrivers = storage.getObjects(Driver.class, new Request(
                            new Columns.All(),
                            new Condition.In("uniqueId", driverUniqueIds))); // Changed to Condition.in

                    // Collect the types of the assigned drivers
                    Set<String> assignedDriverTypes = assignedDrivers.stream()
                            .map(Driver::getType)
                            .filter(type -> type != null && !type.isEmpty())
                            .collect(Collectors.toSet());

                    // Check if all required driver types are present in the assigned crew
                    hasCrew = assignedDriverTypes.containsAll(REQUIRED_DRIVER_TYPES);

                } catch (StorageException e) {
                    LOGGER.warn("Failed to get drivers for device: " + device.getId(), e);
                }
            }

            if (hasCrew) {
                // Check geofences
                Set<Long> geofenceIds = new HashSet<>();
                try {
                    Collection<UserGeofence> userGeofences = storage.getObjects(UserGeofence.class, new Request(
                            new Columns.All(),
                            new Condition.Equals("deviceId", device.getId())));
                    geofenceIds = userGeofences.stream()
                            .map(UserGeofence::getGeofenceId)
                            .collect(Collectors.toSet());
                } catch (StorageException e) {
                    LOGGER.warn("Failed to get user geofences for device: " + device.getId(), e);
                }

                boolean inBaseGeofence = false;

                if (!geofenceIds.isEmpty()) {
                    try {
                        Collection<Geofence> geofences = storage.getObjects(Geofence.class, new Request(
                                new Columns.All(),
                                new Condition.And(
                                        new Condition.In("id", geofenceIds), // Changed to Condition.in
                                        new Condition.Equals("type", Geofence.GEOFENCE_TYPE_BASE))));

                        for (Geofence geofence : geofences) {
                            if (geofence.containsPosition(position)) {
                                inBaseGeofence = true;
                                break;
                            }
                        }
                    } catch (StorageException e) {
                        LOGGER.warn("Failed to get geofences for device: " + device.getId(), e);
                    }
                }

                if (inBaseGeofence) {
                    newAmbulanceStatus = Device.AMBULANCE_STATUS_AVAILABLE;
                } else {
                    newAmbulanceStatus = Device.AMBULANCE_STATUS_ON_MISSION;
                }
            }

            if (!newAmbulanceStatus.equals(device.getAmbulanceStatus())) {
                device.setAmbulanceStatus(newAmbulanceStatus);
                try {
                    storage.updateObject(device, new Request(
                            new Columns.Include("ambulanceStatus"),
                            new Condition.Equals("id", device.getId())));
                } catch (StorageException e) {
                    LOGGER.warn("Failed to update ambulance status for device: " + device.getId(), e);
                }
            }
        }
        callback.processed(false);
    }
}
