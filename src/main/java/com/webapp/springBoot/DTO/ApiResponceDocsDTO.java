package com.webapp.springBoot.DTO;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Класс возращаемых значений DTO")
public class ApiResponceDocsDTO {
    private String messages;
    public ApiResponceDocsDTO(String messages){
        this.messages  = messages;
    }

    public String getMessages() {
        return messages;
    }

    public void setMessages(String messages) {
        this.messages = messages;
    }
}
