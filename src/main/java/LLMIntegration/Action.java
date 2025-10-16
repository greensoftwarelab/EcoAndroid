package LLMIntegration;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

/**
 * Base class for all model suggestion actions.
 * Each subclass defines its own actionType and handles the event accordingly.
 */
public abstract class Action extends AnAction {
    protected String actionType;

    public Action() {
        // Required empty constructor for IntelliJ to instantiate via plugin.xml
    }

    @Override
    public abstract void actionPerformed(AnActionEvent e);
}
