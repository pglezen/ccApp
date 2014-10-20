package org.acme.cc.servlet;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.Subject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;

import com.ibm.websphere.security.WSSecurityException;
import com.ibm.websphere.security.auth.WSSubject;
import com.ibm.websphere.security.cred.WSCredential;

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
	      List<String> groups = getCallerUserGroups();
         if (groups != null) {
            groups.add("BogusGroup1");
            groups.add("BogusGroup2");
            jsonOutput.put(JSON_GROUPS, groups);
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
	
	/**
	 * Return a list of groups for the user ID of the authenticated caller.
	 * This uses a 
	 * <a href="http://www.ibm.com/support/knowledgecenter/SSAW57_8.5.5/com.ibm.websphere.javadoc.doc/web/apidocs/com/ibm/websphere/security/auth/WSSubject.html?cp=SSAW57_8.5.5">WAS-specific WSSubject API</a>
	 * to retrieve group information that was
	 * already fetched at the initial login time.
	 */
	protected static List<String> getCallerUserGroups() {
	   log.entering(CLASSNAME, "getCallerUserGroups");
	   List<String> groups = new LinkedList<String>();
	   try {
         Subject subject = WSSubject.getCallerSubject();
         if (subject != null) {
            Set<WSCredential> creds = subject.getPublicCredentials(WSCredential.class);
            if (creds.size() == 1) { // We expect exactly one.
               WSCredential credential = creds.iterator().next();
               log.fine("        User ID = " + credential.getSecurityName());
               log.finer("      Unique ID = " + credential.getUniqueSecurityName());
               log.finer("Realm Unique ID = " + credential.getRealmUniqueSecurityName());
               log.finer("     Realm Name = " + credential.getRealmName());
               // This API was fixed before generics came to Java.  It returns an
               // ArrayList, but can be safely assigned to List<String>.
               groups = credential.getGroupIds();
               if (!groups.isEmpty()) {
                  log.finer("Group membership:");
                  for (String groupName : groups) {
                     log.finer("\t" + groupName);
                  }
               } else {
                  log.fine("Group membership is empty.");
               }
            } else if (creds.size() == 0) {
               log.info("WSCredential list is empty.");
            } else {
               log.warning("WSCredential list size = " + creds.size() + ". This should not happen.");
            }
         } else {
            log.warning("getCallerSubject returned null; probably unauthenticated.");
         }
      } catch (WSSecurityException e) {
         log.logp(Level.SEVERE, CLASSNAME, "getCallerUserGroups", "Failed to get user groups.", e);
      } catch (GeneralSecurityException e) {
         log.logp(Level.SEVERE, CLASSNAME, "getCallerUserGroups", "Failed to get user groups.", e);
      }
	   log.exiting(CLASSNAME, "getCallerUserGroups");
	   return groups;
	}
}