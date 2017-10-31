import Requests.UpdateUserRequestBody;
import Responses.GetUserResponseBody;
import Requests.VerifyRequestBody;
import Responses.VerifyResponseBody;
import com.google.gson.Gson;

import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {


        // Define port where API will be listening
        port(8100);

        // ------ Filters ------
        // TODO
        after((req, res) -> {
            res.type("application/json");
        });

        // ------ CA calls ------

        // TODO: Issue certificate
        post("/certificates", (req, res) -> {
            return req.body();
            //return "TODO";
        });

        // TODO: Get all certificates of a user
        get("/certificates/:userid", (req, res) -> {
            return req.body();
            //return "TODO";
        });

        // TODO: revoke one/all certificates of a user
        delete("/certificates/:userid", (req, res) -> {
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
        post("/users/verify/:userId", (req, res) -> {
            Gson jsonParser = new Gson();
            // TODO: validate request
            VerifyRequestBody requestBody = jsonParser.fromJson(req.body(), VerifyRequestBody.class);
            // TODO: retrieve password hash from db
            String passwordHashDB = "dummyHash";
            // TODO: hash password
            String passwordHashUser = "dummyHash";
            res.status(200);
            return jsonParser.toJson(new VerifyResponseBody(passwordHashDB.equals(passwordHashUser)));
        });

        // TODO: Get user data
        get("/users/:userId", (req, res) -> {
            Gson jsonParser = new Gson();
            // TODO: validate request
            // TODO: get data from db
            GetUserResponseBody userData = new GetUserResponseBody("dummy", "user", "data");
            res.status(200);
            return jsonParser.toJson(userData);
        });

        // TODO: Update user data
        post("/users/:userId", (req, res) -> {
            Gson jsonParser = new Gson();
            // TODO: validate request
            UpdateUserRequestBody requestBody = jsonParser.fromJson(req.body(), UpdateUserRequestBody.class);
            // TODO: update data in db
            res.status(204);
            return "";
        });

    }
}