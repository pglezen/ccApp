package org.acme.cc.delegate;

import org.acme.cc.jaxws.QueryRequest;
import org.acme.cc.jaxws.QueryResponse;

public interface AccountQuery {
   QueryResponse invoke(QueryRequest request);
}
