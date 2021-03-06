//
// Generated By:JAX-WS RI IBM 2.2.1-11/25/2013 11:48 AM(foreman)- (JAXB RI IBM 2.2.3-11/25/2013 12:35 PM(foreman)-)
//


package org.acme.cc.jaxws;

import javax.xml.ws.WebFault;

@WebFault(name = "QueryFault", targetNamespace = "urn:issw:bare:wssec:cc:query")
public class QueryFaultMsg
    extends Exception
{

    /**
     * Java type that goes as soapenv:Fault detail element.
     * 
     */
    private QueryFault faultInfo;

    /**
     * 
     * @param message
     * @param faultInfo
     */
    public QueryFaultMsg(String message, QueryFault faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @param message
     * @param faultInfo
     * @param cause
     */
    public QueryFaultMsg(String message, QueryFault faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @return
     *     returns fault bean: org.acme.cc.jaxws.QueryFault
     */
    public QueryFault getFaultInfo() {
        return faultInfo;
    }

}
