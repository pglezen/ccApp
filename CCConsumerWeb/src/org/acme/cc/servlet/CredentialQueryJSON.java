package org.acme.cc.servlet;

import java.io.IOException;
import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;

import com.ibm.websphere.security.CustomRegistryException;
import com.ibm.websphere.security.EntryNotFoundException;
import com.ibm.websphere.security.UserRegistry;

/**
 * Servlet implementation class CredentialQuery.
 */
@WebServlet("/creds/json")
public class CredentialQueryJSON extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final String CLASSNAME = CredentialQueryJSON.class.getName();
	private static final Logger log = Logger.getLogger(CLASSNAME);
	
	public static final String JSON_DISPLAY_NAME  = "DisplayName";
	public static final String JSON_UNIQUE_ID     = "UniqueID";
	public static final String JSON_SECURITY_NAME = "SecurityName";
	public static final String JSON_GROUPS        = "Groups";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CredentialQueryJSON() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	   log.entering(CLASSNAME, "doGet");
	   
	   Principal userPrincipal = request.getUserPrincipal();
	   if (userPrincipal != null) {
	      ObjectMapper jacksonMapper = new ObjectMapper();
	      Map<String, Object> jsonOutput = new LinkedHashMap<String, Object>();
	      String securityName = userPrincipal.getName();
	      jsonOutput.put(JSON_SECURITY_NAME, securityName);
	      log.fine("userPrincipal.getName: " + securityName);
	      try {
            InitialContext ic = new InitialContext();
            UserRegistry registry = (UserRegistry)ic.lookup("UserRegistry");
            try {
               String uniqueId = registry.getUniqueUserId(securityName);
               String displayName = registry.getUserDisplayName(securityName);
               if (uniqueId != null) {
                  jsonOutput.put(JSON_UNIQUE_ID, uniqueId);
               }
               if (displayName != null) {
                  jsonOutput.put(JSON_DISPLAY_NAME, displayName);
               }
               List<String> groups = registry.getGroupsForUser(securityName);
               if (groups != null) {
                  groups.add("BogusGroup1");
                  groups.add("BogusGroup2");
                  jsonOutput.put(JSON_GROUPS, groups);
               }
            } catch (EntryNotFoundException e) {
               log.warning("Could not locate security name: " + securityName + " in UserRegistry");
            } catch (CustomRegistryException e) {
               log.logp(Level.WARNING, CLASSNAME, "doGet", "Failed to get unique user id", e);
            }
         } catch (NamingException e) {
            log.warning("Failed to retrieve UserRegistry instance from JNDI.");
         }
	      response.setStatus(HttpServletResponse.SC_OK);
	      response.setContentType("application/json");
	      jacksonMapper.writeValue(response.getOutputStream(), jsonOutput);
	   } else {
	      log.info("Unauthenticated user attempted to retrieve credential information.");
	      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	      response.setContentType("application/json");
	      response.getOutputStream().println("{}");
	   }
	}
}