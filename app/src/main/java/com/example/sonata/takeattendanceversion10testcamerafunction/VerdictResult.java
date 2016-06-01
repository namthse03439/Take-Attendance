package com.example.sonata.takeattendanceversion10testcamerafunction;

import org.json.JSONObject;

/**
 * Created by Tung on 6/1/2016.
 */
class VerdictResult {
    private boolean verdict;
    private double confidence;

    VerdictResult(){
        verdict = true;
        confidence = 50;
    }

    VerdictResult(boolean result){
        verdict = result;
        confidence = 200;
    }

    VerdictResult(JSONObject result){
        try{
            verdict = result.getBoolean("is_same_person");
            confidence = result.getDouble("confidence");
        }
        catch(Exception e){}

    }

    public boolean getVerdict() {
        return (verdict);
    }
    public double getConfidence() {
        return (confidence);
    }

}