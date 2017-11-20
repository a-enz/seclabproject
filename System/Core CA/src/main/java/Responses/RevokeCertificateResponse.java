package Responses;

public class RevokeCertificateResponse {
    byte[] certificateRevocationList;

    public RevokeCertificateResponse(byte[] crl) {
        certificateRevocationList = crl;
    }
}
