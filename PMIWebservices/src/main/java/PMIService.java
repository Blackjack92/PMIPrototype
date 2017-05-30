import org.bouncycastle.asn1.x509.AttributeCertificate;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 * Created by kevin on 04.05.17.
 */
@Path("pmi")
public class PMIService {

    private final PMIManagement pmi;

    public PMIService() {
        pmi = new PMIManagement();
    }

    @GET
    @Path("status/{message}")
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.TEXT_PLAIN)
    public String status(@PathParam("message") String message) {
        return message;
    }

    @POST
    @Path("request/create/{request}")
    @Consumes(MediaType.TEXT_PLAIN)
    public void createRequest(@FormParam("request") String request, @Context HttpServletResponse servletResponse) {
        // TODO: implement create request
    }

    /**
     * Difference to poll is that poll is used with the transaction id, not with serial number
     * @param serialNumber
     * @return
     */
    @GET
    @Path("get/{serialNumber}")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String get(@PathParam("serialNumber") String serialNumber) {
        // TODO: implement get
        return "Not supported at the moment.";
    }

    @GET
    @Path("poll/{transactionId}")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String poll(@PathParam("transactionId") String transactionId) {
        // TODO: implement poll
        return "Not supported at the moment.";
    }

    @DELETE
    @Path("revoke/{serialNumber}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void revoke(@PathParam("serialNumber") String serialNumber) {
        // TODO: email to administrator
    }

    @GET
    @Path("validate/{pkc}/{ac}")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String validate(@PathParam("pkc") String pkc, @PathParam("ac") String ac) {
        // TODO: validate the given ac
        // X509Certificate deserializedPKC = ObjectDeserializer.fromString(pkc);
        // AttributeCertificate deserializedAC = ObjectDeserializer.fromString(ac);
        return null;
    }
}
