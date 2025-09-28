package com.robottx.todo.service.secret;

public interface SecretService {

    String getSecret(String secretName);

    String getValkeyUsername();

    String getValkeyPassword();

    String getClientSecret();

}
