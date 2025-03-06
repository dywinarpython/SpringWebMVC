package com.webapp.springBoot.DTO;

public class ApiResponceDocsDOTO {
    private String messages;
    public ApiResponceDocsDOTO(String messages){
        this.messages  = messages;
    }

    public String getMessages() {
        return messages;
    }

    public void setMessages(String messages) {
        this.messages = messages;
    }
}
