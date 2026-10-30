# omnichannel-dispatcher-api

API de exemplo que envia notificações por e-mail ou SMS. Fiz para o bootcamp, com o
objetivo de mostrar alguns design patterns funcionando juntos num fluxo pequeno.

Não é uma API pronta para produção. É material de estudo.

## Patterns usados

- **Builder** em `Notification`: monta o objeto com `to()` e `withContent()` e valida no
  `build()`, então não existe notificação inválida em memória.
- **Factory** em `NotificationFactory`: recebe o tipo do canal e devolve a estratégia
  certa.
- **Strategy** em `NotificationStrategy`: uma interface e uma implementação por canal.
- **Facade** em `NotificationFacade`: o `dispatch()` esconde o resto, o controller só
  chama ele.
- **Singleton** em `AuditLogger`: instância única, usada para registrar o envio.

O caminho é: controller recebe a requisição, chama o facade, o facade monta a
`Notification` com o Builder, pega a estratégia na Factory e envia.

## Como rodar

```
./mvnw spring-boot:run
```

Roda em `http://localhost:8080`. O Swagger fica em `/swagger-ui.html`.

## Endpoint

`POST /notification`

```json
{
  "recipient": "user@example.com",
  "content": "Olá, mundo!",
  "type": "EMAIL"
}
```

`type` aceita `EMAIL` ou `SMS`.

Se der certo, volta `200` com a mensagem "Notificação enviada com sucesso!".

Se faltar algum campo, volta `400` com a lista de erros:

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

## Testes

```
./mvnw test
```

São 24 testes:

- `NotificationTest`: 10, testando o Builder e as validações.
- `NotificationFactoryTest`: 6, testando qual estratégia volta para cada tipo.
- `NotificationFacadeTest`: 7, testando o facade com a fábrica e a estratégia mockadas.
- `OmnichannelDispatcherApiApplicationTests`: 1, só para ver se o contexto sobe.

Usei Mockito só no teste do facade, porque é a única classe que depende de outras. O
Builder e a Factory não têm dependência, então testei direto.

## Adicionar um canal novo

Para adicionar, por exemplo, WhatsApp:

1. Adiciona `WHATSAPP` no enum `TypeNotification`.
2. Cria `WhatsappNotificationStrategy` implementando `NotificationStrategy`.
3. Adiciona o caso na `NotificationFactory`.

O `switch` da fábrica não compila enquanto o caso não existir, então não tem como
esquecer.
