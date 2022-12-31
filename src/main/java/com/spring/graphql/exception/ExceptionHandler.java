package com.spring.graphql.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExceptionHandler {

    public static void logError(final Throwable e, final Object o) {
        log.error("Error=> msg:{} object: {}", e.getMessage(), o);
    }
}
