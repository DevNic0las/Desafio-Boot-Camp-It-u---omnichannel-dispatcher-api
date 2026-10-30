package com.github.devnic0las.omnichannel_dispatcher_api.exception;

import java.time.Instant;
import java.util.Map;

public record ApiErrorResponse(
    Instant timestamp,
    int status,
    String error,
    String message,
    String path,
    Map<String, String> fieldErrors
) {
}
