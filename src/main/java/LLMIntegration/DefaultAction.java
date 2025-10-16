package LLMIntegration;

import com.intellij.openapi.actionSystem.AnActionEvent;

public class DefaultAction extends Action {
    public DefaultAction() {
        this.actionType = "Default";
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        ActionHandler.handle(actionType, e);
    }
}
