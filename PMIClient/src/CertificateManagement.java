import com.serialization.AttributeCertificateRequest;
import com.serialization.KeyPairReader;
import com.serialization.ObjectDeserializer;
import com.serialization.ObjectSerializer;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
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
import java.io.*;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class name: ${CLASS_NAME}
 * Created by kevin on 09.05.17.
 */
class CertificateManagement {

    void createCertificateRequest(String subject, String pubFileName, String privFileName) throws Exception {
        // Example subject: "M"
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
            System.out.println("RequestString: " + matcher.group(5) + "/" + matcher.group(4));
        }
    }

    void pollCertificate(String subject, String transactionId) throws IOException {
        pollCertificate(subject + "/" + transactionId);
    }

    void pollCertificate(String requestString) throws IOException {
        HttpClient client = new DefaultHttpClient();
        String url = "http://localhost:8080/PMITest_war_exploded/pki/poll/" + requestString;
        System.out.println("Url: " + url);
        HttpGet get = new HttpGet(url);
        HttpResponse response = client.execute(get);
        X509Certificate certificate = convertToCertificate(response);
        System.out.println(certificate == null ? "No certificate found." : certificate);
        storeCertificateDialog(certificate);
    }

    void getCertificate(String serialNumber) throws IOException, ClassNotFoundException {
        HttpClient client = new DefaultHttpClient();
        String url = "http://localhost:8080/PMITest_war_exploded/pki/get/" + serialNumber;
        System.out.println("Url: " + url);
        HttpGet get = new HttpGet(url);
        HttpResponse response = client.execute(get);
        X509Certificate certificate = convertToCertificate(response);
        System.out.println(certificate == null ? "No certificate found." : certificate);
        storeCertificateDialog(certificate);
    }

    private void storeCertificateDialog(X509Certificate certificate) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Store certificate [Y/N]?");
        boolean decided = false;
        while(!decided) {
            String text = scanner.nextLine();
            if ("Y".equals(text)) {
                storeCertificate(certificate);
                return;
            } else if ("N".equals(text)) {
                return;
            }
        }
    }

    void storeCertificate(X509Certificate certificate) {
        try {
            System.out.print("Enter file name:");
            String filename = new Scanner(System.in).nextLine();
            Writer writer = new FileWriter(filename);
            JcaPEMWriter jcaPemWriter = new JcaPEMWriter(writer);
            jcaPemWriter.writeObject(certificate);
            jcaPemWriter.flush();
            jcaPemWriter.close();
            System.out.println("Certificate written to: " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    X509Certificate readCertificate(String fileName) {
        try {

            CertificateFactory fact = CertificateFactory.getInstance("X.509");
            FileInputStream is = new FileInputStream (fileName);
            X509Certificate certificate = (X509Certificate) fact.generateCertificate(is);
            return certificate;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        }

        return null;
    }
    void validateCertificate(String certificateFileName) {
        X509Certificate certificate = readCertificate(certificateFileName);
        if (certificate == null) {
            System.out.println("Could not read the certificate.");
        } else {
            HttpClient client = new DefaultHttpClient();
            try {
                String serializedCertificate = ObjectSerializer.toString(certificate);
                String url = "http://localhost:8080/PMITest_war_exploded/pki/validate/" + serializedCertificate;
                System.out.println("Url: " + url);
                HttpGet get = new HttpGet(url);
                HttpResponse response = client.execute(get);
                printResponse(response, "No validation result returned.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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

    private static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
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

    private X509Certificate convertToCertificate(HttpResponse response) {
        try {
            String content = convertStreamToString(response.getEntity().getContent());
            String serializedCertificate = content.replace("=", "");
            return ObjectDeserializer.fromString(serializedCertificate);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
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

    public void createAttributeCertificateRequest(String certificateFilename, String attribute) throws CertificateException, IOException {
        // Read certificate
        //X509Certificate certificate = null;
        X509Certificate certificate = readCertificate(certificateFilename);
        if (certificate == null) {
            System.out.println("No Certificate found.");
        } else {
            System.out.println(certificate);
        }

        AttributeCertificateRequest request = new AttributeCertificateRequest(certificate, new String[]{attribute});

        HttpClient client = new DefaultHttpClient();
        try {
            String serializedRequest = ObjectSerializer.toString(request);
            String url = "http://localhost:8080/PMITest_war_exploded/pmi/request/create/" + serializedRequest;
            System.out.println("Url: " + url);
            HttpPost post = new HttpPost(url);
            HttpResponse response = client.execute(post);
            printResponse(response, "No attribute certificate issued.");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

