import com.serialization.ObjectDeserializer;
import com.serialization.SimpleCertificate;
import org.jscep.client.Client;
import org.jscep.client.DefaultCallbackHandler;
import org.jscep.client.verification.CertificateVerifier;
import org.jscep.client.verification.OptimisticCertificateVerifier;

import javax.security.auth.callback.CallbackHandler;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Class name: ${CLASS_NAME}
 * Created by kevin on 04.05.17.
 */

@Path("pki")
public class PKIService {

    private final CertificateVerifier verifier;
    private final CallbackHandler handler;
    private final URL url;
    private final Client client;

    public PKIService() throws MalformedURLException {
        verifier = new OptimisticCertificateVerifier();
        handler = new DefaultCallbackHandler(verifier);
        url = new URL("http://141.28.105.137/scep/scep");
        client = new Client(url, handler);
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
    public void createRequest(@PathParam("request") String request, @Context HttpServletResponse servletResponse) throws IOException {
        String redirectUrl = "../../status/";

        try {
            SimpleCertificate certificate = ObjectDeserializer.fromString(request);
            redirectUrl += certificate.getSerialNumber();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            redirectUrl += e.getMessage();
        }

        servletResponse.sendRedirect(redirectUrl);
    }

    @DELETE
    @Path("request/revoke/{serialNumber}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void revokeRequest(@PathParam("serialNumber") String request) {

    }

    @GET
    @Path("poll/{serialNumber}")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String poll(@PathParam("serialNumber") String serialNumber) {
        return null;
    }

    @DELETE
    @Path("revoke/{serialNumber}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void revoke(@PathParam("serialNumber") String serialNumber) {

    }

    @GET
    @Path("validate/{pkc}")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String validate(@PathParam("pkc") String pkc) {
        return null;
    }
}
