package br.com.serverest.extensions;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;

public class RetryExtension implements TestExecutionExceptionHandler {

    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        Retry retry = context.getRequiredTestMethod().getAnnotation(Retry.class);
        if (retry == null) {
            retry = context.getRequiredTestClass().getAnnotation(Retry.class);
        }

        int maxAttempts = (retry != null) ? retry.value() : 1;

        for (int attempt = 1; attempt < maxAttempts; attempt++) {
            try {
                System.out.printf("[Retry] Tentativa %d/%d — %s%n",
                        attempt + 1, maxAttempts, context.getDisplayName());
                context.getRequiredTestMethod().invoke(context.getRequiredTestInstance());
                return;
            } catch (Exception e) {
                if (attempt == maxAttempts - 1) {
                    throw throwable;
                }
            }
        }
        throw throwable;
    }
}
