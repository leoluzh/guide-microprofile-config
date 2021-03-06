// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2017, 2020 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial implementation
 *******************************************************************************/
// end::copyright[]
// tag::config-methods[]
package io.openliberty.guides.inventory;

import java.util.Properties;

// CDI
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.openliberty.guides.inventory.InventoryConfig;

@RequestScoped
@Path("systems")
public class InventoryResource {

  @Inject
  InventoryManager manager;

  // tag::config-injection[]
  @Inject
  InventoryConfig inventoryConfig;
  // end::config-injection[]

  @GET
  @Path("{hostname}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getPropertiesForHost(@PathParam("hostname") String hostname) {

    if (!inventoryConfig.isInMaintenance()) {
      // tag::config-port[]
      Properties props = manager.get(hostname, inventoryConfig.getPortNumber());
      // end::config-port[]
      if (props == null) {
        return Response.status(Response.Status.NOT_FOUND)
                       .entity("{ \"error\" : \"Unknown hostname or the system service " 
                       + "may not be running on " + hostname + "\" }")
                       .build();
      }

      // Add to inventory
      manager.add(hostname, props);
      return Response.ok(props).build();
    } else {
      // tag::email[]
      return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                     .entity("{ \"error\" : \"Service is currently in maintenance. " 
                     + "Contact: " + inventoryConfig.getEmail().toString() + "\" }")
                     .build();
      // end::email[]
    }
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response listContents() {
    // tag::isInMaintenance[]
    if (!inventoryConfig.isInMaintenance()) {
    // end::isInMaintenance[]
      return Response.ok(manager.list()).build();
    } else {
      // tag::email[]
      return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                     .entity("{ \"error\" : \"Service is currently in maintenance. " 
                     + "Contact: " + inventoryConfig.getEmail().toString() + "\" }")
                     .build();
      // end::getEmail[]
    }
  }

}

// end::config-methods[]
