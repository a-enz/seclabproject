package Responses;

public class IssueCertificateResponse {

    public byte[] pkcs12;

    public IssueCertificateResponse(byte[] pkcs12) {
        this.pkcs12 = pkcs12;
    }
}
