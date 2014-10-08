package org.acme.cc.ejb;

import java.util.logging.Logger;

import org.acme.cc.delegate.AccountQuery;
import org.acme.cc.delegate.DelegateException;
import org.acme.cc.jaxws.QueryRequest;
import org.acme.cc.jaxws.QueryResponse;

/**
 * Hard-coded mock of {@link org.acme.cc.delegate.AccountQuery} interface.
 */
public class AccountQueryMock implements AccountQuery {
   
   public static final String CLASSNAME = AccountQueryMock.class.getName();
   private static final Logger log = Logger.getLogger(CLASSNAME);

   @Override
   public QueryResponse invoke(QueryRequest request) throws DelegateException {
      log.entering(CLASSNAME, "invoke");
      QueryResponse response = new QueryResponse();
      response.setCcNo(request.getCcNo());
      response.setLastName(request.getLastName());
      response.setAcctNo("mocked12345");
      response.setFirstName("Mocker");
      response.setBalance(10);
      log.exiting(CLASSNAME, "invoke");
      return response;
   }

}
