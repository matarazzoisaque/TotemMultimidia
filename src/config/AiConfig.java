package config;

import io.github.cdimascio.dotenv.Dotenv;

public class AiConfig {

    private static final String DEFAULT_GEMINI_MODEL = "gemini-1.5-flash";
    private static final String DEFAULT_AI_PROVIDER = "gemini";

    private final Dotenv dotenv;

    private AiConfig(Dotenv dotenv) {
        this.dotenv = dotenv;
    }

    public static AiConfig load() {
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();
        return new AiConfig(dotenv);
    }

    public String getGeminiApiKey() {
        return getValue("GEMINI_API_KEY", "");
    }

    public String getGeminiModel() {
        return getValue("GEMINI_MODEL", DEFAULT_GEMINI_MODEL);
    }

    public String getGrokApiKey() {
        return getValue("GROK_API_KEY", "");
    }

    public String getGrokModel() {
        return getValue("GROK_MODEL", "");
    }

    public String getAiProvider() {
        return getValue("AI_PROVIDER", DEFAULT_AI_PROVIDER);
    }

    public boolean hasGeminiApiKey() {
        return hasValue("GEMINI_API_KEY");
    }

    public boolean hasGrokApiKey() {
        return hasValue("GROK_API_KEY");
    }

    private boolean hasValue(String variableName) {
        return !isBlank(getValue(variableName, ""));
    }

    private String getValue(String variableName, String defaultValue) {
        String value = dotenv.get(variableName);

        if (isBlank(value)) {
            value = System.getenv(variableName);
        }

        if (isBlank(value)) {
            return defaultValue;
        }

        return value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
