package com.github.devnic0las.omnichannel_dispatcher_api.controller;


import com.github.devnic0las.omnichannel_dispatcher_api.dto.NotificationRequest;
import com.github.devnic0las.omnichannel_dispatcher_api.service.NotificationFacade;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {

  private final NotificationFacade notificationFacade;

  @PostMapping
  public ResponseEntity<String> sendMessage(@Valid @RequestBody NotificationRequest notificationRequest){
    notificationFacade.dispatch(
        notificationRequest.recipient(),
        notificationRequest.content(),
        notificationRequest.type()
    );
    return ResponseEntity.ok("Notificação enviada com sucesso!");
  }

}
