package MySQLHelpers;

import java.sql.*;

public class MySQLInterface {

    private Connection dbConn = null;
    private Statement stmt = null;
    private ResultSet resSet = null;

    private final String get_user_query(String userId) {
        return "SELECT lastname, firstname, email FROM users WHERE uid='" + userId + "'";
    }

    private final String get_user_pwd_hash_query(String userId) {
        return "SELECT pwd FROM users WHERE uid='" + userId + "'";
    }

    private final String update_user_query(User user) {
        String update = "UPDATE users SET ";
        if(user.lastName != null)
            update += "lastname='" + user.lastName + "'";
        if(user.firstName != null)
            update += ", firstname='" + user.firstName + "'";
        if(user.email != null)
            update += ", email='" + user.email + "'";
        update += " WHERE uid='" + user.userId + "'";
        return update;
    }


    public MySQLInterface(Integer port, String dbName, String user, String password) {
        try {
            String url = "jdbc:mysql://localhost:" + port.toString() + "/" + dbName;
            dbConn = DriverManager.getConnection(url, user, password);
            stmt = dbConn.createStatement();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    // Returns the data of the user with `userId` or null if no such user exists
    public User getUser(String userId) {
        try {
            ResultSet resSet = stmt.executeQuery(get_user_query(userId));
            if (resSet.next()) {
                return new User(userId, resSet.getString(1), resSet.getString(2), resSet.getString(3));
            } else
                return null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Updates fields according to `user`, null fields are not updated and the function should be called only
    // if something has to be updated
    public void updateUser(User user) throws SQLException {
        stmt.executeUpdate(update_user_query(user));
    }

    public String getPasswordHash(String userId) throws SQLException {
        ResultSet resSet = stmt.executeQuery(get_user_pwd_hash_query(userId));
        if(resSet.next())
            return resSet.getString(1);
        else
            return null;
    }
}
