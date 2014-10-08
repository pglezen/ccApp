package org.acme.cc.delegate;

public class DelegateException extends RuntimeException {

   private static final long serialVersionUID = 1L;
   private String serviceName = "";
   private String jndiName = "";
   
   public DelegateException() {}
   
   public DelegateException(Throwable t) {
      super(t);
   }

   @Override
   public String getMessage() {
      String msg = "Service name: " + serviceName + ", jndiName: " + jndiName
                 + "; " + super.getMessage();
      return msg;
   }

   public String getServiceName() {
      return serviceName;
   }

   public void setServiceName(String serviceName) {
      this.serviceName = serviceName;
   }

   public String getJndiName() {
      return jndiName;
   }

   public void setJndiName(String jndiName) {
      this.jndiName = jndiName;
   };   
}
