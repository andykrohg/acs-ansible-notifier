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
    public static final String ANSIBLE_TEMPLATE_ID = System.getenv("ANSIBLE_TEMPLATE_ID");

    @RestClient
    JobTemplateService jobTemplateService;

    @POST
    public JsonObject notification(JsonObject alert) {
        JsonObject data = Json.createObjectBuilder().add("extra_vars", alert).build();
        jobTemplateService.launchJob("Basic  " +
                Base64.getEncoder().encodeToString((ANSIBLE_USERNAME + ":" + ANSIBLE_PASSWORD).getBytes()),
                ANSIBLE_TEMPLATE_ID,
                data.toString());
        return alert.getJsonObject("alert");
    }
}