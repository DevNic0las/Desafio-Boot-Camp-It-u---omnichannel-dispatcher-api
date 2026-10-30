package com.github.devnic0las.omnichannel_dispatcher_api.dto;

import com.github.devnic0las.omnichannel_dispatcher_api.model.TypeNotification;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NotificationRequest(

    @NotBlank(message = "O campo recipient é obrigatório.")
    String recipient,
    @NotBlank(message = "O campo content é obrigatório.")
    String content,
    @NotNull(message = "O campo type é obrigatório.")
    TypeNotification type

) {
}
