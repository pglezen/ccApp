package org.acme.cc.delegate;

import java.util.Map;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.ws.BindingProvider;

import org.acme.cc.jaxws.CCPortType;
import org.acme.cc.jaxws.CCService;
import org.acme.cc.jaxws.QueryFaultMsg;
import org.acme.cc.jaxws.QueryRequest;
import org.acme.cc.jaxws.QueryResponse;

public class AccountQueryDelegate implements AccountQuery {
   
   public static final String CLASSNAME = AccountQueryDelegate.class.getName();
   private static final Logger log = Logger.getLogger(CLASSNAME);
   
   public static final String jndiLocalRef = "service/CCService";
   
   CCPortType jaxwsPortType;

   public AccountQueryDelegate() throws DelegateException {
      log.entering(CLASSNAME, "ctor");
      try {
         Context ctx = new InitialContext();
         CCService service = (CCService)ctx.lookup("java:comp/env/" + jndiLocalRef);
         jaxwsPortType = service.getCCService();
         if (jaxwsPortType == null) {
            DelegateException de = new DelegateException();
            de.setJndiName(jndiLocalRef);
            de.setServiceName("CCService");
            throw de;
         }
         String endpointURL = "http://localhost:9086/provider/CCService";
         BindingProvider bp = (BindingProvider)jaxwsPortType;
         Map<String, Object> reqCtx = bp.getRequestContext();
         reqCtx.put("javax.xml.ws.service.endpoint.address", endpointURL);
         log.fine("JAX-WS port type initialized with endpoint URL: " + endpointURL);
      } catch (NamingException e) {
         DelegateException de = new DelegateException(e);
         de.setJndiName(jndiLocalRef);
         de.setServiceName("CCService");  
         throw de;
      }
      log.exiting(CLASSNAME, "ctor");
   }
   
   public QueryResponse invoke(QueryRequest request) throws DelegateException {
      log.entering(CLASSNAME, "invoke");
      QueryResponse response = null;
      try {
         response = jaxwsPortType.query(request);
      } catch (QueryFaultMsg fault) {
         DelegateException de = new DelegateException(fault);
         de.setJndiName(jndiLocalRef);
         de.setServiceName("CCService");
         throw de;
      }
      log.exiting(CLASSNAME, "invoke");
      return response;
   }

}
