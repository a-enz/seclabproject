package BashHelpers;

import Responses.IssueCertificateResponse;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private final String sslDirectory = "./ssl";
    private final String caDirectory = sslDirectory + "/CA";
    private final String crlDirectory = caDirectory + "/crl";
    private final String newcertsDirectory = caDirectory + "/newcerts";
    private final String tmpDirectory = "./tmp";
    private final String indexFile = caDirectory + "/index.txt";
    private final String serialFile = caDirectory + "/serial";
    private final String oslConfigFile = sslDirectory + "/openssl.cnf";

    // TODO: set up needed directories and files
    public void setUpCa() throws IOException {
        executeCommand("sh", "setup_script.sh");
    }

    // TODO: handle output
    // Note: every single argument must be passed to ProcessBuilder separately!
    private String executeCommand(String ... command) throws java.io.IOException {
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectOutput(ProcessBuilder.Redirect.appendTo(new File("stdout.txt")));
        pb.redirectError(ProcessBuilder.Redirect.appendTo(new File("stderr.txt")));
        return IOUtils.toString(pb.start().getInputStream());
    }

    private String subj(String userId) {
        return "/C=CH/ST=Zurich/L=Zurich/O=iMovies/OU=IT/CN=" + userId;
    }

    // TODO: currently using -batch option, set unique_subject to false to allow multiple certs for same entity
    // TODO: replace openssl.cnf policy and generate ca certificate with java to have it match encoding
    //executeCommand("openssl", "req", "-new", "-x509", "-extensions", "v3_ca", "-keyout", "cakey.pem", "-out", "cacert.pem", "-days", "3650", "-subj", subj, "-passin", "pass:test");
    public byte[] generatePrivateKeyAndCertificatePKCS12(String userId) throws java.io.IOException, InterruptedException {
        String keyFileName = tmpDirectory + "/" + userId + ".key";
        String csrFileName = tmpDirectory + "/" + userId + ".csr";
        String pkcs12FileName = tmpDirectory + "/" + userId + ".p12";
        // Generate new private key
        executeCommand("openssl", "genrsa", "-out", keyFileName, "2048", "-config", oslConfigFile);
        // TODO: improve waiting
        TimeUnit.SECONDS.sleep(2);
        // Create certificate signing request
        executeCommand("openssl", "req", "-new", "-key", keyFileName, "-out", csrFileName, "-subj", subj(userId), "-config", oslConfigFile);
        // TODO: improve waiting
        TimeUnit.SECONDS.sleep(2);
        // Sign certificate
        executeCommand("openssl", "ca", "-batch", "-in", csrFileName, "-config", oslConfigFile, "-subj", subj(userId), "-passin", "pass:test");
        // TODO: Convert private key and certificate in PKCS#12
        // Bash command: openssl pkcs12 -export -in <name>.pem -inkey <name>.key -out <name>.p12 -name "<certname>"
        executeCommand("openssl", "pkcs12", "-export", "-in", newcertsDirectory + "/" + getOldSerial() + ".pem", "-inkey", keyFileName, "-out", pkcs12FileName, "-name", "someName", "-passout", "pass:12345");
        TimeUnit.SECONDS.sleep(1);
        byte[] pkcs12 = FileUtils.readFileToByteArray(new File(pkcs12FileName));
        //byte[] base64Pkcs12 = Base64.getEncoder().encode(bytes);
        // Delete private key
        executeCommand("rm", keyFileName);
        // Delete signing request
        executeCommand("rm", csrFileName);
        // Delete PKCS#12 files
        executeCommand("rm", pkcs12FileName);
        // TODO: only for testing purposes
        FileUtils.writeByteArrayToFile(new File(tmpDirectory + "/decoded.p12"), Base64.getDecoder().decode(pkcs12));
        //assert(Base64.getDecoder().decode(base64Pkcs12).equals(base64Pkcs12));
        return pkcs12;
    }

    // TODO
    public void revokeAllCertificates(String userId) throws IOException {
        // TODO: search all certificate names for given user and add them to the list
        List<String> certificates = new ArrayList<String>();
        for(String cert : certificates)
            executeCommand("openssl", "ca", "-revoke", cert + ".crt", "-config", oslConfigFile);
    }

    // Bash command: sudo openssl ca -gencrl -out /etc/ssl/CA/crl/crl.pem
    // TODO
    public String createRevocationList() throws IOException {
        executeCommand("openssl", "ca", "-gencrl", "-out", crlDirectory + "/crl.pem");
        // TODO: read crl to string
        String crl = "TODO";
        return crl;
    }

    // TODO: error handling
    public Integer getIssuedSize() throws IOException {
        ProcessBuilder pb = new ProcessBuilder("wc", "-l", indexFile);
        String output = IOUtils.toString(pb.start().getInputStream());
        return parseWcOutput(output);
    }

    // TODO: pipe to wc -l
    public Integer getRevokedSize() throws IOException {
        ProcessBuilder pb = new ProcessBuilder("grep", "V", indexFile); //, "|", "wc", "-l");
        String output = IOUtils.toString(pb.start().getInputStream());
        if(output.isEmpty())
            return 0;
        else
            return parseWcOutput(output);
    }

    private Integer parseWcOutput(String wcOut) {
        Pattern p = Pattern.compile(".*([0-9]+) " + indexFile + "\n");
        Matcher m = p.matcher(wcOut);
        return Integer.parseInt(m.group(1));
    }

    // TODO: parse output string
    public String getCurrentSerial() throws IOException {
        ProcessBuilder pb = new ProcessBuilder("cat", serialFile);
        String output = IOUtils.toString(pb.start().getInputStream());
        return output.replace("\n", "");
    }

    private String getOldSerial() throws IOException {
        ProcessBuilder pb = new ProcessBuilder("cat", serialFile + ".old");
        String output = IOUtils.toString(pb.start().getInputStream());
        return output.replace("\n", "");
    }
}
