package com.ghhwer.jacu.endpoints;

import javax.validation.constraints.NotNull;

public class SessionNew {
    @NotNull(message = "A host must be provided")
    String host;
    @NotNull(message = "A port must be provided")
    Long port;
    @NotNull(message = "An username must be provided")
    String username;
    @NotNull(message = "A password must be provided")
    String password;

    public void setHost(String host) {
        this.host = host;
    }
    public String getHost() {
        return host;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public String getPassword() {
        return password;
    }

    public void setPort(Long port) {
        this.port = port;
    }
    public Long getPort() {
        return port;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public String getUsername() {
        return username;
    }

}
