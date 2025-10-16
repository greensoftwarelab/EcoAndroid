package LLMIntegration;

import com.intellij.openapi.components.*;

@State(
        name = "LLMIntegration.LLMSettingsState",
        storages = @Storage("LLMIntegration.LLMSettings.xml")
)
@Service
public final class SettingsState implements PersistentStateComponent<SettingsState> {
    private String selectedModelDisplayName;
    private String selectedModel;
    private String apiUrl;
    private String apiKey;

    public SettingsState() {
        // Define um modelo inicial seguro, se existir
        String[] availableModels = ModelMapping.getDisplayNames();
        if (availableModels.length > 0 && ModelMapping.hasModel(availableModels[0])) {
            selectedModelDisplayName = availableModels[0];
            ModelMapping.ModelInfo info = ModelMapping.getModelInfo(selectedModelDisplayName);
            selectedModel = info.getId();
            apiUrl = info.getApiUrl();
            apiKey = info.getApiKey();
        } else {
            // Inicializa com valores vazios se não houver modelos
            selectedModelDisplayName = "";
            selectedModel = "";
            apiUrl = "";
            apiKey = "";
        }
    }

    public static SettingsState getInstance() {
        return ServiceManager.getService(SettingsState.class);
    }

    public String getSelectedModelDisplayName() {
        return selectedModelDisplayName;
    }

    public void setSelectedModelDisplayName(String selectedModelDisplayName) {
        this.selectedModelDisplayName = selectedModelDisplayName;
    }

    public String getSelectedModel() {
        return selectedModel;
    }

    public void setSelectedModel(String selectedModel) {
        this.selectedModel = selectedModel;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    @Override
    public SettingsState getState() {
        return this;
    }

    @Override
    public void loadState(SettingsState state) {
        if (state != null) {
            this.selectedModelDisplayName = state.selectedModelDisplayName;
            this.selectedModel = state.selectedModel;
            this.apiKey = state.apiKey;
            this.apiUrl = state.apiUrl;
        }
    }
}
