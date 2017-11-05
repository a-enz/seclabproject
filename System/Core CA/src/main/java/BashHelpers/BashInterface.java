package BashHelpers;

// See https://docs.oracle.com/javase/7/docs/api/java/lang/ProcessBuilder.html
// and https://stackoverflow.com/questions/16825001/running-a-bash-command-from-within-a-java-program
// for how to run a bash process
public class BashInterface {

    // Bash command: openssl genrsa -out <name>.key 2048
    // TODO
    public void generatePrivateKey() {

    }

    // Bash command: openssl req -new -key <name>.key -out <name>.csr
    // TODO
    public void generateSigningRequest() {

    }

    // Bash command: sudo openssl ca -in <name>.csr -config /etc/ssl/openssl.cnf
    // TODO
    public void signCertificate() {

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

    // Bash command:
    // TODO
    public Integer getIssuedSize() {
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
