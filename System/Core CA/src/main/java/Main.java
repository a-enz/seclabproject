import MySQLHelpers.MySQLInterface;
import MySQLHelpers.User;
import Requests.UpdateUserRequestBody;
import Responses.GetUserResponseBody;
import Requests.VerifyRequestBody;
import Responses.VerifyResponseBody;
import com.google.gson.Gson;

import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {

        // TODO: take db params from `args`
        MySQLInterface sqlInterface = new MySQLInterface(3306, "testdb", "root", "");

        // Define port where API will be listening
        port(8100);

        // ------ Filters ------
        // TODO
        after((req, res) -> {
            res.type("application/json");
        });

        // ------ CA calls ------

        // TODO: Issue certificate: generate key, generate certificate, return both in PKCS#12 format
        post("/certificates", (req, res) -> {
            return req.body();
            //return "TODO";
        });

        // TODO: Get all certificates of a user
//        get("/certificates/:userid", (req, res) -> {
//            return req.body();
//            //return "TODO";
//        });

        // TODO: revoke one/all certificates of a user, return new certificate revocation list
        delete("/certificates/:userid", (req, res) -> {
            return req.body();
            //return "TODO";
        });

        // TODO: get revocation list
//        get("/certificates/revocation_list", (req, res) -> {
//            return req.body();
//            //return "TODO";
//        });

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

        // Verify userId:passwordHash
        post("/users/verify/:userId", (req, res) -> {
            Gson jsonParser = new Gson();
            // TODO: validate request
            VerifyRequestBody requestBody = jsonParser.fromJson(req.body(), VerifyRequestBody.class);
            // Retrieve password hash from db
            String passwordHashDB = sqlInterface.getPasswordHash(req.params(":userId"));
            String passwordHashUser = requestBody.userPasswordHash;
            res.status(200);
            return jsonParser.toJson(new VerifyResponseBody(passwordHashDB.equals(passwordHashUser)));
        });

        // Get user data
        get("/users/:userId", (req, res) -> {
            Gson jsonParser = new Gson();
            // TODO: validate request
            // Get data from db
            User user = sqlInterface.getUser(req.params(":userId"));
            GetUserResponseBody userData = new GetUserResponseBody(user);
            res.status(200);
            return jsonParser.toJson(userData);
        });

        // Update user data
        post("/users/:userId", (req, res) -> {
            Gson jsonParser = new Gson();
            // TODO: validate request
            UpdateUserRequestBody requestBody = jsonParser.fromJson(req.body(), UpdateUserRequestBody.class);
            // Update data in db
            User updatedUser = new User(req.params(":userId"), requestBody.firstname, requestBody.lastname, requestBody.emailAddress);
            sqlInterface.updateUser(updatedUser);
            res.status(204);
            return "";
        });

    }
}