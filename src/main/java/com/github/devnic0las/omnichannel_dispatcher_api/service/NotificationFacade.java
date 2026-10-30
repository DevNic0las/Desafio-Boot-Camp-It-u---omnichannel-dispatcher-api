package com.github.devnic0las.omnichannel_dispatcher_api.service;

import com.github.devnic0las.omnichannel_dispatcher_api.factory.NotificationFactory;
import com.github.devnic0las.omnichannel_dispatcher_api.logging.AuditLogger;
import com.github.devnic0las.omnichannel_dispatcher_api.model.Notification;
import com.github.devnic0las.omnichannel_dispatcher_api.model.TypeNotification;
import com.github.devnic0las.omnichannel_dispatcher_api.strategy.NotificationStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Padrão <b>Facade</b>: uma única porta de entrada ("dispatch") esconde o
 * encadeamento Builder -> Factory -> Strategy -> Singleton(AuditLogger).
 */
@Service
@RequiredArgsConstructor
public class NotificationFacade {

  private final NotificationFactory notificationFactory;

  public void dispatch(String target, String text, TypeNotification channel) {

    AuditLogger.getInstance().log("Iniciando envio de notificação para " + target + " via " + channel);

    Notification notification = Notification.builder().to(target).withContent(text).build();

    NotificationStrategy strategy = notificationFactory.getStrategy(channel);

    strategy.send(notification);

    AuditLogger.getInstance().log("Despacho concluído com sucesso.");
  }

}
