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

        int listenPort = 8101;
        int dbPort = 3302;
        String dbName = "testDB";
        String dbUser = "root";
        String dbPassword = "";

        // TODO: message
        if(args.length != 6) {
            System.out.println("Invalid number of arguments. Expected: <>");
            return;
        } else {
            listenPort = Integer.parseInt(args[1]);
            dbPort = Integer.parseInt(args[2]);
            dbName = args[3];
            dbUser = args[4];
            dbPassword = args[5];
        }

        MySQLInterface sqlInterface = new MySQLInterface(dbPort, dbName, dbUser, dbPassword);

        // Define port where API will be listening
        port(listenPort);

        // ------ Filters ------
        // TODO
        after((req, res) -> {
            res.type("application/json");
        });

        // ------ Calls ------

        // Verify userId:passwordHash
        post("/users/verify/:userId", (req, res) -> {
            Gson jsonParser = new Gson();
            // TODO: validate request
            VerifyRequestBody requestBody = jsonParser.fromJson(req.body(), VerifyRequestBody.class);
            // Retrieve password hash from db
            String passwordHashDB = sqlInterface.getPasswordHash(req.params(":userId"));
            String passwordHashUser = requestBody.userPasswordHash;
            res.status(200);
            return jsonParser.toJson(new VerifyResponseBody(passwordHashUser.equals(passwordHashDB)));
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