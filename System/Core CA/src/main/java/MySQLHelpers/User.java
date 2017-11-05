package MySQLHelpers;

public class User {
    public String userId;
    public String firstName;
    public String lastName;
    public String email;
    //public String passwordHash;

    public User(String userId, String firstName, String lastName, String email) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
}
