package com.example.project_toilet;
public class Calculate_Method {
    public boolean getThreshold_Check (float input_len , float last_len , float threshold){
        if( (input_len/last_len)*100 < threshold){
            return false;
        }
        return true;
    }
}
