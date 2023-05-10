package org.hubz.common.exceptions;

import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;

/**
 * @author hubz
 * @date 2023/3/12 10:18
 */
public class LoadExtraPropertyFileAnalyzer extends AbstractFailureAnalyzer<LoadExtraPropertyFileException> {
    @Override
    protected FailureAnalysis analyze(Throwable rootFailure, LoadExtraPropertyFileException cause) {
        return new FailureAnalysis(cause.getMessage(), "加载额外依赖的配置文件错误!", cause);
    }
}
