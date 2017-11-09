package BashHelpers;

import java.io.File;
import java.io.IOException;

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
    private void executeCommand(String ... command) throws java.io.IOException {
        ProcessBuilder pb = new ProcessBuilder(command);
        //pb.redirectOutput(ProcessBuilder.Redirect.appendTo(new File("file1.txt")));
        Process p = pb.start();
    }

    public String generatePrivateKeyAndCertificatePKCS12(String userId) throws java.io.IOException {
        executeCommand("openssl genrsa -out " + userId + ".key 2048");
        //executeCommand("openssl req -new -key " + userId + ".key -out " + userId + ".csr");
        //executeCommand("sudo openssl ca -in " + userId + ".csr -config /etc/ssl/openssl.cnf");
        // TODO: convert in PKCS#12
        return "PKCS#12 key and certificate";
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
