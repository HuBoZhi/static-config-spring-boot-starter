package org.hubz.common.exceptions;

import org.springframework.boot.ExitCodeGenerator;

public class PropertyNotFoundException extends RuntimeException implements ExitCodeGenerator {

    @Override
    public int getExitCode() {
        return 1010;
    }

    public PropertyNotFoundException(String message) {
        super(message);
    }

    public PropertyNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}