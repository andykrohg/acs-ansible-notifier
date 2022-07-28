package com.redhat;

import java.util.Base64;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.eclipse.microprofile.rest.client.inject.RestClient;

@Path("/")
public class NotificationListener {
    public static final String ANSIBLE_USERNAME = System.getenv("ANSIBLE_USERNAME");
    public static final String ANSIBLE_PASSWORD = System.getenv("ANSIBLE_PASSWORD");

    @RestClient
    JobTemplateService jobTemplateService;

    @POST
    public JsonObject notification(JsonObject notification) {
        String templateId = notification.getString("template_id");
        String authString = "Basic  " + Base64.getEncoder().encodeToString((ANSIBLE_USERNAME + ":" + ANSIBLE_PASSWORD).getBytes());
        String payload = Json.createObjectBuilder().add("extra_vars", notification).build().toString();
        jobTemplateService.launchJob(authString, templateId, payload);
        return Json.createObjectBuilder().build();
    }
}