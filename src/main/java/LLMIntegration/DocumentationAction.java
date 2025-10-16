package LLMIntegration;

import com.intellij.openapi.actionSystem.AnActionEvent;


public class DocumentationAction extends Action {
    public DocumentationAction() {
        this.actionType = "Documentation";
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        ActionHandler.handle(actionType, e);
    }
}

