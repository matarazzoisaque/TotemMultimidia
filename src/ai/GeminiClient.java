package ai;

import config.AiConfig;

public class GeminiClient implements AiClient {

    private final AiConfig config;

    public GeminiClient() {
        this(AiConfig.load());
    }

    public GeminiClient(AiConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("AiConfig cannot be null.");
        }
        this.config = config;
    }

    @Override
    public String getProviderName() {
        return AiProvider.GEMINI.getProviderName();
    }

    @Override
    public boolean isConfigured() {
        return config.hasGeminiApiKey() && hasText(config.getGeminiModel());
    }

    public String getModel() {
        return config.getGeminiModel();
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
