import com.serialization.KeyPairReader;
import com.serialization.ObjectDeserializer;
import com.serialization.ObjectSerializer;
import com.serialization.SimpleCertificate;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;
import org.junit.jupiter.api.Test;

import javax.security.auth.x500.X500Principal;
import java.io.IOException;
import java.security.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by kevin on 05.05.17.
 */
public class SerializationTest {

    @Test
    void serializeAndDeserialize() {
        SimpleCertificate cert1 = new SimpleCertificate("1234", "secret", "John Doe");
        SimpleCertificate cert2 = new SimpleCertificate("2345", "secret", "Jane Doe");
        try {

            String serializedCert1 = ObjectSerializer.toString(cert1);
            SimpleCertificate deserializedCert1 = ObjectDeserializer.fromString(serializedCert1);
            assertEquals(cert1.getOwner(), deserializedCert1.getOwner());
            assertEquals(cert1.getPublicKey(), deserializedCert1.getPublicKey());
            assertEquals(cert1.getSerialNumber(), deserializedCert1.getSerialNumber());

            String serializedCert2 = ObjectSerializer.toString(cert2);
            SimpleCertificate deserializedCert2 = ObjectDeserializer.fromString(serializedCert2);
            assertEquals(cert2.getOwner(), deserializedCert2.getOwner());
            assertEquals(cert2.getPublicKey(), deserializedCert2.getPublicKey());
            assertEquals(cert2.getSerialNumber(), deserializedCert2.getSerialNumber());

        } catch (IOException | ClassNotFoundException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void readKeyPair() throws Exception {
        String privateFileName = "./out/test/PMIUtils/keys/private_key.der";
        String publicFileName = "./out/test/PMIUtils/keys/public_key.der";

        PrivateKey privateKey = KeyPairReader.readPrivateKey(privateFileName);
        assertNotNull(privateKey);

        PublicKey publicKey = KeyPairReader.readPublicKey(publicFileName);
        assertNotNull(publicKey);

        KeyPair keyPair = KeyPairReader.readKeyPair(publicFileName, privateFileName);
        assertNotNull(keyPair);

        assertEquals(privateKey.getAlgorithm(), keyPair.getPrivate().getAlgorithm());
        assertEquals(privateKey.getFormat(), keyPair.getPrivate().getFormat());

        assertEquals(publicKey.getAlgorithm(), keyPair.getPublic().getAlgorithm());
        assertEquals(publicKey.getFormat(), keyPair.getPublic().getFormat());

        assertEquals(privateKey, keyPair.getPrivate());
        assertEquals(publicKey, keyPair.getPublic());
    }

    @Test
    void serializeCSR() throws NoSuchAlgorithmException, OperatorCreationException, IOException {
        KeyPair requestKeyPair = SerializationTest.createRandomKeyPair();
        X500Principal entitySubject = new X500Principal("CN=jscep.org, L=Cardiff, ST=Wales, C=UK");
        PKCS10CertificationRequestBuilder csrBuilder = new JcaPKCS10CertificationRequestBuilder(entitySubject, requestKeyPair.getPublic());

        // Sign the request
        JcaContentSignerBuilder csrSignerBuilder = new JcaContentSignerBuilder("SHA1withRSA");
        ContentSigner csrSigner = csrSignerBuilder.build(requestKeyPair.getPrivate());
        PKCS10CertificationRequest csr = csrBuilder.build(csrSigner);

        // Serialize to base64 string
        String serialized = ObjectSerializer.toString(csr);
        assertNotNull(serialized);

        // Deserialize from base64 string
        PKCS10CertificationRequest deserialized = ObjectDeserializer.fromCSRString(serialized);
        assertNotNull(deserialized);

        assertTrue(csr.equals(deserialized));
    }

    private static KeyPair createRandomKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(1024);
        return keyPairGenerator.genKeyPair();
    }

}
