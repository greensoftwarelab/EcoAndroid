package LLMIntegration;

import com.intellij.openapi.actionSystem.AnActionEvent;


public class EnergyAction extends Action {
    public EnergyAction() {
        this.actionType = "Energy";
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        ActionHandler.handle(actionType, e);
    }
}