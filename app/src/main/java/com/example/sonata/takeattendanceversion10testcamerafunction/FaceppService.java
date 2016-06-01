package com.example.sonata.takeattendanceversion10testcamerafunction;

import com.facepp.http.HttpRequests;

/**
 * Created by Tung on 6/1/2016.
 */
public class FaceppService {
    final static String apiKey = "8253a8dfd06d885e754ef8c596d4e809";
    final static String apiSecret = "HlTQpKjISJ0Fxp1kkd4COSf12-_ErMrH";
    final static HttpRequests httpRequests = new HttpRequests(apiKey, apiSecret);
    final static double verificationThreshold = 60;

}
