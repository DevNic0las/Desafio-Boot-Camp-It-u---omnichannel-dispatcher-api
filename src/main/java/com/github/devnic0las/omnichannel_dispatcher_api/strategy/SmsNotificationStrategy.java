package com.github.devnic0las.omnichannel_dispatcher_api.strategy;

import com.github.devnic0las.omnichannel_dispatcher_api.model.Notification;

public class SmsNotificationStrategy implements NotificationStrategy{


@Override
    public void send(Notification notification) {

        System.out.println("Enviando notificação por SMS: " + notification.getContent());
}

}
