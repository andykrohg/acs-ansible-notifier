package com.redhat;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.eclipse.microprofile.rest.client.inject.RestClient;

@Path("/")
public class NotificationListener {
    @RestClient
    JobTemplateService jobTemplateService;

    @POST
    public JsonObject notification(@HeaderParam("Authorization") String authenticationHeader, JsonObject notification) {
        String templateId = notification.getString("template_id");
        String payload = Json.createObjectBuilder().add("extra_vars", notification).build().toString();
        jobTemplateService.launchJob(authenticationHeader, templateId, payload);
        return Json.createObjectBuilder().build();
    }
}