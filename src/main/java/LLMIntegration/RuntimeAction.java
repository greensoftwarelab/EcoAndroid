package LLMIntegration;

import com.intellij.openapi.actionSystem.AnActionEvent;

public class RuntimeAction extends Action {
    public RuntimeAction() {
        this.actionType = "Runtime";
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        ActionHandler.handle(actionType, e);
    }
}