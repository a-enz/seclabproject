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
        int dbPort = 3306;
        String dbName = "testDB";
        String dbUser = "root";
        String dbPassword = "";
        
        if(args.length == 5) {
            listenPort = Integer.parseInt(args[0]);
            dbPort = Integer.parseInt(args[1]);
            dbName = args[2];
            dbUser = args[3];
            dbPassword = args[4];
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