import com.serialization.AttributeCertificateRequest;
import org.bouncycastle.cert.X509AttributeCertificateHolder;

/**
 * Created by kevin on 12.05.2017.
 */
public class PMIManagement {

    public X509AttributeCertificateHolder createAttributeCertificate(AttributeCertificateRequest parsedRequest) {
        // 1) Validate Certificate
        // 2) Valdiate attributes

        // On success: -->
        // 1) Create ACHolder
        // 2) Store ACHolder in DB
        // 3) return holder
        return null;
    }
}
