package LLMIntegration;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class PromptDialog extends DialogWrapper {
    private final String defaultQuestion;
    private JTextArea promptInput;
    private String chosenPrompt = null;


    /**
     * Displays the window for writing a personalised prompt or choosing the default one.
     *
     * @param project project
     * @param defaultQuestion predefined prompt to send to the model
     */
    public PromptDialog(Project project, String defaultQuestion) {
        super(project);
        this.defaultQuestion = defaultQuestion;
        setTitle("Prompt Selection");
        init();
    }


    /**
     * Layouts prompt personalisation window.
     *
     */
    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(500, 200));

        promptInput = new JTextArea(5, 40);
        promptInput.setLineWrap(true);
        promptInput.setWrapStyleWord(true);
        JScrollPane scrollPane = new JBScrollPane(promptInput);

        JLabel yourPromptLabel = new JLabel("Your prompt:");
        JLabel defaultLabel = new JLabel("Default prompt: \"" + defaultQuestion + "\"");

        JButton useDefaultButton = new JButton("Use Default Prompt");
        useDefaultButton.addActionListener(e -> promptInput.setText(defaultQuestion));

        panel.add(yourPromptLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(scrollPane);
        panel.add(Box.createVerticalStrut(10));
        panel.add(defaultLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(useDefaultButton);

        return panel;
    }

    @Override
    protected void doOKAction() {
        chosenPrompt = promptInput.getText().trim();
        if (chosenPrompt.isEmpty()) {
            chosenPrompt = defaultQuestion;
        }
        super.doOKAction();
    }


    /**
     * Returns the prompt chosen to send to the model.
     *
     * @return prompt chosen
     */
    public String getChosenPrompt() {
        return chosenPrompt;
    }
}