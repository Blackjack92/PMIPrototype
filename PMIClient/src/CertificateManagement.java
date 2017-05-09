import com.serialization.KeyPairReader;
import com.serialization.ObjectSerializer;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.security.auth.x500.X500Principal;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class name: ${CLASS_NAME}
 * Created by kevin on 09.05.17.
 */
class CertificateManagement {

    void createCertificateRequest(String subject, String pubFileName, String privFileName) throws Exception {
        // Example subject: "CN=jscep.org, L=Cardiff, ST=Wales, C=UK"
        // Example public key: "/home/kevin/Projects/JavaProjects/PMI/clientKeys/public_key.der"
        // Example private key: "/home/kevin/Projects/JavaProjects/PMI/clientKeys/private_key.der"

        HttpClient client = new DefaultHttpClient();
        KeyPair keyPair = KeyPairReader.readKeyPair(pubFileName, privFileName);
        PKCS10CertificationRequest csr = createCSR(subject, keyPair);
        String serializedCSR = ObjectSerializer.toString(csr);
        String url = "http://localhost:8080/PMITest_war_exploded/pki/request/create/" + serializedCSR;
        System.out.println("Url: " + url);

        HttpPost post = new HttpPost(url);
        HttpResponse response = client.execute(post);

        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            builder.append(line);
        }

        Document doc = Jsoup.parse(builder.toString());
        Element link = doc.select("a").first();
        String linkHref = link.attr("href");
        System.out.println(linkHref);

        Pattern pattern = Pattern.compile("Success:(\\w+)_Pending:(\\w+)_Failure:(\\w+)_TransId:([a-zA-Z0-9_-]+)_Subject:([a-zA-Z0-9_-]+)");
        Matcher matcher = pattern.matcher(linkHref);
        if (matcher.find())
        {
            // Whole content
            // System.out.println(matcher.group(0));
            System.out.println("IsSuccess: " + matcher.group(1));
            System.out.println("IsPending: " + matcher.group(2));
            System.out.println("IsFailure: " + matcher.group(3));
            System.out.println("TransId: " + matcher.group(4));
            System.out.println("Subject: " + matcher.group(5));
        }
    }

    void pollCertificate(String subject, String transactionId) throws IOException {
        HttpClient client = new DefaultHttpClient();
        String url = "http://localhost:8080/PMITest_war_exploded/pki/poll/" + subject + "/" + transactionId;
        System.out.println("Url: " + url);
        HttpGet get = new HttpGet(url);
        HttpResponse response = client.execute(get);
        printResponse(response, "No certificate found");
    }

    void getCertificate(String serialNumber) throws IOException {
        HttpClient client = new DefaultHttpClient();
        String url = "http://localhost:8080/PMITest_war_exploded/pki/get/" + serialNumber;
        System.out.println("Url: " + url);
        HttpGet get = new HttpGet(url);
        HttpResponse response = client.execute(get);
        printResponse(response, "No certificate found");
    }

    void validateCertificate(String certificateFileName) {
        // TODO: implement validate
        System.out.println("Is not supported at the moment.");
    }

    // Eventually more sense with certificateFileName
    void revokeCertificate(String serialNumber) {
        // TODO: implement revoke
        System.out.println("Is not supported at the moment.");
    }

    void revokeCertificateRequest(String transactionId) {
        // TODO: implement revoke certificate request
        System.out.println("Is not supported at the moment.");
    }

    private void printResponse(HttpResponse response, String emptyResponseMessage) throws IOException {
        if (response.getEntity() == null) {
            System.out.println(emptyResponseMessage);
        } else {
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line;
            while ((line = rd.readLine()) != null) {
                System.out.println(line);
            }
        }
    }

    private PKCS10CertificationRequest createCSR(String subject, KeyPair requestKeyPair) throws OperatorCreationException, NoSuchAlgorithmException {
        X500Principal entitySubject = new X500Principal(subject);
        PKCS10CertificationRequestBuilder csrBuilder = new JcaPKCS10CertificationRequestBuilder(entitySubject, requestKeyPair.getPublic());

        // Sign the request
        JcaContentSignerBuilder csrSignerBuilder = new JcaContentSignerBuilder("SHA1withRSA");
        ContentSigner csrSigner = csrSignerBuilder.build(requestKeyPair.getPrivate());
        return csrBuilder.build(csrSigner);
    }
}
