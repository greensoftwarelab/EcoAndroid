package LLMIntegration;

import com.android.annotations.Nullable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CodeSelection extends DialogWrapper {
    private final List<String> codeBlocks;
    private final JBList<String> codeList;
    private int selectedIndex = -1;

    /**
     * Displays a dropdown menu for choosing which alternative to use in comparison window.
     *
     * @param project project
     * @param codeBlocks list with every portion of code present in model's response
     */
    public CodeSelection(Project project, List<String> codeBlocks){
        super(project);
        this.codeBlocks = codeBlocks;
        this.codeList = new JBList<>();

        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (int i = 0; i < this.codeBlocks.size(); i++) {
            listModel.addElement("Suggestion " + (i + 1));
        }
        codeList.setModel(listModel);
        codeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        codeList.setSelectedIndex(0);

        codeList.setPreferredSize(new Dimension(350, 250));

        setTitle("Choose Suggestion to Apply");
        init();
    }


    /**
     * Layouts dropdown menu.
     *
     */
    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JBScrollPane scrollPane = new JBScrollPane(codeList);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    @Override
    protected void doOKAction() {
        selectedIndex = codeList.getSelectedIndex();
        super.doOKAction();
    }

    /**
     * Finds the index of the option selected on the dropdown menu.
     *
     * @return the index of the choice
     */
    public int getSelectedIndex() {
        return selectedIndex;
    }
}
