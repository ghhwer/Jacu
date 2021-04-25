package com.ghhwer.jacu.utils;

public class ApplicationResponse {
    Boolean status;
    Object details;

    public ApplicationResponse(Boolean status, Object details){
        this.status = status;
        this.details = details;
    }

    public Boolean getStatus() {
        return status;
    }

    public Object getDetails() {
        return details;
    }
}
