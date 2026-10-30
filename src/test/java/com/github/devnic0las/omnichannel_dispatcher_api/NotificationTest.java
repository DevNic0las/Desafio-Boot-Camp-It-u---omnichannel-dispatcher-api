package com.github.devnic0las.omnichannel_dispatcher_api;

import com.github.devnic0las.omnichannel_dispatcher_api.model.Notification;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Notification (builder)")
class NotificationTest {

  @Test
  @DisplayName("deve criar notificação com recipient e content válidos")
  void shouldBuildNotificationWithValidFields() {
    // Arrange + Act
    Notification notification = Notification.builder()
        .to("user@example.com")
        .withContent("Olá")
        .build();

    // Assert
    assertEquals("user@example.com", notification.getRecipient());
    assertEquals("Olá", notification.getContent());
  }

  @ParameterizedTest(name = "recipient inválido: [{0}]")
  @NullSource
  @ValueSource(strings = {"", "   ", "\t"})
  @DisplayName("deve falhar quando recipient é nulo, vazio ou só espaços")
  void shouldThrowWhenRecipientIsBlank(String recipient) {
    // Arrange
    Notification.Builder builder = Notification.builder().withContent("Olá");

    // Act + Assert
    assertThrows(IllegalStateException.class, () -> builder.to(recipient).build());
  }

  @ParameterizedTest(name = "content inválido: [{0}]")
  @NullSource
  @ValueSource(strings = {"", "   ", "\n"})
  @DisplayName("deve falhar quando content é nulo, vazio ou só espaços")
  void shouldThrowWhenContentIsBlank(String content) {
    // Arrange
    Notification.Builder builder = Notification.builder().to("user@example.com");

    // Act + Assert
    assertThrows(IllegalStateException.class, () -> builder.withContent(content).build());
  }

  @Test
  @DisplayName("deve validar o recipient antes do content")
  void shouldValidateRecipientBeforeContent() {
    // Arrange
    Notification.Builder builder = Notification.builder().withContent(null);

    // Act
    IllegalStateException ex = assertThrows(
        IllegalStateException.class,
        () -> builder.to(null).build()
    );

    // Assert
    assertEquals("O destinatário (recipient) é obrigatório.", ex.getMessage());
  }
}
