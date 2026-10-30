package com.github.devnic0las.omnichannel_dispatcher_api.factory;

import com.github.devnic0las.omnichannel_dispatcher_api.model.TypeNotification;
import com.github.devnic0las.omnichannel_dispatcher_api.strategy.EmailNotificationStrategy;
import com.github.devnic0las.omnichannel_dispatcher_api.strategy.NotificationStrategy;
import com.github.devnic0las.omnichannel_dispatcher_api.strategy.SmsNotificationStrategy;
import org.springframework.stereotype.Component;

/**
 * Padrão <b>Factory</b>: centraliza a escolha da {@link NotificationStrategy}
 * a partir do tipo de canal. Quem chama não conhece as implementações concretas.
 */
@Component
public class NotificationFactory {

  public NotificationStrategy getStrategy(TypeNotification type) {
    if (type == null) {
      throw new IllegalArgumentException("O tipo de notificação é obrigatório.");
    }
    // switch sobre enum é exaustivo: o compilador exige tratar todos os valores.
    return switch (type) {
      case EMAIL -> new EmailNotificationStrategy();
      case SMS -> new SmsNotificationStrategy();
    };
  }
}
