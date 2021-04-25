package com.ghhwer.endpoints;

import com.ghhwer.endpoints.ssh.Command;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;

public class SessionHolder extends Thread {
    private final long idx;
    private final String externalIdx;
    private RunnableSession session;

    public SessionHolder(SessionNew connection, long id, String externalIdx, ExecutorService threadPool) {
        this.idx = id;
        this.externalIdx = externalIdx;
        this.session = new RunnableSession(
                connection.getHost(), connection.getPort(), connection.getUsername(), connection.getPassword()
        );
        threadPool.submit(this.session);
    }

    public long getIdx() {
        return idx;
    }

    public String getStatus(){
        return this.session.getStatus();
    }

    public String getExternalIdx() {
        return externalIdx;
    }

    public ArrayList<Command> getCommands(){
        return new ArrayList<Command>(this.session.getExecutedCommands());
    }

    public long postCommand(String command) {
        if (this.session.getStatus() == "closed" || this.session.getStatus() == "dead")
            return -1;
        return this.session.pushNewCommand(command);
    }

    public void closeSession() {
        try {
            this.session.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}