package com.github.devnic0las.omnichannel_dispatcher_api;

import com.github.devnic0las.omnichannel_dispatcher_api.factory.NotificationFactory;
import com.github.devnic0las.omnichannel_dispatcher_api.model.TypeNotification;
import com.github.devnic0las.omnichannel_dispatcher_api.strategy.EmailNotificationStrategy;
import com.github.devnic0las.omnichannel_dispatcher_api.strategy.NotificationStrategy;
import com.github.devnic0las.omnichannel_dispatcher_api.strategy.SmsNotificationStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("NotificationFactory (padrão Factory)")
class NotificationFactoryTest {

  private final NotificationFactory factory = new NotificationFactory();

  static Stream<Arguments> tipoParaEstrategia() {
    return Stream.of(
        arguments(TypeNotification.EMAIL, EmailNotificationStrategy.class),
        arguments(TypeNotification.SMS, SmsNotificationStrategy.class)
    );
  }

  @ParameterizedTest(name = "{0} -> {1}")
  @MethodSource("tipoParaEstrategia")
  @DisplayName("deve devolver a estratégia correta para cada tipo")
  void shouldReturnStrategyForEachType(
      TypeNotification type,
      Class<? extends NotificationStrategy> expectedStrategy
  ) {
    // Act
    NotificationStrategy strategy = factory.getStrategy(type);

    // Assert
    assertInstanceOf(expectedStrategy, strategy);
  }

  @ParameterizedTest(name = "tipo: {0}")
  @EnumSource(TypeNotification.class)
  @DisplayName("nenhum tipo do enum pode cair no caso inválido")
  void shouldNeverThrowForAnyEnumValue(TypeNotification type) {
    // Act + Assert — canário: novo valor no enum sem case no switch quebra aqui
    assertNotNull(factory.getStrategy(type));
  }

  @Test
  @DisplayName("deve falhar com IllegalArgumentException quando o tipo é nulo")
  void shouldThrowIllegalArgumentExceptionWhenTypeIsNull() {
    // Act + Assert
    IllegalArgumentException ex = assertThrows(
        IllegalArgumentException.class,
        () -> factory.getStrategy(null)
    );

    // Assert
    assertNotNull(ex.getMessage());
  }

  @Test
  @DisplayName("deve criar uma nova instância a cada chamada (fábrica sem estado)")
  void shouldReturnANewInstanceOnEachCall() {
    // Act
    NotificationStrategy first = factory.getStrategy(TypeNotification.EMAIL);
    NotificationStrategy second = factory.getStrategy(TypeNotification.EMAIL);

    // Assert
    assertNotSame(first, second);
  }
}
