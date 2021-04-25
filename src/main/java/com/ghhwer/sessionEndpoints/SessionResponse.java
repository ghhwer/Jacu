package com.ghhwer.sessionEndpoints;

import com.ghhwer.sessionEndpoints.ssh.Command;

import java.util.ArrayList;

public class SessionResponse {
    private long idx;
    private String externalIdx;
    private String status;
    private ArrayList<Command> commands;

    public SessionResponse(long id, String externalIdx, String status, ArrayList<Command> commands) {
        this.idx = id;
        this.externalIdx = externalIdx;
        this.status = status;
        this.commands = commands;
    }


    public long getIdx() {
        return idx;
    }
    public String getStatus(){
        return this.status;
    }
    public String getExternalIdx() {
        return externalIdx;
    }
    public ArrayList<Command> getCommands() {
        return commands;
    }
}
