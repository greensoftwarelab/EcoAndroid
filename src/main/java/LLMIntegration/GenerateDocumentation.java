package LLMIntegration;

import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;

public class GenerateDocumentation {
    private PsiMethod method = null;
    private Project project = null;
    private Editor editor = null;
    private PsiFile psiFile = null;

    public void init(Project project, Editor editor){
        this.project = project;
        this.editor = editor;
        PsiDocumentManager.getInstance(this.project).commitAllDocuments();
        this.psiFile = PsiDocumentManager.getInstance(this.project).getPsiFile(this.editor.getDocument());
    }

    /**
     * Applies a comment in Javadoc format exactly above the method for which it was requested.
     *
     * @param javadoc string with the javadoc comment to apply to the method
     */
    public void setDocumentation(String javadoc) {
        if (!javadoc.isEmpty() && this.method != null) {
            WriteCommandAction.runWriteCommandAction(project, () -> {
                PsiComment comment = PsiElementFactory.getInstance(project).createCommentFromText(javadoc, psiFile);

                PsiElement parent = this.method.getParent();
                if (parent != null && this.method.isValid()) {
                    try {
                        parent.addBefore(comment, this.method);
                    } catch (Exception e) {
                        NotificationGroupManager.getInstance()
                                .getNotificationGroup("LLM Notifications")
                                .createNotification("Failed to add documentation: " + e.getMessage(), NotificationType.ERROR)
                                .notify(project);
                    }
                } else {
                    NotificationGroupManager.getInstance()
                            .getNotificationGroup("LLM Notifications")
                            .createNotification("Failed to add documentation: Invalid method or parent. " +
                                    "Method: " + this.method + ", Parent: " + parent, NotificationType.ERROR)
                            .notify(project);
                }
            });
        } else {
            NotificationGroupManager.getInstance()
                    .getNotificationGroup("LLM Notifications")
                    .createNotification("Failed to add documentation: Empty javadoc or method is null", NotificationType.WARNING)
                    .notify(project);
        }
    }


    /**
     * Finds the body of the method for which the comment was requested.
     *
     * @return body of the method to comment
     */
    public PsiMethod getMethod() {
        if (psiFile != null) {
            PsiElement elementAtCaret = psiFile.findElementAt(this.editor.getCaretModel().getOffset());
            PsiMethod method = PsiTreeUtil.getParentOfType(elementAtCaret, PsiMethod.class);

            if (method == null || !method.isValid()) {
                NotificationGroupManager.getInstance()
                        .getNotificationGroup("LLM Notifications")
                        .createNotification("No valid method found at the cursor position", NotificationType.WARNING)
                        .notify(this.project);
                return null;
            }

            this.method = method;
            return method;
        } else {
            NotificationGroupManager.getInstance()
                    .getNotificationGroup("LLM Notifications")
                    .createNotification("Failed to get method: PsiFile is null", NotificationType.ERROR)
                    .notify(this.project);
            return null;
        }
    }
}
