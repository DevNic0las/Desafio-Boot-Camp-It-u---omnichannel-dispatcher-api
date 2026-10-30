package com.github.devnic0las.omnichannel_dispatcher_api;

import com.github.devnic0las.omnichannel_dispatcher_api.model.TypeNotification;
import com.github.devnic0las.omnichannel_dispatcher_api.service.NotificationFacade;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OmnichannelDispatcherApiApplication {

	public static void main(String[] args) {
		var ctx = SpringApplication.run(OmnichannelDispatcherApiApplication.class, args);

		ctx.getBean(NotificationFacade.class)
						.dispatch("user@example.com", "Hello, World!", TypeNotification.EMAIL);

	}

}
