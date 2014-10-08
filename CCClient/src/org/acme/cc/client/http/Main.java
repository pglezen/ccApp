package org.acme.cc.client.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

public class Main {

   public static void main(String[] args) {
      HttpAuthenticationFeature basicAuth = HttpAuthenticationFeature.basic("paul", "paul");
      Client client = ClientBuilder.newClient();
//      client.register(basicAuth);  // This causes all invocations to use basicAuth.
      WebTarget target = client.target("http://localhost:9804/consumer");
      WebTarget credTarget = target.path("creds/json");
      credTarget.register(basicAuth);
      NewCookie ltpaCookie = callAuth(credTarget);
      
//      System.out.println("Waiting 6 minutes for LTPA token to expire ...");
//      try {
//         Thread.sleep(360000);
//      } catch (InterruptedException e1) {
//         e1.printStackTrace();
//      }
//      System.out.println("Done waiting.");

      WebTarget   ccTarget = target.path("cc/json");
      Invocation.Builder ccBuilder = ccTarget.request(MediaType.APPLICATION_JSON_TYPE);
      LinkedHashMap<String, String> ccJsonRequest = new LinkedHashMap<String, String>();
      ccJsonRequest.put("CCNo", "g4926032");
      ccJsonRequest.put("LastName", "Smith");
      try {
         ObjectMapper jacksonMapper = new ObjectMapper();
         for (short retriesLeft = 2; retriesLeft > 0; retriesLeft--) {
            if (ltpaCookie != null) {
               ccBuilder.cookie(ltpaCookie);
               System.out.println("Added LTPA cookie to CC request builder.");
            }
            Invocation ccInvocation = ccBuilder.buildPost(Entity.entity(jacksonMapper.writeValueAsString(ccJsonRequest), MediaType.APPLICATION_JSON_TYPE));
            Response ccResponse = ccInvocation.invoke();
            int status = ccResponse.getStatus();
            System.out.println(" CC Response status = " + status);
            if (status == 200) {
               retriesLeft = 0;
               System.out.println("CC Response " + (ccResponse.hasEntity() ? "has an" : "does not have an") + " entity.");
               String responseString = ccResponse.readEntity(String.class);
               System.out.println("CC Response string = " + responseString);
            } else if (status == 401) {
               System.out.println("LTPA cookie failed ...  re-authenticating.");
               ltpaCookie = callAuth(credTarget);
            }
         }
      } catch (JsonGenerationException e) {
         e.printStackTrace();
      } catch (JsonMappingException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
   
   // Call the auth target and return the LTPA cookie that results if
   // the authentication was successful.  Return null otherwise.
   //
   static NewCookie callAuth(WebTarget target) {
      NewCookie result = null;
      Invocation.Builder credInvocationBuilder = target.request(MediaType.APPLICATION_JSON_TYPE);
      Response credResponse = credInvocationBuilder.get();
      System.out.println("Credential Response status = " + credResponse.getStatus());
      System.out.println("Credential Response " + (credResponse.hasEntity() ? "has an" : "does not have an") + " entity.");
      Map<String, NewCookie> cookies = credResponse.getCookies();
      result = cookies.get("LtpaToken2");
      for (String cookieName : cookies.keySet()) {
         NewCookie c = cookies.get(cookieName);
         System.out.println("Cookie key: " + cookieName);
         System.out.println("\t   name: " + c.getName());
         System.out.println("\t  value: " + c.getValue());
         System.out.println("\t domain: " + c.getDomain());
         System.out.println("\t   path: " + c.getPath());
         System.out.println("\t expiry: " + c.getExpiry());
         System.out.println("\tmax age: " + c.getMaxAge());
      }
      String responseString = credResponse.readEntity(String.class);
      System.out.println("Response string = " + responseString);
      ObjectMapper jacksonMapper = new ObjectMapper();
      try {
         Map<String, Object> jsonResponse = jacksonMapper.readValue(responseString, new TypeReference<Map<String, Object>>() {});
         System.out.println("Response:");
         System.out.println("\tSecurity name: " + jsonResponse.get("SecurityName"));
         System.out.println("\tUnique ID: " + jsonResponse.get("UniqueID"));
         ArrayList<String> groups = (ArrayList<String>)jsonResponse.get("Groups");
         if (groups.size() > 0) {
            System.out.println("\tGroups:");
            for (String g : groups) {
               System.out.println("\t\tGroup name: " + g);
            }
         }
      } catch (IOException e) {
         e.printStackTrace();
      }
      return result;
   }
}
