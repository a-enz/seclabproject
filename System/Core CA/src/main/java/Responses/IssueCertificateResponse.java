package Responses;

public class IssueCertificateResponse {

    public String privateKey;

    public String certificate;

    public IssueCertificateResponse(String privateKey, String certificate) {
        this.privateKey = privateKey;
        this.certificate = certificate;
    }
}
