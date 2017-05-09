import org.apache.commons.cli.*;

import java.util.Scanner;

/**
 * Class name: ${CLASS_NAME}
 * Created by kevin on 09.05.17.
 */
public class Main {

    public static void main(String[] args) throws ParseException {
        CertificateManagement cm = new CertificateManagement();

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        Options options = new ConsoleOptions().getOptions();
        Scanner scanner = new Scanner(System.in);

        System.out.println("PMIClient started...");
        while (true) {
            String test = scanner.nextLine();
            String[] input = test.split(" ");
            try {
                CommandLine cmd = parser.parse(options, input, true);

                if (cmd.hasOption("q")) {
                    scanner.close();
                    System.out.println("Program quit.");
                    System.exit(0);
                    return;
                } else if (cmd.hasOption("r")) {
                    System.out.print("Enter subject:");
                    String subject = scanner.nextLine();
                    System.out.print("Enter public key filename:");
                    String pubFileName = scanner.nextLine();
                    System.out.print("Enter private key filename:");
                    String privFileName = scanner.nextLine();
                    cm.createCertificateRequest(subject, pubFileName, privFileName);
                } else if (cmd.hasOption("h")) {
                    formatter.printHelp("PMIClient", options);
                } else if (cmd.hasOption("p")) {
                    //System.out.print("Enter transaction id:");
                    //String transactionId = scanner.nextLine();
                    //System.out.println("Enter subject:");
                    //String subject = scanner.nextLine();
                    //cm.pollCertificate(subject, transactionId);
                    System.out.print("Enter request string:");
                    String requestString = scanner.nextLine();
                    cm.pollCertificate(requestString);
                } else if (cmd.hasOption("g")) {
                    System.out.print("Enter serial number:");
                    String serialNumber = scanner.nextLine();
                    cm.getCertificate(serialNumber);
                } else if (cmd.hasOption("v")) {
                    // TODO: implement validate
                    cm.validateCertificate(null);
                } else if (cmd.hasOption("k")) {
                    // TODO: implement revoke certificate
                    cm.revokeCertificate(null);
                } else if (cmd.hasOption("q")) {
                    // TODO: implement revoke certificate request
                    cm.revokeCertificateRequest(null);
                }
            } catch (Exception e) {
                e.printStackTrace();
                formatter.printHelp("PMIClient", options);
            }
        }
    }
}
