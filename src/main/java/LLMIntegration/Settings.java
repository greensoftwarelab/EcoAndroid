package LLMIntegration;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class Settings implements Configurable {
    private JPanel mainPanel;
    private ComboBox<String> modelDropdown;
    private SettingsState settingsState;

    public Settings() {
        settingsState = SettingsState.getInstance();
    }

    @Override
    public @Nls(capitalization = Nls.Capitalization.Title) String getDisplayName() {
        return "EcoAndroid Settings";
    }

    @Override
    public @Nullable JComponent createComponent() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel menuPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        JLabel selectLabel = new JLabel("Select the model:");

        modelDropdown = new ComboBox<>(ModelMapping.getDisplayNames());
        modelDropdown.setPreferredSize(new Dimension(400, 30));
        modelDropdown.setMaximumSize(new Dimension(400, 30));

        String selected = settingsState.getSelectedModelDisplayName();
        if (selected != null && ModelMapping.hasModel(selected)) {
            modelDropdown.setSelectedItem(selected);
        } else {
            modelDropdown.setSelectedItem("No models available");
        }

        menuPanel.add(selectLabel);
        menuPanel.add(modelDropdown);
        menuPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel addModel = new JLabel("Add New Model");
        addModel.setFont(addModel.getFont().deriveFont(Font.BOLD, 14f));
        addModel.setAlignmentX(Component.LEFT_ALIGNMENT);
        topPanel.add(menuPanel);

        Dimension labelSize = new Dimension(80, 25);
        Dimension fieldSize = new Dimension(300, 24);

        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));
        fieldsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField modelIDText = new JTextField();
        JTextField apiUrlText = new JTextField();
        JTextField apiKeyText = new JTextField();

        fieldsPanel.add(createFieldPanel("Model ID:", modelIDText, labelSize, fieldSize));
        fieldsPanel.add(createFieldPanel("API URL:", apiUrlText, labelSize, fieldSize));
        fieldsPanel.add(createFieldPanel("API KEY:", apiKeyText, labelSize, fieldSize));

        JButton saveNewModelButton = new JButton("Save New Model");
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        buttonPanel.add(saveNewModelButton);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel idLabel = new JLabel();
        JLabel urlLabel = new JLabel();
        JLabel keyLabel = new JLabel();
        updateInfo(idLabel, urlLabel, keyLabel);
        modelDropdown.addActionListener(e -> updateInfo(idLabel, urlLabel, keyLabel));

        topPanel.add(idLabel);
        topPanel.add(urlLabel);
        topPanel.add(keyLabel);
        topPanel.add(addModel);
        topPanel.add(fieldsPanel);
        topPanel.add(Box.createVerticalStrut(6));
        topPanel.add(buttonPanel);

        saveNewModelButton.addActionListener(e -> {
            String id = modelIDText.getText().trim();
            String url = apiUrlText.getText().trim();
            String key = apiKeyText.getText().trim();
            if (id.isEmpty() || url.isEmpty() || key.isEmpty()) {
                Messages.showErrorDialog("Please fill in all fields.", "Missing Information");
                return;
            }
            ModelMapping.addModel(id, new ModelMapping.ModelInfo(id, url, key));

            modelDropdown.addItem(id);
            modelDropdown.setSelectedItem(id);
        });

        mainPanel.add(topPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        return mainPanel;
    }

    private JPanel createFieldPanel(String labelText, JTextField textField, Dimension labelSize, Dimension fieldSize) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel label = new JLabel(labelText);
        label.setPreferredSize(labelSize);
        textField.setPreferredSize(fieldSize);
        textField.setMaximumSize(fieldSize);
        panel.setMaximumSize(new Dimension(400, 28));
        panel.add(label);
        panel.add(textField);
        return panel;
    }

    @Override
    public boolean isModified() {
        return !settingsState.getSelectedModel().equals(modelDropdown.getSelectedItem());
    }

    @Override
    public void apply() {
        String selectedDisplayName = (String) modelDropdown.getSelectedItem();
        if (selectedDisplayName == null || !ModelMapping.hasModel(selectedDisplayName)) return;

        settingsState.setSelectedModelDisplayName(selectedDisplayName);

        ModelMapping.ModelInfo selectedModelInfo = ModelMapping.getModelInfo(selectedDisplayName);
        if (selectedModelInfo != null) {
            settingsState.setSelectedModel(selectedModelInfo.getId());
            settingsState.setApiKey(selectedModelInfo.getApiKey());
            settingsState.setApiUrl(selectedModelInfo.getApiUrl());
        }
    }

    @Override
    public void reset() {
        String selected = settingsState.getSelectedModelDisplayName();
        if (selected != null && ModelMapping.hasModel(selected)) {
            modelDropdown.setSelectedItem(selected);
        } else {
            modelDropdown.setSelectedItem("No models available");
        }
    }

    @Override
    public void disposeUIResources() {
        mainPanel = null;
    }

    private void updateInfo(JLabel idLabel, JLabel urlLabel, JLabel keyLabel) {
        String selectedDisplayName = (String) modelDropdown.getSelectedItem();
        if (selectedDisplayName == null || !ModelMapping.hasModel(selectedDisplayName)) {
            idLabel.setText("Model ID:      —");
            urlLabel.setText("API URL:      —");
            keyLabel.setText("API Key:      —");
            return;
        }

        ModelMapping.ModelInfo selectedModel = ModelMapping.getModelInfo(selectedDisplayName);
        if (selectedModel != null) {
            idLabel.setText("Model ID:      " + selectedModel.getId());
            urlLabel.setText("API URL:      " + selectedModel.getApiUrl());

            String apiKey = selectedModel.getApiKey();
            String maskedApiKey = apiKey.replaceAll(".", "*");
            if (apiKey.length() > 3) {
                maskedApiKey = maskedApiKey.substring(0, apiKey.length() - 3) + apiKey.substring(apiKey.length() - 3);
            }
            keyLabel.setText("API Key:      " + maskedApiKey);
        }
    }
}
