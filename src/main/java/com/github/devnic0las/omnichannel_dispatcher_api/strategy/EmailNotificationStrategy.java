package com.github.devnic0las.omnichannel_dispatcher_api.strategy;

import com.github.devnic0las.omnichannel_dispatcher_api.model.Notification;
import org.springframework.stereotype.Component;

@Component
public class EmailNotificationStrategy implements NotificationStrategy{


  @Override
    public void send(Notification notification) {

        System.out.println("Enviando notificação por e-mail: " + notification.getContent());
    }
}
