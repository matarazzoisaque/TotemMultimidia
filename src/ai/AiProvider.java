package ai;

public enum AiProvider {
    GEMINI("gemini"),
    GROK("grok");

    private final String providerName;

    AiProvider(String providerName) {
        this.providerName = providerName;
    }

    public String getProviderName() {
        return providerName;
    }

    public static AiProvider fromName(String providerName) {
        if (providerName == null) {
            return GEMINI;
        }

        for (AiProvider provider : values()) {
            if (provider.providerName.equalsIgnoreCase(providerName.trim())) {
                return provider;
            }
        }

        return GEMINI;
    }
}
