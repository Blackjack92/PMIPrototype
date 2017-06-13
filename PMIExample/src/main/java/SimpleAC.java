import java.math.BigDecimal;

/**
 * Created by kevin on 12.05.2017.
 */

public class SimpleAC {

    private final BigDecimal pkcSerialNumber;
    private final String[] attributes;
    private final String signature;

    public SimpleAC(BigDecimal pkcSerialNumber, String[] attributes) {
        this.pkcSerialNumber = pkcSerialNumber;
        this.attributes = attributes;
        this.signature = "secureSignature";
    }

    public BigDecimal getPKCSerialNumber() {
        return pkcSerialNumber;
    }

    public String[] getAttributes() {
        return attributes;
    }

    public String getSignature() {
        return signature;
    }

}
