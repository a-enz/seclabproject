import BashHelpers.BashInterface;

import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {

        BashInterface bashInterface = new BashInterface();

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
            // TODO: validation
            bashInterface.generatePrivateKeyAndCertificatePKCS12(req.params("userId"));
            return "";
        });

        // TODO: revoke one/all certificates of a user, return new certificate revocation list
        delete("/certificates/one/:userid", (req, res) -> {
            return req.body();
            //return "TODO";
        });

        // TODO: revoke one/all certificates of a user, return new certificate revocation list
        delete("/certificates/all/:userid", (req, res) -> {
            return req.body();
            //return "TODO";
        });

        // TODO: get number of issued certificates
        get("/ca/issued", (req, res) -> {
            bashInterface.getIssuedSize();
            return req.body();
            //return "TODO";
        });

        // TODO: get number of revoked certificates
        get("/ca/revoked", (req, res) -> {
            return req.body();
            //return "TODO";
        });

        // TODO: get serial number
        get("/ca/serial_number", (req, res) -> {
            return req.body();
            //return "TODO";
        });
    }
}