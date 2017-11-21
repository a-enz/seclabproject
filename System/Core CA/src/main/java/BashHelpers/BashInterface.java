package BashHelpers;

import Responses.IssueCertificateResponse;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
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
    private final String ORGANIZATORIAL_UNIT = "Administration";

    private final static String caPassword = "passwordThatShouldNotBeHardcoded";

    // File paths
    private final String baseDirectory = ".";
    private final String sslDirectory = baseDirectory + "/ssl";
    private final String caDirectory = sslDirectory + "/CA";
    private final String crlDirectory = caDirectory + "/crl";
    private final String newcertsDirectory = caDirectory + "/newcerts";
    private final String keysDirectory = caDirectory + "/keys";
    private final String tmpDirectory = baseDirectory + "/tmp";
    private final String logsDirectory = baseDirectory + "/logs";
    private final String indexFile = caDirectory + "/index.txt";
    private final String serialFile = caDirectory + "/serial";
    private final String oslConfigFile = sslDirectory + "/openssl.cnf";
    private final String crlFile = crlDirectory + "/crl.pem";
    private final String symmKeyFileName = tmpDirectory + "/symm.key";
    private final String backupPubKeyFileName = baseDirectory + "/backup.pub";

    public BashInterface() {
        new File(crlDirectory).mkdirs();
        new File(keysDirectory).mkdirs();
        new File(tmpDirectory).mkdirs();
    }

    // Note: every single argument must be passed to ProcessBuilder separately!
    private String executeCommand(String ... command) throws java.io.IOException {
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectError(ProcessBuilder.Redirect.appendTo(new File(logsDirectory + "/stderr.log")));
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
    public byte[] generatePrivateKeyAndCertificatePKCS12(String userId, String userPwd) throws java.io.IOException, InterruptedException {
        String keyFileName = tmpDirectory + "/" + userId + ".key";
        String csrFileName = tmpDirectory + "/" + userId + ".csr";
        String pkcs12FileName = tmpDirectory + "/" + userId + ".p12";


        // Generate new private key for user
        executeCommand("openssl", "genrsa", "-out", keyFileName, "2048", "-config", oslConfigFile);

        TimeUnit.MILLISECONDS.sleep(200);

        // Create certificate signing request
        executeCommand("openssl", "req", "-new", "-key", keyFileName, "-out", csrFileName, "-subj", subj(userId), "-config", oslConfigFile);

        TimeUnit.MILLISECONDS.sleep(200);

        // Sign certificate
        executeCommand("openssl", "ca", "-batch", "-in", csrFileName, "-config", oslConfigFile, "-subj", subj(userId), "-passin", "pass:" + caPassword);

        // Convert private key and certificate in PKCS#12
        executeCommand("openssl", "pkcs12", "-export", "-in", newcertsDirectory + "/" + getOldSerial() + ".pem", "-inkey", keyFileName, "-out", pkcs12FileName, "-name", "iMovies", "-passout", "pass:" + userPwd);

        TimeUnit.MILLISECONDS.sleep(200);

        // Read PKCS#12 file
        byte[] pkcs12 = FileUtils.readFileToByteArray(new File(pkcs12FileName));

        // Generate 256 symmteric key
        executeCommand("openssl", "rand", "32", "-out", symmKeyFileName);

        TimeUnit.MILLISECONDS.sleep(200);

        // Encrypt private key and store in /keys
        String newKeyName = keysDirectory + "/" + getIssuedSize() + "-" + userId;
        executeCommand("openssl", "enc", "-aes-256-cbc", "-salt", "-in", keyFileName, "-out", newKeyName + ".key.enc", "-pass", "file:" + symmKeyFileName);
        // openssl enc -aes-256-cbc -salt -in largefile.pdf -out largefile.pdf.enc -pass file:./bin.key
        TimeUnit.MILLISECONDS.sleep(200);

        // Encrypt symmteric key and store it in /keys
        // openssl rsautl -encrypt -inkey ../Certificates/backup.pub -pubin -in symm.key -out symm.enc
        executeCommand("openssl", "rsautl", "-encrypt", "-inkey", backupPubKeyFileName, "-pubin", "-in", symmKeyFileName, "-out", newKeyName + ".symm.enc");

        // Delete private key, symmetric key, signing request and PKCS#12 files
        executeCommand("rm", keyFileName);
        executeCommand("rm", symmKeyFileName);
        executeCommand("rm", csrFileName);
        executeCommand("rm", pkcs12FileName);

        return pkcs12;
    }

    public byte[] revokeAllCertificates(String userId) throws IOException {
        String[] grepStrings = executeCommand("grep", "CN=" + userId, indexFile).split("\n");
        for(String grepString : grepStrings) {
            Pattern p = Pattern.compile("(R|V)\t.*\t.*\t([0-9A-F]+)\tunknown\t/C=CH/ST=Zurich/O=iMovies/OU=User/CN=" + userId);
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
        return parseWcOutput(output); // index.txt has an empty line at the end
    }

    public Integer getRevokedSize() throws IOException {
        String output = executeCommand("bash", "-c", "grep R " + indexFile + " | wc -l");
        if(output.isEmpty())
            return 0;
        else
            return parseWcOutput(output) - 1;
    }

    private Integer parseWcOutput(String wcOut) {
        Pattern p = Pattern.compile(" *([0-9]+) ?.*\n");
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
