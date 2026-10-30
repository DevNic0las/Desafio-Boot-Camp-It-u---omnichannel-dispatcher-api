package com.github.devnic0las.omnichannel_dispatcher_api;

import com.github.devnic0las.omnichannel_dispatcher_api.factory.NotificationFactory;
import com.github.devnic0las.omnichannel_dispatcher_api.model.Notification;
import com.github.devnic0las.omnichannel_dispatcher_api.model.TypeNotification;
import com.github.devnic0las.omnichannel_dispatcher_api.service.NotificationFacade;
import com.github.devnic0las.omnichannel_dispatcher_api.strategy.NotificationStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationFacade (padrão Facade)")
class NotificationFacadeTest {

  @Mock
  private NotificationFactory factory;

  @Mock
  private NotificationStrategy strategy;

  @InjectMocks
  private NotificationFacade facade;

  @ParameterizedTest(name = "canal: {0}")
  @EnumSource(TypeNotification.class)
  @DisplayName("deve enviar pela estratégia devolvida pela fábrica")
  void shouldSendUsingStrategyFromFactory(TypeNotification channel) {
    // Arrange
    when(factory.getStrategy(channel)).thenReturn(strategy);

    // Act
    facade.dispatch("user@example.com", "Olá", channel);

    // Assert
    verify(factory).getStrategy(channel);
    verify(strategy).send(any(Notification.class));
  }

  @Test
  @DisplayName("deve repassar recipient e content intactos para a estratégia")
  void shouldForwardRecipientAndContentToStrategy() {
    // Arrange
    when(factory.getStrategy(TypeNotification.EMAIL)).thenReturn(strategy);
    ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);

    // Act
    facade.dispatch("user@example.com", "Olá", TypeNotification.EMAIL);

    // Assert
    verify(strategy).send(captor.capture());
    assertEquals("user@example.com", captor.getValue().getRecipient());
    assertEquals("Olá", captor.getValue().getContent());
  }

  @Test
  @DisplayName("deve lançar IllegalStateException quando o recipient é nulo")
  void shouldThrowWhenRecipientIsNull() {
    // Act + Assert — falha no builder, antes de qualquer estratégia
    assertThrows(
        IllegalStateException.class,
        () -> facade.dispatch(null, "Olá", TypeNotification.EMAIL)
    );

    // Assert
    verifyNoInteractions(strategy);
  }

  @ParameterizedTest(name = "content inválido: [{0}]")
  @NullSource
  @ValueSource(strings = {"", "   "})
  @DisplayName("deve lançar IllegalStateException quando o content é inválido")
  void shouldThrowWhenContentIsInvalid(String content) {
    // Act + Assert
    assertThrows(
        IllegalStateException.class,
        () -> facade.dispatch("user@example.com", content, TypeNotification.EMAIL)
    );

    // Assert
    verify(strategy, never()).send(any(Notification.class));
  }
}
