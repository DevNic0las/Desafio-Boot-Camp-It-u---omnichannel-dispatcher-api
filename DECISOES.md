# Decisões do projeto

Anotações minhas sobre o que escolhi em cada parte e o que abri mão. Não é guia de
boas práticas, são as decisões deste projeto.

## Notification: usei Builder

Poderia ser um record com validação no construtor compacto, que seria bem mais curto.
Fui de Builder porque a chamada fica legível e a validação fica num lugar só, o `build()`.
O custo é mais código e um objeto intermediário que pode ser reaproveitado sem querer,
gerando duas notificações parecidas. Para dois campos é exagero, mas o objetivo era
mostrar o pattern.

A validação lança `IllegalStateException`. Escolhi assim porque o que está errado é o
estado do builder, não o argumento de um método. Poderia ser `IllegalArgumentException`,
mas como o handler trata as duas igual, na prática dá no mesmo.

Não implementei `equals`/`hashCode`. Não uso comparação de notificação em teste nem
em set. Se precisar comparar duas, vou ter que implementar.

## NotificationFactory: deixou de ser static

Antes era `static`. Troquei por instância para conseguir injetar no Facade e mockar no
teste. Se continuasse static, eu teria que usar `mockStatic`, que é chato de manter.
O preço é que agora ela precisa ser bean e quem chama precisa receber no construtor.

Tirei o `if/else if` e coloquei `switch`. O `if/else if` tinha um `throw` de "tipo
inválido" no final que nunca era executado, porque o enum só tem dois valores. Era
código morto. O `switch` sobre enum é exaustivo e o compilador reclama quando eu
adicionar um canal novo, então não preciso lembrar.

Ela ainda faz `new EmailNotificationStrategy()`. Poderia pegar os beans do Spring, mas
ia virar um mapa de injeção e o pattern sumia. Escolhi deixar o `new` visível.

Isso criou uma inconsistência: `EmailNotificationStrategy` tem `@Component` e
`SmsNotificationStrategy` não. Como a fábrica usa `new` nas duas, o `@Component` da
primeira não serve para nada hoje. Se um dia eu injetar as estratégias, a SMS vai
faltar e quebra no startup.

## NotificationFacade

Recebe a fábrica por construtor com `@RequiredArgsConstructor`. Antes chamava o static
direto e não dava para testar sem `mockStatic`.

Continua chamando `AuditLogger.getInstance()` direto. Não injetei porque o Singleton é
uma das coisas que quero mostrar. O efeito colateral é que os testes imprimem no
console e eu não consigo verificar a chamada de log sem `mockStatic`. Aceitei o ruído.

O `dispatch` não devolve nada, o controller monta a mensagem de sucesso. Se um dia
precisar devolver id ou status, muda a assinatura.

## AuditLogger

Mantive como estava, com `getInstance` sincronizado e `System.out`. Em código de
verdade eu usaria o log do Spring, mas aí perco o exemplo de Singleton. O `synchronized`
sem `volatile` não é a forma correta de fazer double-checked locking, mas para a
demonstração atende.

## Tratamento de erro

Usei um record próprio em vez do `ProblemDetail`, que o Spring já tem. O `ProblemDetail`
é padrão e eu reinventei. Fiz assim para controlar o formato e mostrar o record, mas em
produção eu iria de `ProblemDetail`.

O handler trata `IllegalArgumentException` e `IllegalStateException` como 400. Isso é
largo: qualquer `IllegalArgumentException` de biblioteca também vira 400. O certo seria
ter exceções próprias do domínio e mapear cada uma.

O erro de validação devolve `fieldErrors` como mapa. Se dois campos repetirem a mesma
mensagem, o `Collectors.toMap` pega a primeira e ignora a segunda, então não dá erro
por chave duplicada.

## Testes

`Notification` e `NotificationFactory` testei sem Mockito, porque não têm dependência
externa. O `NotificationFacade` tem, então usei mock e `ArgumentCaptor` para conferir
os dados que chegam na estratégia.

Nos testes de validação usei `@ParameterizedTest` com `null`, vazio e espaços, que são
as bordas que os `if` tratam.

Não tem teste do controller com MockMvc nem teste de integração do endpoint. Testei na
mão com `curl`. Fica pendente.

## O que ficou de fora

Sem `equals`/`hashCode`, sem exceções customizadas, sem teste do controller, sem
persistência, sem retry, sem fila, sem autenticação. Nada disso era o objetivo.
