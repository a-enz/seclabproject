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
    private final String ORGANIZATORIAL_UNIT = "Users";

    private final static String caPassword = "passwordThatShouldNotBeHardcoded";

    // File paths
    private final String sslDirectory = "./ssl";
    private final String caDirectory = sslDirectory + "/CA";
    private final String crlDirectory = caDirectory + "/crl";
    private final String newcertsDirectory = caDirectory + "/newcerts";
    private final String tmpDirectory = "./tmp";
    private final String indexFile = caDirectory + "/index.txt";
    private final String serialFile = caDirectory + "/serial";
    private final String oslConfigFile = sslDirectory + "/openssl.cnf";
    private final String crlFile = crlDirectory + "/crl.pem";

    public void setUpCa(Boolean reset) throws IOException, InterruptedException {
        executeCommand("sh", "setup.sh");
    }

    // Note: every single argument must be passed to ProcessBuilder separately!
    private String executeCommand(String ... command) throws java.io.IOException {
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectError(ProcessBuilder.Redirect.appendTo(new File("stderr.log")));
        return IOUtils.toString(pb.start().getInputStream());
    }

    private String subj(String userId) {
        return "/C="+ COUNTRY_NAME +
                "/ST=" + STATE_NAME +
                "/L=" + LOCALITY_NAME +
                "/O=" + ORGANIZATION_NAME +
                "/OU=" + ORGANIZATORIAL_UNIT +
                "/CN=" + userId;
    }

    // Currently using -batch option and set unique_subject to false to allow multiple certs for same entity
    // string_mask also set to utf8only
    public byte[] generatePrivateKeyAndCertificatePKCS12(String userId) throws java.io.IOException, InterruptedException {
        String keyFileName = tmpDirectory + "/" + userId + ".key";
        String csrFileName = tmpDirectory + "/" + userId + ".csr";
        String pkcs12FileName = tmpDirectory + "/" + userId + ".p12";

        // Generate new private key for user
        executeCommand("openssl", "genrsa", "-out", keyFileName, "2048", "-config", oslConfigFile);

        // TODO: improve waiting
        TimeUnit.SECONDS.sleep(1);

        // Create certificate signing request
        executeCommand("openssl", "req", "-new", "-key", keyFileName, "-out", csrFileName, "-subj", subj(userId), "-config", oslConfigFile);

        // TODO: improve waiting
        TimeUnit.SECONDS.sleep(1);

        // Sign certificate
        executeCommand("openssl", "ca", "-batch", "-in", csrFileName, "-config", oslConfigFile, "-subj", subj(userId), "-passin", "pass:" + caPassword);

        // Convert private key and certificate in PKCS#12
        // TODO: remove password? return a random password? request it?
        executeCommand("openssl", "pkcs12", "-export", "-in", newcertsDirectory + "/" + getOldSerial() + ".pem", "-inkey", keyFileName, "-out", pkcs12FileName, "-name", "iMovies", "-passout", "pass:12345");
        TimeUnit.SECONDS.sleep(1);

        // Read PKCS#12 file
        byte[] pkcs12 = FileUtils.readFileToByteArray(new File(pkcs12FileName));

        // Delete private key, signing request and PKCS#12 files
        executeCommand("rm", keyFileName);
        executeCommand("rm", csrFileName);
        executeCommand("rm", pkcs12FileName);

        return pkcs12;
    }

    public byte[] revokeAllCertificates(String userId) throws IOException {
        String[] grepStrings = executeCommand("grep", "CN=" + userId).split("\n");
        for(String grepString : grepStrings) {
            Pattern p = Pattern.compile("(R|V)\t.*\t.*\t([0-9A-F]+)\tunknown\t/C=CH/ST=Zurich/O=iMovies/OU=Users/CN=" + userId);
            Matcher m = p.matcher(grepString);
            if(m.matches())
                executeCommand("openssl", "ca", "-revoke", newcertsDirectory + "/" + m.group(2) + ".pem", "-config", oslConfigFile, "-passin", "pass:" + caPassword);
        }
        return createRevocationList();
    }

    public byte[] revokeCertificate(String number) throws IOException {
        executeCommand("openssl", "ca", "-revoke", newcertsDirectory + "/" + number + ".pem", "-config", oslConfigFile, "-passin", "pass:" + caPassword);
        return createRevocationList();
    }

    public byte[] createRevocationList() throws IOException {
        executeCommand("openssl", "ca", "-gencrl", "-out", crlDirectory + "/crl.pem", "-config", oslConfigFile);
        byte[] crl = FileUtils.readFileToByteArray(new File(crlFile));
        return crl;
    }

    public Integer getIssuedSize() throws IOException {
        String output = executeCommand("wc", "-l", indexFile);
        return parseWcOutput(output);
    }

    public Integer getRevokedSize() throws IOException {
        String output = executeCommand("bash", "-c", "grep R " + indexFile + " | wc -l");
        if(output.isEmpty())
            return 0;
        else
            return parseWcOutput(output);
    }

    private Integer parseWcOutput(String wcOut) {
        Pattern p = Pattern.compile(".*([0-9A-F]+) ?.*\n");
        Matcher m = p.matcher(wcOut);
        if(m.matches())
            return Integer.parseInt(m.group(1));
        else
            return 0;
    }

    public String getCurrentSerial() throws IOException {
        return executeCommand("cat", serialFile).replace("\n", "");
    }

    private String getOldSerial() throws IOException {
        String ret = executeCommand("cat", serialFile + ".old").replace("\n", "");
        if(ret.equals(""))
            return "01";
        return ret;
    }
}
