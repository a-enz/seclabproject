package BashHelpers;

import Responses.IssueCertificateResponse;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

// See https://docs.oracle.com/javase/7/docs/api/java/lang/ProcessBuilder.html
// and https://stackoverflow.com/questions/16825001/running-a-bash-command-from-within-a-java-program
// for how to run a bash process
public class BashInterface {

    // Fields for creating a certificate
    private final String COUNTRY_NAME = "CH";
    private final String STATE_NAME = "Zurich";
    private final String LOCALITY_NAME = "Zurich";
    private final String ORGANIZATION_NAME = "iMovies";
    private final String COMMON_NAME = ""; // TODO

    // File paths
    private final String sslDirectory = "/etx/ssl";
    private final String caDirectory = sslDirectory + "/CA";
    private final String indexTxtFile = caDirectory + "/index.txt";

    // TODO: handle output
    // Note: every single argument must be passed to ProcessBuilder separately!
    private void executeCommand(String ... command) throws java.io.IOException {
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectOutput(ProcessBuilder.Redirect.appendTo(new File("file1.txt")));
        pb.redirectError(ProcessBuilder.Redirect.appendTo(new File("error1.txt")));
        Process p = pb.start();
    }

    private String subj(String userId) {
        return "/C=CH/ST=Zurich/L=Zurich/O=iMovies/OU=IT Department/CN=" + userId;
    }

    // TODO: currently not checking certificate fileds (policy_anything) and using -batch option
    // TODO: replace openssl.cnf policy and generate ca certificate with java to have it match encoding
    //executeCommand("openssl", "req", "-new", "-x509", "-extensions", "v3_ca", "-keyout", "cakey.pem", "-out", "cacert.pem", "-days", "3650", "-subj", subj, "-passin", "pass:test");
    public IssueCertificateResponse generatePrivateKeyAndCertificatePKCS12(String userId) throws java.io.IOException, InterruptedException {
        // Generate new private key
        executeCommand("openssl", "genrsa", "-out", userId + ".key", "2048");
        // TODO: improve waiting
        TimeUnit.SECONDS.sleep(2);
        // Create certificate signing request
        executeCommand("openssl", "req", "-new", "-key", userId + ".key", "-out", userId + ".csr", "-subj", subj(userId));
        // TODO: improve waiting
        TimeUnit.SECONDS.sleep(2);
        // Sign certificate
        executeCommand("openssl", "ca", "-batch", "-in", userId + ".csr", "-config", "./ssl/openssl.cnf", "-subj", subj(userId), "-passin", "pass:test");
        // TODO: Convert private key in PKCS#12
        String convertedKey = "convertedKey";
        // TODO: Convert certificate key in PKCS#12
        String convertedCertificate = "convertedCert";
        // TODO: Delete private key
        //executeCommand("openssl", "pkcs12", "-export", "-in", "TODO: serial.pem", "-inkey", "TODO: key.key", "-out");
        // TODO Delete signing request
        return new IssueCertificateResponse(convertedKey, convertedCertificate);
    }

    // Bash command: openssl genrsa -out <name>.key 2048
    // TODO
    private void generatePrivateKey(String userId) throws java.io.IOException {
        executeCommand("openssl genrsa -out " + userId + ".key 2048");
    }

    // Bash command: openssl req -new -key <name>.key -out <name>.csr
    // TODO
    private void generateSigningRequest(String userId) throws java.io.IOException {
        executeCommand("openssl req -new -key " + userId + ".key -out " + userId + ".csr");
    }

    // Bash command: sudo openssl ca -in <name>.csr -config /etc/ssl/openssl.cnf
    // TODO
    private void signCertificate(String userId) throws java.io.IOException {
        executeCommand("sudo openssl ca -in " + userId + ".csr -config /etc/ssl/openssl.cnf");
    }

    // Bash command: sudo openssl ca -revoke <name>.crt -config /etc/ssl/openssl.cnf
    // TODO
    public void revokeCertificate() {

    }

    // Bash command: sudo openssl ca -gencrl -out /etc/ssl/CA/crl/crl.pem
    // TODO
    public void createRevocationList() {

    }

    // Bash command: openssl pkcs12 -export -in <name>.pem -inkey <name>.key -out <name>.p12 -name "<certname>"
    // TODO
    public void exportCertToPKCS12() {

    }

    // Bash command: sudo wc -l < indexTxtFile
    // TODO
    public Integer getIssuedSize() throws IOException {
        ProcessBuilder pb = new ProcessBuilder("/bin/echo", "THIS IS STUFF FOR THE FILE");
        pb.redirectOutput(ProcessBuilder.Redirect.appendTo(new File("file1.txt")));
        Process p = pb.start();
        return 0;
    }

    // Bash command:
    // TODO
    public Integer getRevokedSize() {
        return 0;
    }

    // Bash command:
    // TODO
    public Integer getCurrentSerial() {
        return 0;
    }
}
