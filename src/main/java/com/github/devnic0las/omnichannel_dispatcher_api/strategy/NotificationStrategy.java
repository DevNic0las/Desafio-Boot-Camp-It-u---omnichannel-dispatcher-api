package com.github.devnic0las.omnichannel_dispatcher_api.strategy;

import com.github.devnic0las.omnichannel_dispatcher_api.model.Notification;

public interface NotificationStrategy {

  void send(Notification notification);


}
