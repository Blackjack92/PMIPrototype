import com.serialization.AttributeCertificateRequest;
import com.serialization.KeyPairReader;
import org.bouncycastle.cert.AttributeCertificateHolder;
import org.bouncycastle.cert.X509AttributeCertificateHolder;
import org.bouncycastle.cert.X509v2AttributeCertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaAttributeCertificateIssuer;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.jscep.client.Client;
import org.jscep.client.ClientException;
import org.omg.PortableServer.SERVANT_RETENTION_POLICY_ID;
import sun.security.x509.X509CertImpl;
import validation.CertificateValidator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.*;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by kevin on 12.05.2017.
 */
public class PMIManagement {
   // private final Client client;
   //int serial = Database.GetNextFreeSerialNumber();

    private HashMap<BigInteger, List<String>> allowedAttributes = new HashMap<>();
    public PMIManagement() throws SQLException, ClassNotFoundException {
        // 373990605818127595288063
        List<String> attributes = new ArrayList<>();
        attributes.add("Room1");
        attributes.add("Room2");
        attributes.add("Room3");
        allowedAttributes.put(new BigInteger("373990605818127595288063"), attributes);
    }

    public X509AttributeCertificateHolder createAttributeCertificate(AttributeCertificateRequest parsedRequest) throws Exception {
        if (parsedRequest == null || parsedRequest.getCertificate() == null) { return null; }

        // 1) Validate Certificate
        PKIManagement pki = new PKIManagement();
        String validationResult = pki.validateCertificate( parsedRequest.getCertificate());
        //if (validationResult );
        if (validationResult.equals("Validation was successful.\n")){

        }else {return null;}


        // 2) Validate attributes

        BigInteger serialNumber = parsedRequest.getCertificate().getSerialNumber();

        boolean requestedAttributesAllowed = true;
        if (allowedAttributes.containsKey(serialNumber)) {
            List<String> attributes = allowedAttributes.get(serialNumber);
            String[] requestedAttributes = parsedRequest.getAttributes();
            for (String s : requestedAttributes) {
                requestedAttributesAllowed &= attributes.stream().anyMatch(attr -> attr.equals(s));
            }
        }

        if (!requestedAttributesAllowed) { return null; }
        parsedRequest.getCertificate().getPublicKey();


        //read cacertificate

        FileInputStream in = new FileInputStream("/home/rz/Dokumente/PMIPrototype/PMIAAkeys/cert.pem");
        CertificateFactory factory = CertificateFactory.getInstance("X.509");
        X509Certificate cert = (X509Certificate) factory.generateCertificate(in);
        PublicKey capubkey = cert.getPublicKey();
        PrivateKey caprivkey = KeyPairReader.readPrivateKey("/home/rz/Dokumente/PMIPrototype/PMIAAkeys/key.der");

        X509v2AttributeCertificateBuilder acBuilder = new X509v2AttributeCertificateBuilder(
                new AttributeCertificateHolder(new JcaX509CertificateHolder(parsedRequest.getCertificate())),
                new JcaAttributeCertificateIssuer(cert),
                new BigInteger(String.valueOf(12)),
                new Date(System.currentTimeMillis() - 50000),
                new Date(System.currentTimeMillis() + 50000));
        Security.addProvider(new BouncyCastleProvider());
        // -->> priv Key vom cacertificate nicht erreichbar
        X509AttributeCertificateHolder att = acBuilder.build(new JcaContentSignerBuilder("SHA1WithRSA").setProvider("BC").build(caprivkey));
        System.out.println();

//X509AttributeCertificateHolder att = acBuilder
        // On success: -->
        // 1) Create ACHolder
        // 2) Store ACHolder in DB
        // 3) return holder
        return att;
    }
//    String validateCertificate(X509Certificate certificateToValidate) throws ClientException {
//
//        String validationResult = "";
//
//        try {
//            CertStore caCertStore  = client.getCaCertificate();
//            Collection<? extends Certificate> certificates = caCertStore.getCertificates(null);
//            Set<X509Certificate> caCertificates = certificates.stream().map(c -> (X509Certificate)c).collect(Collectors.toSet());
//            PKIXCertPathBuilderResult pathBuilderResult = CertificateValidator.verifyCertificate(certificateToValidate, caCertificates);
//            validationResult += "Validation was successful.\n";
//            validationResult += pathBuilderResult;
//        } catch (Exception e) {
//            e.printStackTrace();
//            validationResult += "Validation was not successful.\n";
//        }
//
//        return validationResult;
//    }
}
