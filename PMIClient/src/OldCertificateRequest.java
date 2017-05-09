import com.serialization.ObjectSerializer;
import com.serialization.SimpleCertificate;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by kevin on 03.05.17.
 */
public class OldCertificateRequest {
    public static void main(String[] args) throws ClientProtocolException, IOException {
        /* HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet("http://141.28.105.137:8080/pmi/cacertificates");
        HttpResponse response = client.execute(request);
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String line = "";
        while ((line = rd.readLine()) != null) {
            System.out.println(line);
        } */

        SimpleCertificate certificate = new SimpleCertificate("1234", "pubkey", "John Doe");

        HttpClient client = new DefaultHttpClient();
        //HttpPost post = new HttpPost("http://localhost:8080/PMITest_war_exploded/pkc/request/create/" + ObjectSerializer.toString(certificate));
        String test = "http://localhost:8080/PMITest_war_exploded/pkc/request/create/" + ObjectSerializer.toString(certificate);
        HttpPost post = new HttpPost(test);
        HttpResponse response = client.execute(post);
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String line = "";
        while ((line = rd.readLine()) != null) {
            System.out.println(line);
        }
    }
}
