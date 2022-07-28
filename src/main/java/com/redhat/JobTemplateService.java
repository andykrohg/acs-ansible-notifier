package com.redhat;

import javax.json.JsonObject;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/")
@RegisterRestClient
public interface JobTemplateService {
    @POST
    @Path("/v2/job_templates/{id}/launch//")
    Response launchJob(@HeaderParam("Authorization") String authorization, @PathParam("id") String id, String data);
}