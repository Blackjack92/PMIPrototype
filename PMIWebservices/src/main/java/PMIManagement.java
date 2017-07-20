import com.serialization.AttributeCertificateRequest;
import com.serialization.KeyPairReader;
import org.bouncycastle.asn1.x509.X509AttributeIdentifiers;
import org.bouncycastle.cert.AttributeCertificateHolder;
import org.bouncycastle.cert.X509AttributeCertificateHolder;
import org.bouncycastle.cert.X509v2AttributeCertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaAttributeCertificateIssuer;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.asn1.x509.X509AttributeIdentifiers;
import org.bouncycastle.asn1.x509.RoleSyntax;
import org.bouncycastle.asn1.*;

import java.sql.ResultSet;
import java.util.Base64;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.jscep.client.Client;
import org.jscep.client.ClientException;
import org.omg.PortableServer.SERVANT_RETENTION_POLICY_ID;
import org.bouncycastle.asn1.x509.GeneralName;
import sun.security.x509.X509CertImpl;
import validation.CertificateValidator;
//import org.bouncycastle.util.encoders.Base64;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
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

public class PMIManagement {
   // private final Client client;
   Database database = new Database();
    X509AttributeCertificateHolder att;
   //int serial = database.GetNextFreeSerialNumber();

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
                new BigInteger(String.valueOf(1)),
                new Date(System.currentTimeMillis() - 50000),
                new Date(System.currentTimeMillis() + 50000));
        //Provider hinzufügen
        Security.addProvider(new BouncyCastleProvider());
        //Aktuelle Attribute
        GeneralName attributes = new GeneralName(GeneralName.uniformResourceIdentifier, "Room1");
        acBuilder.addAttribute(X509AttributeIdentifiers.id_at_role, new RoleSyntax(attributes));
        //Erzeuge Attribut Zertifikat
        att = acBuilder.build(new JcaContentSignerBuilder("SHA1WithRSA").setProvider("BC").build(caprivkey));
        BigInteger acSerial = att.getSerialNumber();
        BigInteger pkcSerial = att.getHolder().getSerialNumber();
        String ac = Base64.getUrlEncoder().encodeToString(att.getEncoded());
        //database.inserting2(acSerial,pkcSerial,ac);
        database.inserting(acSerial,pkcSerial,ac);
        return att;
    }
    public static String getFileContent(FileInputStream fis) throws IOException
    {
        try( BufferedReader br =
                     new BufferedReader( new InputStreamReader(fis, "UTF-8" )))
        {
            StringBuilder sb = new StringBuilder();
            String line;
            while(( line = br.readLine()) != null ) {
                sb.append( line );
                sb.append( '\n' );
            }
            return sb.toString();
        }
    }
    void storeCertificate(X509AttributeCertificateHolder att) {
        try {
            Writer writer = new FileWriter("/home/rz/Dokumente/PMIPrototype/ac.pem");
            JcaPEMWriter jcaPemWriter = new JcaPEMWriter(writer);
            jcaPemWriter.writeObject(att);
            jcaPemWriter.flush();
            jcaPemWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
