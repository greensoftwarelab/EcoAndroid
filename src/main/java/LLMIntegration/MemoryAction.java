package LLMIntegration;

import com.intellij.openapi.actionSystem.AnActionEvent;


public class MemoryAction extends Action {
    public MemoryAction() {
        this.actionType = "Memory";
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        ActionHandler.handle(actionType, e);
    }
}