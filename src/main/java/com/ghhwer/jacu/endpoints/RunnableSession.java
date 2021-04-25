package com.ghhwer.jacu.endpoints;

import com.ghhwer.jacu.endpoints.ssh.Command;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.ByteArrayOutputStream;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicLong;

import static java.lang.Thread.sleep;

public class RunnableSession implements Runnable {

    // Thread Handling Stuff
    private final static int sleepMS = 500;
    private final static int quitAfterInactivity = 1800;
    private String status;
    private boolean keepAlive = true;
    private long sleepCyclesInactive = 0;
    private LinkedList<Command> commandQueue = new LinkedList<Command>();
    private LinkedList<Command> commandDoneQueue = new LinkedList<Command>();
    private final AtomicLong counter = new AtomicLong();

    // Connection Stuff
    private String host;
    private long port;
    private String username;
    private String password;
    Session session = null;
    ChannelExec channel = null;

    private void startSession() throws JSchException {
        session = new JSch().getSession(username, host, (int) port);
        session.setPassword(password);
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();
    }

    private String runCommandInSession(String command) throws JSchException, InterruptedException {
        channel = (ChannelExec) session.openChannel("exec");
        channel.setCommand(command);
        ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
        channel.setOutputStream(responseStream);
        channel.connect();

        while (channel.isConnected()) {
            sleep(100);
        }

        String responseString = new String(responseStream.toByteArray());
        channel.disconnect();
        return responseString;
    }

    public RunnableSession(String host, long port, String username, String password){
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        status = "starting";
    }

    public long pushNewCommand(String command){
        long commandIdx = counter.incrementAndGet();
        commandQueue.add(new Command(commandIdx, command));
        return commandIdx;
    }

    public void run(){
        try {
            startSession();
            while (keepAlive) {
                // Status is Waiting
                this.status = "waiting";
                while(commandQueue.size() != 0){
                    this.status = "running";
                    Command runningCommand = this.commandQueue.removeFirst();
                    String commandReturn = runCommandInSession(runningCommand.getCommand());
                    runningCommand.setResponse(commandReturn);
                    commandDoneQueue.add(runningCommand);
                }
                // Sleep a bit
                sleep(sleepMS);
                sleepCyclesInactive += 1;
                // Exits by timeout
                if (sleepCyclesInactive >= quitAfterInactivity) {
                    this.status = "timed out";
                    keepAlive = false;
                }
            }
        } catch (InterruptedException e) {
            this.status = "dead";
            e.printStackTrace();
        } catch (JSchException e) {
            // ssh error
            this.status = "dead";
            e.printStackTrace();
        }
        finally {
            // Disconnects things if needed
            if (session != null) {
                session.disconnect();
            }
            if (channel != null) {
                channel.disconnect();
            }
        }
        if(this.status == "running" || this.status == "waiting"){
            this.status = "closed";
        }
    }

    public LinkedList<Command> getExecutedCommands(){
        return commandDoneQueue;
    }

    public String getStatus() {
        return status;
    }

    public void close() throws InterruptedException {
        this.keepAlive = false;
        while(this.status == "running" || this.status == "waiting"){
            sleep(200);
        }
    }
}
