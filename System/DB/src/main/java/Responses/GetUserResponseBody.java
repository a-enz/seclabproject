package Responses;

import MySQLHelpers.User;

public class GetUserResponseBody {

    public String lastname;

    public String firstname;

    public String emailAddress;

    public GetUserResponseBody(User user) {
        this.lastname = user.lastName;
        this.firstname = user.firstName;
        this.emailAddress = user.email;
    }
}
