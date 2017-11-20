package Requests;

import Responses.RevokeCertificateResponse;

public class RevokeOneRequestBody {
    public String number;

    public RevokeOneRequestBody(String number) {
        this.number = number;
    }
}
