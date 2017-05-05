import com.serialization.ObjectDeserializer;
import com.serialization.ObjectSerializer;
import com.serialization.SimpleCertificate;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

        } catch (IOException e) {
            fail(e.getMessage());
        } catch (ClassNotFoundException e) {
            fail(e.getMessage());
        }
    }
}
