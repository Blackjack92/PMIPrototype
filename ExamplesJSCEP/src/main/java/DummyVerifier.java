import org.apache.commons.codec.binary.Hex;
import org.jscep.client.verification.CertificateVerifier;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.*;

import static java.security.Security.getAlgorithms;

/**
 * Created by kevin on 02.05.17.
 */
public class DummyVerifier implements CertificateVerifier {
    @Override
    public boolean verify(final X509Certificate cert) {
        /*
        final List<String> algs = new ArrayList<String>(
                getAlgorithms(MessageDigest.class.getSimpleName()));
        Collections.sort(algs);
        int max = 0;
        for (final String alg : algs) {
            if (alg.length() > max) {
                max = alg.length();
            }
        }

        for (final String alg : algs) {
            MessageDigest digest;
            try {
                digest = MessageDigest.getInstance(alg);
            } catch (NoSuchAlgorithmException e) {
                return false;
            }
            byte[] hash;
            try {
                hash = digest.digest(cert.getEncoded());
            } catch (CertificateEncodingException e) {
                return false;
            }
            System.out.format("%" + max + "s: %s%n", alg,
                    Hex.encodeHexString(hash));
        }
*/
        return true;
    }

}
