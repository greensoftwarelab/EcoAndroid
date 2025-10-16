package LLMIntegration;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {


    /**
     * Formats the code to make it readable, applying identation and spaces.
     *
     * @param code code
     * @param project project
     * @return formated code
     */
    public static String formatCode(String code, Project project) {
        PsiFile tempPsiFile = PsiFileFactory.getInstance(project).createFileFromText("temp.java", JavaFileType.INSTANCE, code);
        CodeStyleManager.getInstance(project).reformat(tempPsiFile);
        return tempPsiFile.getText();
    }


    /**
     * Extracts all the code blocks present in the model's response. In this case, the code appears delimited by '---'.
     *
     * @param response model's response
     * @return structure with the code blocks
     */
    public static List<String> extractCode(String response){
        List<String> codeBlocks = new ArrayList<>();
        Pattern pattern = Pattern.compile("---\\s*java(.*?)---", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(response);

        while (matcher.find()){
            codeBlocks.add(matcher.group(1));
        }
        return codeBlocks;
    }


    /**
     * Replaces the code in the file, with the suggestions given by the model.
     *
     * @param document document
     * @param editor editor
     * @param newCode code suggested by the model
     * @param selectedMethod name of the method to be replaced
     */
    public static void replaceCode(com.intellij.openapi.editor.Document document, com.intellij.openapi.editor.Editor editor, String newCode, @Nullable String selectedMethod){
        com.intellij.openapi.command.WriteCommandAction.runWriteCommandAction( editor.getProject(), () -> {
            PsiFile psiFile = PsiDocumentManager.getInstance(editor.getProject()).getPsiFile(document);

            if (selectedMethod != null){
                PsiMethod[] methods = PsiTreeUtil.collectElementsOfType(psiFile, PsiMethod.class).toArray(PsiMethod[]::new);
                if (methods != null) {
                    for (PsiMethod method : methods) {
                        if (selectedMethod.equals(method.getName())) {
                            method.replace(createMethodFromText(psiFile, newCode));
                            break;
                        }
                    }
                }
            } else {
                String selectedCode = editor.getSelectionModel().getSelectedText();
                if (selectedCode != null && !selectedCode.isBlank()) {
                    int start = editor.getSelectionModel().getSelectionStart();
                    int end = editor.getSelectionModel().getSelectionEnd();
                    document.replaceString(start, end, newCode);
                } else{
                    document.setText(newCode);
                }
            }
        });
    }


    /**
     * Converts the string that contains the method suggested in a PSI method.
     *
     * @param psiFile file where the method is located
     * @param newCode string with the suggested method
     * @return method suggested in PsiMethod format
     */
    private static PsiMethod createMethodFromText(PsiFile psiFile, String newCode) {
        return JavaPsiFacade.getInstance(psiFile.getProject())
                .getElementFactory()
                .createMethodFromText(newCode, psiFile);
    }


    /**
     * Extracts the relevant information from the model's response (JSON).
     *
     * @param json model's response in JSON
     * @return relevant content from model's response
     */
    public static String extractResponseText(String json) {
        JSONObject obj = new JSONObject(json);
        JSONArray choices = obj.getJSONArray("choices");
        JSONObject message = choices.getJSONObject(0).getJSONObject("message");
        return message.getString("content");
    }


    /**
     * Lists the names of the methods in the file.
     *
     * @param file file
     * @return list of the method's names
     */
    public static List<String> listMethods(PsiFile file){
        List<String> methodsList = new ArrayList<>();
        PsiClass[] classes = PsiTreeUtil.getChildrenOfType(file, PsiClass.class);
        if (classes != null) {
            for (PsiClass psiClass : classes) {
                PsiMethod[] methods = psiClass.getMethods();
                for (PsiMethod method : methods) {
                    methodsList.add(method.getName());
                }
            }
        }
        return methodsList;
    }


    /**
     * Displays the window with the model's response, and the options from there.
     *
     * @param document document
     * @param editor editor
     * @param code code in the file
     * @param response model's response
     * @param selectedMethod name of the method selected to optimise
     */
    public static void responseBox(com.intellij.openapi.editor.Document document, com.intellij.openapi.editor.Editor editor, String code, String response, @Nullable String selectedMethod) {
        List<String> codeBlocks = extractCode(response);

        if (codeBlocks.isEmpty()) {
            Messages.showInfoMessage(response, "EcoAndroid - LLM Suggestions");
            return;
        }

        String[] options = {"Refactor", "Ok"};
        int choice = Messages.showDialog(response, "EcoAndroid - LLM Suggestions", options, 1, null);

        if (choice == 0) {
            String selectedCode;

            if (codeBlocks.size() > 1) {
                CodeSelection selectionDialog = new CodeSelection(editor.getProject(), codeBlocks);
                selectionDialog.show();

                int selectedIndex = selectionDialog.getSelectedIndex();
                if(selectedIndex == -1) return;

                selectedCode = codeBlocks.get(selectedIndex);
            } else {
                selectedCode = codeBlocks.get(0);
            }

            selectedCode = formatCode(selectedCode, editor.getProject());

            while(true) {
                CodeReview dialog = new CodeReview(editor.getProject(), code, selectedCode);
                dialog.show();

                if (dialog.isConfirmed()) {
                    replaceCode(document, editor, selectedCode, selectedMethod);
                    break;
                } else {
                    CodeSelection selectionDialog = new CodeSelection(editor.getProject(), codeBlocks);
                    selectionDialog.show();
                    int selectedIndex = selectionDialog.getSelectedIndex();
                    if (selectedIndex == -1) return;
                    selectedCode = codeBlocks.get(selectedIndex);
                    selectedCode = formatCode(selectedCode, editor.getProject());
                }
            }
        }
    }


    /**
     * Handles the flow when the option 'Generate Documentation' is selected.
     *
     * @param modelInfo information of the model used
     * @param project project
     * @param editor editor
     */
    public static void handleDocumentation(List<String> modelInfo, Project project, Editor editor){
        GenerateDocumentation documentation = new GenerateDocumentation();
        documentation.init(project, editor);
        PsiMethod method = documentation.getMethod();
        if (method != null) {
            String methodText = method.getText();
            Notification notification = NotificationGroupManager.getInstance().getNotificationGroup("LLM Notifications")
                    .createNotification("Generating documentation. It will only take a few seconds...", NotificationType.INFORMATION);
            notification.notify(project);

            com.intellij.openapi.application.ApplicationManager.getApplication().executeOnPooledThread(() -> {
                try {
                    String rawJson = Communication.queryModel(modelInfo.get(0), modelInfo.get(1), modelInfo.get(2), "Generate a javadoc comment to apply to this method. Send just the comment, no any kind of explanation.\n\n" + methodText);
                    String javadoc = extractResponseText(rawJson);
                    documentation.setDocumentation(javadoc);
                    notification.expire();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    com.intellij.openapi.application.ApplicationManager.getApplication().invokeLater(() -> {
                        Messages.showErrorDialog("Error communicating with the model: " + ex.getMessage(), "Error");
                    });
                }
            });
        }
    }


    /**
     * Connects the flow with the model communication, acting as an intermediary.
     *
     * @param modelInfo information of the model used
     * @param document document
     * @param editor editor
     * @param project project
     * @param code code in the file
     * @param prompt prompt to send to the model
     * @param selectedMethod name of the method selected to optimise
     */
    public static void requestModel(List<String> modelInfo, Document document, Editor editor, Project project, String code, String prompt, String selectedMethod){
        Notification notification = NotificationGroupManager.getInstance().getNotificationGroup("LLM Notifications")
                .createNotification("Waiting for model response. It will only take a few seconds...", NotificationType.INFORMATION);
        notification.notify(project);

        com.intellij.openapi.application.ApplicationManager.getApplication().executeOnPooledThread(() -> {
            try {
                String rawJson = Communication.queryModel(modelInfo.get(0), modelInfo.get(1), modelInfo.get(2), prompt + "\n\n" + code);
                String response = extractResponseText(rawJson);
                response = response.replaceAll("##+", "\n\n").replace("```", "\n---\n");

                String finalResponse = response;
                com.intellij.openapi.application.ApplicationManager.getApplication().invokeLater(() -> {
                    responseBox(document, editor, code, finalResponse, selectedMethod);
                    notification.expire();
                });
            } catch (IOException ex) {
                ex.printStackTrace();
                com.intellij.openapi.application.ApplicationManager.getApplication().invokeLater(() -> {
                    Messages.showErrorDialog("Error communicating with the model: " + ex.getMessage(), "Error");
                });
            }
        });
    }
}