import org.jscep.client.Client;
import org.jscep.client.DefaultCallbackHandler;
import org.jscep.client.verification.CertificateVerifier;

import javax.security.auth.callback.CallbackHandler;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.URL;
import java.security.cert.CertStore;
import java.security.cert.CollectionCertStoreParameters;

/**
 * Created by kevin on 02.05.17.
 */
@Path("/certificates")
public class CACertificates {

    @GET
    @Path("ca")
    @Produces(MediaType.TEXT_PLAIN)
    public String getCertificates() {

        String result = "";

        try {

            URL url = new URL("http://141.28.105.137/scep/scep");
            // URL url = new URL("http://localhost/scep/scep");
            CertificateVerifier verifier = new DummyVerifier(); // new ConsoleCertificateVerifier();
            CallbackHandler handler = new DefaultCallbackHandler(verifier);

            Client client = new Client(url, handler);
            CertStore caCertificate = client.getCaCertificate();
            // System.out.println(caCertificate);

            for (Object o : (((CollectionCertStoreParameters) caCertificate.getCertStoreParameters()).getCollection())) {
                result += "Certificate:";
                result += o;
                result += "\n";
            }
        } catch (Exception ex) {
            return "Exception " + ex;
        }


        // Return some cliched textual content
        return result;
    }

    @GET
    @Path("pkcrequest/{requestid}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getPKCRequest(@PathParam("requestid") String username) {
        return "The username is: " + username;
    }
/*
    @POST
    @Path("pkc")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void createPKCRequest(@FormParam("") String name, @Context HttpServletResponse servletResponse) throws IOException {
        servletResponse.sendRedirect("../certificates/pkcrequest/" + String.valueOf(Math.random()));
    }
*/
}
