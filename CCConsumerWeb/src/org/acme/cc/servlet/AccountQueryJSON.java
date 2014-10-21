package org.acme.cc.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.WebServiceRef;

import org.acme.cc.delegate.AccountQuery;
import org.acme.cc.delegate.AccountQueryDelegate;
import org.acme.cc.jaxws.QueryRequest;
import org.acme.cc.jaxws.QueryResponse;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

/**
 * Servlet implementation class AccountQueryJSON.
 */
@WebServlet("/cc/json")
public class AccountQueryJSON extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final String CLASSNAME = AccountQueryJSON.class.getName();
	private static final Logger log = Logger.getLogger(CLASSNAME);
	
	public static final String JSON_CC_NO = "CCNo";
	public static final String JSON_ACCT_NO = "AcctNo";
	public static final String JSON_FIRST_NAME = "FirstName";
	public static final String JSON_LAST_NAME  = "LastName";
	public static final String JSON_BALANCE    = "Balance";
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AccountQueryJSON() {
        super();
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		log.entering(CLASSNAME, "doPost");
		
		InputStream jsonInputStream = request.getInputStream();
		ObjectMapper jacksonMapper = new ObjectMapper();
		Map<String, String> jsonInput = jacksonMapper.readValue(jsonInputStream, new TypeReference<Map<String, String>>() {});
		String jsonValue = null;

		jsonValue = jsonInput.get(JSON_CC_NO);
		if (jsonValue == null) {
		   log.severe("Missing " + JSON_CC_NO + " field.");
		   response.setStatus(400);
		} else {
		   QueryRequest soapInput = new QueryRequest();
		   soapInput.setCcNo(jsonValue);
		   
		   jsonValue = jsonInput.get(JSON_LAST_NAME);
		   if (jsonValue != null) {
		      log.finer("JSON " + JSON_LAST_NAME + " value: " + jsonValue);
		      soapInput.setLastName(jsonValue);
		   }
		   try {
		      log.fine("Acquire instance of AccountQueryDelegate.");
		      AccountQuery account = new AccountQueryDelegate();
		      log.fine("Invoking CC web service.");
            QueryResponse soapOutput = account.invoke(soapInput);
            log.fine("CC web service returned.");
            response.setStatus(200);
            response.setContentType("application/json");
            Map<String, String> jsonOutput = new LinkedHashMap<String, String>();
            jsonOutput.put(JSON_CC_NO,      soapOutput.getCcNo());
            jsonOutput.put(JSON_ACCT_NO,    soapOutput.getAcctNo());
            jsonOutput.put(JSON_FIRST_NAME, soapOutput.getFirstName());
            jsonOutput.put(JSON_LAST_NAME,  soapOutput.getLastName());
            jsonOutput.put(JSON_BALANCE,    String.valueOf(soapOutput.getBalance()));
            log.fine("Marshalling response to JSON.");
            jacksonMapper.writeValue(response.getOutputStream(), jsonOutput);
         } catch (Exception e) {
            log.logp(Level.SEVERE, CLASSNAME, "doPost", "CC web service fault", e);
            log.warning("Returning 500 to client with message: " + e.getLocalizedMessage());
            response.setStatus(500);
            response.setContentType("text/plain");
            response.getOutputStream().println(e.getLocalizedMessage());
         }
		   response.setContentType("application/json");
		}
		log.exiting(CLASSNAME, "doPost");
	}
}