import BashHelpers.BashInterface;
import Requests.RevokeOneRequestBody;
import Responses.*;
import com.google.gson.Gson;

import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {

        BashInterface bashInterface = new BashInterface();

        Boolean ssl = false;
        Integer listenPort = 4800; // TODO change to 80/443

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
        // TODO: block ips

        after((req, res) -> {
            res.type("application/json");
        });

        // ------ CA calls ------

        // Issue certificate: generate key, generate certificate, return both in PKCS#12 format
        get("/certificates/new/:userId", (req, res) -> {
            return jsonParser.toJson(new IssueCertificateResponse(bashInterface.generatePrivateKeyAndCertificatePKCS12(req.params("userId"))));
        });

        // Revoke one certificate of a user, return new certificate revocation list
        delete("/certificates/:userId/one", (req, res) -> {
            RevokeOneRequestBody requestBody = jsonParser.fromJson(req.body(), RevokeOneRequestBody.class);
            if(requestBody == null) {
                res.status(400);
                return jsonParser.toJson(new ErrorResponseBody("Invalid request body"));
            }
            else if(notRevokable(requestBody.number)) {
                res.status(400);
                return jsonParser.toJson(new ErrorResponseBody("Certificate cannot be revoked"));
            }
            return jsonParser.toJson(new RevokeCertificateResponse(bashInterface.revokeCertificate(requestBody.number)));
        });

        // Revoke all certificates of a user, return new certificate revocation list
        delete("/certificates/:userId/all", (req, res) -> {
            return jsonParser.toJson(new RevokeCertificateResponse(bashInterface.revokeAllCertificates(req.params(":userId"))));
        });

        // Get number of issued certificates
        // TODO: check if correct what is meant with issued (how many created, which is serial - 1?)
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
    }

    // TODO
    private static Boolean notRevokable(String number) {
        switch(number) {
            case "01": case "02": case "03": return true;
            default: return false;
        }
    }
}