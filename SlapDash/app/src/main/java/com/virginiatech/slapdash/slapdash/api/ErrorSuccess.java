package com.virginiatech.slapdash.slapdash.api;

import lombok.Data;

/**
 * Created by nima on 11/28/16.
 */
@Data
public class ErrorSuccess {
    private String error;
    private String success;

    public ErrorSuccess(String success, String error){
        this.success = success;
        this.error = error;
    }
}
