import javax.ws.rs.*;

/**
 * Created by kevin on 04.05.17.
 */

@Path("pkc")
public class PKCService {

    @POST
    @Path("request/create")
    public void createRequest(@FormParam("request") String request) {

    }

    @DELETE
    @Path("request/revoke")
    public void revokeRequest(@FormParam("request") String request) {

    }

    @GET
    @Path("poll")
    public String poll(@FormParam("id") String id) {
        return null;
    }

    @DELETE
    @Path("revoke")
    public void revoke(@FormParam("id") String id) {

    }

    @GET
    @Path("validate")
    public String validate(@FormParam("pkc") String pkc) {
        return null;
    }
}
