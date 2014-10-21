#WS Client Bindings - No EJB or @WebServiceRef

The branch demonstrates how to code a web.xml
deployment decriptor entry for a web service
client in such a way that it becomes a managed
client for the purpose of applying WS-Security
policy sets and bindings.
It avoids the `@WebServiceRef` annotation not because
they are evil, but because that was not practical for
the particular circumstance that was presented.

In order to get the client bindings to show up in the
admin console, I had to add a `service-qname` element
to the `service-ref` element of the deployment descriptor.
If clone this repository locally, it's helpful to run
the following diff command to see what I changed from
using an annotation with an EJB versus a web project
deployment descriptor.

```
   git diff master...noejb -- CCConsumer*
```

This "symmetric difference" notation in Git says
"show me the changes on the noejb branch since splitting
from master."  The rest of the command limits the paths
to exclude this `readme.md` file.

I refrained from deleting the EJB project from this branch
so that it would be available for comparison.  It has no
role in this branch and will light-up with all kinds of
errors in RAD.  Since it's not part of the EAR project,
it doesn't interfere with exports.
