import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {


        // Define port where API will be listening
        port(8100);

        // ------ CA calls ------

        // TODO: Issue certificate
        post("/certificates", (req, res) -> {
            return req.body();
            //return "TODO";
        });

        // TODO: Get all certificates of a user
        get("/certificates/:username", (req, res) -> {
            return req.body();
            //return "TODO";
        });

        // TODO: revoke one/all certificates of a user
        delete("/certificates/:username", (req, res) -> {
            return req.body();
            //return "TODO";
        });

        // TODO: get revocation list
        get("/certificates/revocation_list", (req, res) -> {
            return req.body();
            //return "TODO";
        });

        // TODO: get number of issued certificates
        get("/ca/issued", (req, res) -> {
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


        // ------ DB calls ------

        // TODO: Verify username:password
        post("/user/verify", (req, res) -> {
            return req.body();
            //return "TODO";
        });

        // TODO: Get user data
        get("/user/:username", (req, res) -> "Hello World");

        // TODO: Update user data
        post("/user/:username", (req, res) -> "Hello World");
    }
}