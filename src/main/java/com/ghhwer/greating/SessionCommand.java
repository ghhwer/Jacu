package com.ghhwer.greating;

import javax.validation.constraints.NotNull;

public class SessionCommand {
    @NotNull(message = "Command was not provided in the body")
    private String command;


    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
