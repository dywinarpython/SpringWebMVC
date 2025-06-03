package com.webapp.springBoot.DTO.Message;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class RequestMessageDTO {
    @NotNull(message = "Получатель не может быть null")
    private String nicknameRecipient;

    @NotNull(message = "Сообщение не может быть null")
    private String message;

    @NotNull(message = "Не передан необходимый параметр")
    private String key;
}
