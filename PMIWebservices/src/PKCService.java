import com.serialization.ObjectDeserializer;
import com.serialization.SimpleCertificate;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

/**
 * Created by kevin on 04.05.17.
 */

@Path("pkc")
public class PKCService {

    @GET
    @Path("status/{value}")
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String status(@PathParam("value") String value) {
        return value;
    }

    @POST
    @Path("request/create/{request}")
    @Consumes(MediaType.TEXT_PLAIN)
    public void createRequest(@PathParam("request") String request, @Context HttpServletResponse servletResponse) {

        try {
            SimpleCertificate certificate = ObjectDeserializer.fromString(request);
            servletResponse.sendRedirect("../../status/" + certificate.getSerialNumber());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
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
