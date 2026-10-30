package com.github.devnic0las.omnichannel_dispatcher_api.model;

import lombok.*;



@Getter
public class Notification {
  private final String recipient;
  private final String content;

  private Notification(Builder builder) {
    this.recipient = builder.recipient;
    this.content = builder.content;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private String recipient;
    private String content;

    public Builder to(String recipient) {
      this.recipient = recipient;
      return this;
    }

    public Builder withContent(String content) {
      this.content = content;
      return this;
    }

    public Notification build() {
      if (recipient == null || recipient.isBlank()) {
        throw new IllegalStateException("O destinatário (recipient) é obrigatório.");
      }
      if (content == null || content.isBlank()) {
        throw new IllegalStateException("O conteúdo (content) não pode ser vazio.");
      }
      return new Notification(this);
    }
  }


}
