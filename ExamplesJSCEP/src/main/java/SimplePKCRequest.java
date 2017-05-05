import org.bouncycastle.asn1.DERPrintableString;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.X509Extension;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;
import org.jscep.client.Client;
import org.jscep.client.ClientException;
import org.jscep.client.DefaultCallbackHandler;
import org.jscep.client.EnrollmentResponse;
import org.jscep.client.verification.CachingCertificateVerifier;
import org.jscep.client.verification.CertificateVerifier;
import org.jscep.client.verification.ConsoleCertificateVerifier;
import org.jscep.transaction.TransactionException;
import org.jscep.transport.response.Capabilities;

import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.x500.X500Principal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by kevin on 05.05.17.
 */
public class SimplePKCRequest {
    public static void main(String[] args) throws MalformedURLException, NoSuchAlgorithmException, OperatorCreationException, CertIOException, CertificateException, ClientException, TransactionException {

        // CallbackHandler
        CertificateVerifier consoleVerifier = new ConsoleCertificateVerifier();
        //CertificateVerifier verifier = new CachingCertificateVerifier(consoleVerifier);
        CallbackHandler handler = new DefaultCallbackHandler(consoleVerifier);

        // JSCEP Server
        URL url = new URL("http://141.28.105.137/scep/scep");
        Client client = new Client(url, handler);

        // KeyPair for the certificate owner
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(1024);
        KeyPair requesterKeyPair = keyPairGenerator.genKeyPair();

        // Usable signature algorithms
        Capabilities caps = client.getCaCapabilities();
        String sigAlg = caps.getStrongestSignatureAlgorithm();

        // Mandatory
        X500Principal requesterIssuer = new X500Principal("CN=jscep.org, L=Cardiff, ST=Wales, C=UK");
        BigInteger serial = BigInteger.ONE;
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1); // yesterday
        Date notBefore = calendar.getTime();
        calendar.add(Calendar.DATE, +2); // tomorrow
        Date notAfter = calendar.getTime();
        X500Principal requesterSubject = new X500Principal("CN=jscep.org, L=Cardiff, ST=Wales, C=UK"); // doesn't need to be the same as issuer
        PublicKey requesterPubKey = requesterKeyPair.getPublic(); // from generated key pair
        JcaX509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(requesterIssuer, serial, notBefore, notAfter, requesterSubject, requesterPubKey);

        // Self Signing
        PrivateKey requesterPrivKey = requesterKeyPair.getPrivate(); // from generated key pair
        JcaContentSignerBuilder certSignerBuilder = new JcaContentSignerBuilder(sigAlg); // from above
        ContentSigner certSigner = certSignerBuilder.build(requesterPrivKey);

        // Certificate request
        X509CertificateHolder certHolder = certBuilder.build(certSigner);
        JcaX509CertificateConverter converter = new JcaX509CertificateConverter();
        X509Certificate requesterCert = converter.getCertificate(certHolder);

        // Create a key pair to communicate with the PKI
        keyPairGenerator.initialize(1024);
        KeyPair entityKeyPair = keyPairGenerator.genKeyPair();

        X500Principal entitySubject = requesterSubject; // use the same subject as the self-signed certificate
        PublicKey entityPubKey = entityKeyPair.getPublic();
        PKCS10CertificationRequestBuilder csrBuilder = new JcaPKCS10CertificationRequestBuilder(entitySubject, entityPubKey);

        DERPrintableString password = new DERPrintableString("SecretChallenge");
        csrBuilder.addAttribute(PKCSObjectIdentifiers.pkcs_9_at_challengePassword, password);

        PrivateKey entityPrivKey = entityKeyPair.getPrivate();
        JcaContentSignerBuilder csrSignerBuilder = new JcaContentSignerBuilder("SHA1withRSA");
        ContentSigner csrSigner = csrSignerBuilder.build(entityPrivKey);
        PKCS10CertificationRequest csr = csrBuilder.build(csrSigner);

        EnrollmentResponse res = client.enrol(requesterCert, requesterPrivKey, csr);
        System.out.println(res.isPending());
    }

}
