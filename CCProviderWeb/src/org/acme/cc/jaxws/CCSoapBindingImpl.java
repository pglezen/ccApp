package org.acme.cc.jaxws;

import java.security.Principal;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.xml.ws.WebServiceContext;

@javax.jws.WebService(endpointInterface = "org.acme.cc.jaxws.CCPortType", targetNamespace = "urn:issw:bare:wssec:cc:query", serviceName = "CCService", portName = "CCService", wsdlLocation = "WEB-INF/wsdl/ccQuery.wsdl")
public class CCSoapBindingImpl {
   
   private static final String CLASSNAME = CCSoapBindingImpl.class.getName();
   private static final Logger log = Logger.getLogger(CLASSNAME);
   
   @Resource WebServiceContext wsCtx;

   public QueryResponse query(QueryRequest request) throws QueryFaultMsg {
      log.entering(CLASSNAME, "query");
      Principal principal = wsCtx.getUserPrincipal();
      if (principal != null) {
         String userName = principal.getName();
         if (userName != null) {
            log.fine("SE Impl is running as user: " + userName);
         } else {
            log.fine("SE Impl user name field on principal returned null.");
         }
      } else {
         log.fine("SE Impl WebServiceContext.getUserPrincipal() returned null.");
      }
      QueryResponse response = new QueryResponse();
      String ccNo = request.getCcNo();
      if (ccNo == null || ccNo.length() == 0) {
         QueryFault qf = new QueryFault();
         qf.setCcNo("");
         qf.setTxnId(1234);
         QueryFaultMsg ex = new QueryFaultMsg("Missing CC number", qf);
         throw ex;
      } else {
         response.setCcNo(ccNo);
         response.setAcctNo("d392492");
         response.setFirstName("John");
         response.setLastName(request.getLastName());
         response.setBalance(100);
      }
      log.exiting(CLASSNAME, "query");
      return response;
   }
}