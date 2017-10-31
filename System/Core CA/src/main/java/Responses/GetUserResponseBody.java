package Responses;

public class GetUserResponseBody {

    public String lastname;

    public String firstname;

    public String emailAddress;

    public GetUserResponseBody(String lastname, String firstname, String emailAddress) {
        this.lastname = lastname;
        this.firstname = firstname;
        this.emailAddress = emailAddress;
    }
}
