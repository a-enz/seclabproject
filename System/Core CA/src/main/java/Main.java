import BashHelpers.BashInterface;
import Responses.*;
import com.google.gson.Gson;

import java.io.IOException;

import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {

        BashInterface bashInterface = new BashInterface();

        try {
            bashInterface.setUpCa();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Define port where API will be listening
        port(8100);

        // ------ Filters ------
        // TODO
        after((req, res) -> {
            res.type("application/json");
        });

        // ------ CA calls ------

        // Issue certificate: generate key, generate certificate, return both in PKCS#12 format
        get("/certificates/new/:userId", (req, res) -> {
            Gson jsonParser = new Gson();
            // TODO: validation
            return jsonParser.toJson(new IssueCertificateResponse(bashInterface.generatePrivateKeyAndCertificatePKCS12(req.params("userId"))));
        });

        // TODO: revoke one certificate of a user, return new certificate revocation list
        delete("/certificates/one/:userid", (req, res) -> {
            return new RevokeCertificateResponse("TODO");
        });

        // TODO: revoke all certificates of a user, return new certificate revocation list
        delete("/certificates/all/:userid", (req, res) -> {
            return new RevokeCertificateResponse("TODO");
        });

        // Get number of issued certificates
        // TODO: check if correct what is meant with issued (how many created, which is serial - 1?)
        get("/ca/issued", (req, res) -> {
            Gson jsonParser = new Gson();
            return jsonParser.toJson(new GetIssuedResponse(bashInterface.getIssuedSize()));
        });

        // Get number of revoked certificates
        get("/ca/revoked", (req, res) -> {
            Gson jsonParser = new Gson();
            return jsonParser.toJson(new GetRevokedResponse(bashInterface.getRevokedSize()));
        });

        // Get current serial number
        get("/ca/serial_number", (req, res) -> {
            Gson jsonParser = new Gson();
            return jsonParser.toJson(new GetSerialResponse(bashInterface.getCurrentSerial()));
        });
    }
}