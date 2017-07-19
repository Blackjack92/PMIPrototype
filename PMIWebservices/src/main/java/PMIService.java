import com.serialization.AttributeCertificateRequest;
import com.serialization.ObjectDeserializer;
import org.bouncycastle.asn1.x509.AttributeCertificate;
import org.bouncycastle.cert.X509AttributeCertificateHolder;
import org.bouncycastle.operator.OperatorCreationException;
import org.jscep.client.ClientException;
import com.mysql.jdbc.Driver;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.awt.*;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.sql.SQLException;
import java.util.Arrays;

@Path("pmi")
public class PMIService {

    private final PMIManagement pmi;

    public PMIService() throws SQLException, ClassNotFoundException {
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
    public void createRequest(@PathParam("request") String request, @Context HttpServletResponse servletResponse) throws Exception {
        String redirectUrl = "../../status/";
        AttributeCertificateRequest parsedRequest = ObjectDeserializer.fromString(request);
        for (String attribute : parsedRequest.getAttributes()) {
            redirectUrl += attribute;
        }

        X509AttributeCertificateHolder holder = pmi.createAttributeCertificate(parsedRequest);

        // 3) send redirect url with holder.encode();
        // Check holder == null if null return failure
        // else
        // redirectUrl += holder.getEncoded();
        servletResponse.sendRedirect(redirectUrl);

    }

    /**
     * Difference to poll is that poll is used with the transaction id, not with serial number
     * @param serialNumber
     * @return
     */
    //getac
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
    public String validate(@PathParam("pkc") String pkc, @PathParam("ac") String ac) throws IOException, ClassNotFoundException {
        // TODO: validate the given ac
         X509Certificate deserializedPKC = ObjectDeserializer.fromString(pkc);
        AttributeCertificate deserializedAC = ObjectDeserializer.fromString(ac);
        return null;
    }
}
