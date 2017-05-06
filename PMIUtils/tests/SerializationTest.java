import com.serialization.KeyPairReader;
import com.serialization.ObjectDeserializer;
import com.serialization.ObjectSerializer;
import com.serialization.SimpleCertificate;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

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

}
