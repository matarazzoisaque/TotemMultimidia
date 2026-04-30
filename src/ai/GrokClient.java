package ai;

import config.AiConfig;

public class GrokClient implements AiClient {

    private final AiConfig config;

    public GrokClient() {
        this(AiConfig.load());
    }

    public GrokClient(AiConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("AiConfig cannot be null.");
        }
        this.config = config;
    }

    @Override
    public String getProviderName() {
        return AiProvider.GROK.getProviderName();
    }

    @Override
    public boolean isConfigured() {
        return config.hasGrokApiKey() && hasText(config.getGrokModel());
    }

    public String getModel() {
        return config.getGrokModel();
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
