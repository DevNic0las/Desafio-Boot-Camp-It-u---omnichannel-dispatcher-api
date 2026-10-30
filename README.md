# Omnichannel Dispatcher API

API REST de demonstração criada para um **bootcamp**, com o objetivo de ilustrar, em um único fluxo pequeno e legível, como **quatro design patterns** clássicos (GoF) se combinam num caso de uso real: o envio de notificações por canais diferentes.

Não é uma API pronta para produção — é um **material didático**. Cada classe carrega no Javadoc o nome do pattern que representa.

---

## O fluxo

```
POST /notification
        │
        ▼
NotificationController          (HTTP / validação de entrada)
        │
        ▼
NotificationFacade              ── FACADE ──► uma porta de entrada só
        │
        ├─► Notification.builder()  ── BUILDER ──► objeto validado e imutável
        │
        ├─► NotificationFactory     ── FACTORY ──► escolhe a estratégia pelo canal
        │
        └─► NotificationStrategy    ── STRATEGY ─► e-mail | SMS (+ AuditLogger = SINGLETON)
                │
                ▼
        EmailNotificationStrategy / SmsNotificationStrategy
```

---

## Design Patterns aplicados

### 1. Builder — `model/Notification`

Objeto **imutável** (`final` fields, construtor privado) construído de forma fluente. A validação vive no `build()`, então **é impossível existir uma `Notification` inválida** em memória.

```java
Notification notification = Notification.builder()
    .to("user@example.com")
    .withContent("Olá")
    .build();
```

| Regra | Exceção |
|---|---|
| `recipient` não pode ser nulo, vazio ou só espaços | `IllegalStateException` |
| `content` não pode ser nulo, vazio ou só espaços | `IllegalStateException` |

O builder valida `recipient` **antes** de `content` — ordem determinística, coberta por teste.

### 2. Factory — `factory/NotificationFactory`

Centraliza a decisão de "qual estratégia usar para este canal". Quem chama **não conhece** as implementações concretas.

```java
return switch (type) {
  case EMAIL -> new EmailNotificationStrategy();
  case SMS   -> new SmsNotificationStrategy();
};
```

O `switch` sobre enum é **exaustivo**: ao adicionar um canal novo em `TypeNotification`, o compilador obriga a atualizar a fábrica. É o compilador trabalhando como teste.

### 3. Strategy — `strategy/NotificationStrategy`

Interface única (`void send(Notification)`), com uma implementação por canal. O `NotificationFacade` depende apenas da abstração — trocar ou adicionar canais não altera o facade.

### 4. Facade — `service/NotificationFacade`

Uma única chamada (`dispatch(target, text, channel)`) esconde todo o encadeamento Builder → Factory → Strategy → Logger. O controller não precisa saber que quatro objetos participam do processo.

### 5. Singleton — `logging/AuditLogger`

Instância única, acesso global, construtor privado — o exemplo canônico do pattern, mantido de forma deliberada para demonstração. É consumido pelo facade via `AuditLogger.getInstance().log(...)`.

> **Nota didática:** em código de produção, log normalmente é resolvido com SLF4J (que já vem no `spring-boot-starter`) em vez de um singleton com `System.out`. Aqui o Singleton existe porque o objetivo é **mostrá-lo**.

---

## Stack

| Item | Versão |
|---|---|
| Java | 21 |
| Spring Boot | 4.1.2 |
| Spring Web MVC + Validation | via starter |
| Lombok | via starter |
| springdoc-openapi (Swagger UI) | 3.2.0 |
| JUnit 5 + Mockito + AssertJ | via `spring-boot-starter-test` |

---

## Como rodar

```bash
./mvnw spring-boot:run
```

Aplicação em `http://localhost:8080`.

**Swagger UI:** `http://localhost:8080/swagger-ui.html`
**OpenAPI JSON:** `http://localhost:8080/v3/api-docs`

---

## Endpoint

### `POST /notification`

**Requisição**

```bash
curl -X POST http://localhost:8080/notification \
  -H "Content-Type: application/json" \
  -d '{
    "recipient": "user@example.com",
    "content": "Olá, mundo!",
    "type": "EMAIL"
  }'
```

```json
{ "recipient": "user@example.com", "content": "Olá, mundo!", "type": "EMAIL" }
```

`type` aceita `EMAIL` ou `SMS`.

**Resposta de sucesso — `200 OK`**

```
Notificação enviada com sucesso!
```

**Resposta de erro de validação — `400 Bad Request`**

```json
{
  "timestamp": "2026-10-30T16:59:44.660538159Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Erro de validação na requisição.",
  "path": "/notification",
  "fieldErrors": {
    "recipient": "O campo recipient é obrigatório.",
    "content": "O campo content é obrigatório.",
    "type": "O campo type é obrigatório."
  }
}
```

Erros de negócio (`IllegalStateException`, `IllegalArgumentException`) também retornam `400`, com a mensagem original em `message`. Qualquer outra exceção retorna `500` com mensagem genérica — sem vazar stack trace.

---

## Testes

```bash
./mvnw test
```

```
NotificationTest ......................... 10 testes   (Builder)
NotificationFactoryTest ..................  6 testes   (Factory, sem Mockito)
NotificationFacadeTest ...................  7 testes   (Facade, com Mockito)
OmnichannelDispatcherApiApplicationTests .  1 teste    (smoke test de contexto)
```

24 testes, todos verdes.

A separação não é acidental — é a lição principal:

| Classe sob teste | Mockito? | Por quê |
|---|---|---|
| `Notification` | ❌ | Value object sem colaboradores. Só regras próprias. |
| `NotificationFactory` | ❌ | Só devolve objetos. Não depende de nada externo. |
| `NotificationFacade` | ✅ | **Tem** colaboradores (`Factory`, `Strategy`) que precisam ser isolados. |

**Testar o que a classe faz, não como ela faz.** O teste do facade usa `ArgumentCaptor` para verificar que `recipient` e `content` chegam intactos na estratégia — e `verifyNoInteractions` para garantir que uma entrada inválida **não** dispara nenhum envio (falha sem efeito colateral).

Técnicas demonstradas:

- `@ParameterizedTest` + `@ValueSource` / `@NullSource` — bordas de string (`null`, `""`, `"   "`, `"\t"`)
- `@EnumSource` — canário: novo valor no enum sem tratamento quebra o teste sozinho
- `@MethodSource` — tabela de mapeamento tipo → estratégia
- `ArgumentCaptor` — inspecionar o objeto que atravessou a fronteira
- `assertThrows` com retorno — validar também a mensagem da exceção

---

## Estrutura

```
src/main/java/com/github/devnic0las/omnichannel_dispatcher_api/
├── controller/   NotificationController      HTTP, entrada/saída
├── dto/          NotificationRequest         record + Bean Validation
├── exception/    GlobalExceptionHandler      @RestControllerAdvice → RFC-ish
│                 ApiErrorResponse
├── factory/      NotificationFactory         FACTORY
├── logging/      AuditLogger                 SINGLETON
├── model/        Notification                BUILDER
│                 TypeNotification            enum de canais
├── service/      NotificationFacade          FACADE
└── strategy/     NotificationStrategy        STRATEGY
                  EmailNotificationStrategy
                  SmsNotificationStrategy
```

---

## Adicionando um novo canal (ex.: `WHATSAPP`)

O `switch` exaustivo da fábrica **não compila** até você tratar o novo caso — e o `@EnumSource` do teste falha. O caminho é:

1. Adicionar `WHATSAPP` em `TypeNotification`
2. Criar `WhatsappNotificationStrategy implements NotificationStrategy`
3. Adicionar o `case WHATSAPP` em `NotificationFactory`

O compilador e os testes guiam a mudança. Nenhum outro arquivo precisa ser tocado — é o ganho concreto de Strategy + Factory.
