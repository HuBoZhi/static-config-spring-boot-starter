package org.hubz.common.exceptions;

import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;

public class PropertyNotFoundAnalyzer extends AbstractFailureAnalyzer<PropertyNotFoundException> {
    @Override
    protected FailureAnalysis analyze(Throwable rootFailure, PropertyNotFoundException cause) {
        return new FailureAnalysis(cause.getMessage(), "配置项缺失", cause);
    }
}