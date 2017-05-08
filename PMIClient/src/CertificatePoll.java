import com.serialization.ObjectSerializer;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyPair;

/**
 * Class name: ${CLASS_NAME}
 * Created by kevin on 08.05.17.
 */
public class CertificatePoll {

    public static void main(String[] args) throws IOException {
        HttpClient client = new DefaultHttpClient();
        //HttpPost post = new HttpPost("http://localhost:8080/PMITest_war_exploded/pkc/request/create/" + ObjectSerializer.toString(certificate));

        // Example
        String transactionId = "rO0ABXNyACNvcmcuanNjZXAudHJhbnNhY3Rpb24uVHJhbnNhY3Rpb25JZLcq6UIUcqIQAgABWwACaWR0AAJbQnhwdXIAAltCrPMX-AYIVOACAAB4cAAAACg0YTU0NTlhZDQ5M2ExZDg1ZDJkNGFmNTY5ZjdmNzk1NjQ0YjM5YmY2";
        String principal = "rO0ABXNyACZqYXZheC5zZWN1cml0eS5hdXRoLng1MDAuWDUwMFByaW5jaXBhbPkN_zyIuHfHAwAAeHB1cgACW0Ks8xf4BghU4AIAAHhwAAAARTBDMQswCQYDVQQGEwJVSzEOMAwGA1UECBMFV2FsZXMxEDAOBgNVBAcTB0NhcmRpZmYxEjAQBgNVBAMTCWpzY2VwLm9yZ3g";

        String test = "http://localhost:8080/PMITest_war_exploded/pki/poll/" + principal + "/" + transactionId;
        HttpGet post = new HttpGet(test);
        HttpResponse response = client.execute(post);

        if (response.getEntity() == null) {
            System.out.println("No certificate found");
        } else {
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line = "";
            while ((line = rd.readLine()) != null) {
                System.out.println(line);
            }
        }
    }

}
