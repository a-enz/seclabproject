package Responses;

public class VerifyResponseBody {

    public Boolean correctCredentials;

    public VerifyResponseBody(Boolean authResult) {
        this.correctCredentials = authResult;
    }
}
