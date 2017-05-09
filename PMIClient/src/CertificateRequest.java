import com.serialization.ObjectSerializer;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;

import javax.security.auth.x500.X500Principal;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

/**
 * Class name: ${CLASS_NAME}
 * Created by kevin on 08.05.17.
 */
public class CertificateRequest {

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, OperatorCreationException {
        HttpClient client = new DefaultHttpClient();
        //HttpPost post = new HttpPost("http://localhost:8080/PMITest_war_exploded/pkc/request/create/" + ObjectSerializer.toString(certificate));

        KeyPair keyPair = createRandomKeyPair();
        PKCS10CertificationRequest csr = createCSR(keyPair);
        String serializedCSR = ObjectSerializer.toString(csr);
        String url = "http://localhost:8080/PMITest_war_exploded/pki/request/create/" + serializedCSR;
        HttpPost post = new HttpPost(url);
        HttpResponse response = client.execute(post);
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String line = "";
        while ((line = rd.readLine()) != null) {
            System.out.println(line);
        }

    }

    private static PKCS10CertificationRequest createCSR(KeyPair requestKeyPair) throws OperatorCreationException, NoSuchAlgorithmException {
        X500Principal entitySubject = new X500Principal("CN=jscep.org, L=Cardiff, ST=Wales, C=UK");
        //X500Principal entitySubject = new X500Principal("CN=jscep.org, L=Cardiff, ST=Wales, C=UK");
        PKCS10CertificationRequestBuilder csrBuilder =
                new JcaPKCS10CertificationRequestBuilder(entitySubject, requestKeyPair.getPublic());

        // Add attributes to the request
        // DERPrintableString password = new DERPrintableString("SecretChallenge");
        // csrBuilder.addAttribute(PKCSObjectIdentifiers.pkcs_9_at_challengePassword, password);

        // Sign the request
        JcaContentSignerBuilder csrSignerBuilder = new JcaContentSignerBuilder("SHA1withRSA");
        ContentSigner csrSigner = csrSignerBuilder.build(requestKeyPair.getPrivate());
        return csrBuilder.build(csrSigner);
    }

    private static KeyPair createRandomKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(1024);
        return keyPairGenerator.genKeyPair();
    }
}