package LLMIntegration;

import com.intellij.notification.*;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;

import java.util.ArrayList;
import java.util.List;

public class ActionHandler {
    public static void handle(String actionType, AnActionEvent e) {
        Project project = e.getProject();
        var editor = e.getData(com.intellij.openapi.actionSystem.CommonDataKeys.EDITOR);
        var document = editor != null ? editor.getDocument() : null;

        if (project == null || document == null) return;

        SettingsState settingsState = SettingsState.getInstance();

        List<String> modelInfo = new ArrayList<>();
        modelInfo.add(settingsState.getSelectedModel());
        modelInfo.add(settingsState.getApiUrl());
        modelInfo.add(settingsState.getApiKey());

        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
        if (psiFile == null) return;

        List<String> methods = Utils.listMethods(psiFile);
        PsiMethod[] methodsFunc = PsiTreeUtil.collectElementsOfType(psiFile, PsiMethod.class).toArray(PsiMethod[]::new);
        String code = document.getText();

        if (actionType.equals("Documentation")) {
            Utils.handleDocumentation(modelInfo, project, editor);
            return;
        }

        String prompt = getPrompt(actionType);
        AnalysisSelection analysisSelection = new AnalysisSelection(project, methods);

        if (!analysisSelection.showAndGet()) return;

        String selectedMethod = analysisSelection.getSelectedMethod();
        if (selectedMethod != null && methodsFunc != null) {
            prompt += " strictly of the method " + selectedMethod;
            for (PsiMethod method : methodsFunc) {
                if (selectedMethod.equals(method.getName())) {
                    code = method.getText();

                    PsiElement prev = method.getPrevSibling();
                    while (prev != null && !(prev instanceof PsiComment)) {
                        prev = prev.getPrevSibling();
                    }
                    if (prev != null) {
                        String comment = prev.getText();
                        code = comment + "\n" + code;
                    }
                }
            }
        } else if (analysisSelection.isSelectedCode()) {
            String selected = editor.getSelectionModel().getSelectedText();
            if (selected != null) {
                code = selected;
            } else {
                Notification notification = NotificationGroupManager.getInstance()
                        .getNotificationGroup("LLM Notifications")
                        .createNotification(
                                "No Code Selected",
                                "No code was selected. The entire file will be analyzed.",
                                NotificationType.INFORMATION
                        );
                notification.notify(project);
            }
        }

        if (prompt.isEmpty()) {
            PromptDialog dialog = new PromptDialog(project, "How can I improve this code? Give me an improved version of it.");
            dialog.show();
            if (!dialog.isOK()) return;
            prompt = dialog.getChosenPrompt();
        }

        Utils.requestModel(modelInfo, document, editor, project, code, prompt, selectedMethod);
    }

    private static String getPrompt(String actionType) {
        switch (actionType) {
            case "Energy":
                return "How can I improve this code regarding energy issues? Give me an improved version";
            case "Memory":
                return "How can I improve this code regarding memory issues? Give me an improved version";
            case "Runtime":
                return "How can I improve this code runtime performance? Give me an improved version";
            default:
                return "";
        }
    }
}
