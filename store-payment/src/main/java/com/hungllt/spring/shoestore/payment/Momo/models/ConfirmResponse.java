package com.hungllt.spring.shoestore.payment.Momo.models;


import com.hungllt.spring.shoestore.payment.Momo.enums.ConfirmRequestType;

public class ConfirmResponse extends Response {
    private Long amount;
    private Long transId;
    private String requestId;
    private ConfirmRequestType requestType;
}
