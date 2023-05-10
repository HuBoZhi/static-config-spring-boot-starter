package org.hubz.common.exceptions;

import org.springframework.boot.ExitCodeGenerator;

/**
 * @author hubz
 * @date 2023/3/12 10:17
 */
public class LoadExtraPropertyFileException extends RuntimeException
        implements ExitCodeGenerator {

    @Override
    public int getExitCode() {
        return 1011;
    }

    public LoadExtraPropertyFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
