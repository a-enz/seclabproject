package Responses;

public class RevokeCertificateResponse {
    String certificateRevocationList;

    public RevokeCertificateResponse(String crl) {
        certificateRevocationList = crl;
    }
}
