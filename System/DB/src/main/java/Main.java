import MySQLHelpers.MySQLInterface;
import MySQLHelpers.User;
import Requests.UpdateUserRequestBody;
import Responses.ErrorResponseBody;
import Responses.GetUserResponseBody;
import Requests.VerifyRequestBody;
import Responses.VerifyResponseBody;
import com.google.gson.Gson;

import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {

        Boolean ssl = false;
        int listenPort = 48100;
        int dbPort = 3306;
        String dbName = "testDB";
        String dbUser = "root";
        String dbPassword = "";
        
        if(args.length == 2) {
            ssl = Boolean.parseBoolean(args[0]);
            listenPort = Integer.parseInt(args[1]);
        }

        MySQLInterface sqlInterface = new MySQLInterface(dbPort, dbName, dbUser, dbPassword);

        Gson jsonParser = new Gson();

        // Define port where API will be listening
        port(listenPort);

        if(ssl)
            secure("./database.jks", "passwordThatShouldNotBeHardcoded", null, null);

        // ------ Filters ------
        // TODO: block ips
        after((req, res) -> {
            res.type("application/json");
        });

        // ------ Calls ------

        // Verify userId:passwordHash
        post("/users/verify/:userId", (req, res) -> {
            VerifyRequestBody requestBody = jsonParser.fromJson(req.body(), VerifyRequestBody.class);
            if(requestBody == null) {
                res.status(400);
                return jsonParser.toJson(new ErrorResponseBody("Invalid request body"));
            }
            // Retrieve password hash from db
            String passwordHashDB = sqlInterface.getPasswordHash(req.params(":userId"));
            String passwordHashUser = requestBody.userPasswordHash;
            res.status(200);
            return jsonParser.toJson(new VerifyResponseBody(passwordHashUser.equals(passwordHashDB)));
        });

        // Get user data
        get("/users/:userId", (req, res) -> {
            // Get data from db
            User user = sqlInterface.getUser(req.params(":userId"));
            GetUserResponseBody userData = new GetUserResponseBody(user);
            res.status(200);
            return jsonParser.toJson(userData);
        });

        // Update user data
        post("/users/:userId", (req, res) -> {
            UpdateUserRequestBody requestBody = jsonParser.fromJson(req.body(), UpdateUserRequestBody.class);
            if(requestBody == null) {
                res.status(400);
                return jsonParser.toJson(new ErrorResponseBody("Invalid request body"));
            }
            // Update data in db
            User updatedUser = new User(req.params(":userId"), requestBody.firstname, requestBody.lastname, requestBody.emailAddress);
            sqlInterface.updateUser(updatedUser);
            res.status(204);
            return "";
        });
    }
}