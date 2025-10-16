package LLMIntegration;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBScrollPane;

import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class CodeReview extends DialogWrapper {
    private final String originalCode;
    private final String newCode;
    private final Project project;
    private boolean confirmed = false;


    /**
     * Displays the window for code comparison, between the original code and the suggested one.
     *
     * @param project project
     * @param originalCode original code
     * @param newCode suggested code
     */
    public CodeReview(Project project, String originalCode, String newCode){
        super(project);
        this.project = project;
        this.originalCode = originalCode;
        this.newCode = newCode;
        setTitle("EcoAndroid - Review Suggested Code");
        init();
    }


    /**
     * Layouts comparison window.
     *
     */
    @Override
    protected  @Nullable JComponent createCenterPanel(){
        JPanel panel = new JPanel(new GridLayout(1,2));

        JTextArea originalCodeArea = new JTextArea(originalCode);
        JTextArea newCodeArea = new JTextArea(newCode);

        originalCodeArea.setEditable(false);
        newCodeArea.setEditable(false);

        panel.add(new JBScrollPane(originalCodeArea));
        panel.add(new JBScrollPane(newCodeArea));

        return panel;
    }

    @Override
    protected void doOKAction() {
        confirmed = true;
        super.doOKAction();
    }


    /**
     * Verifies if code replacement was requested.
     *
     * @return true for replacement, false otherwise
     */
    public boolean isConfirmed() {
        return confirmed;
    }
}
