package com.ghhwer.jacu.endpoints.ssh;

public class Command {
    private String command;
    private String response;
    private long idx;

    public Command(long idx, String command){
        this.command = command;
        this.idx = idx;
    }
    public String getCommand(){
        return this.command;
    }

    public long getIdx(){
        return idx;
    }

    public void setResponse(String response){
        this.response = response;
    }
    public String getResponse(){
        return this.response;
    }
}
