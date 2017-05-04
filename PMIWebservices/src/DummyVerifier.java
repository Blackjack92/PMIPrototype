import org.jscep.client.verification.CertificateVerifier;

import java.security.cert.X509Certificate;

/**
 * Created by kevin on 02.05.17.
 */
public class DummyVerifier implements CertificateVerifier {
    @Override
    public boolean verify(final X509Certificate cert) {
        return true;
    }

}
