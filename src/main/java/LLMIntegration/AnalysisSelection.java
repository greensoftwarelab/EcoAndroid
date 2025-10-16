package LLMIntegration;

import com.android.annotations.Nullable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AnalysisSelection extends DialogWrapper {
    private final List<String> methods;
    private final JBList<String> methodsList;
    private int selectedIndex = -1;
    private boolean isSelectedCode = false;

    public AnalysisSelection(Project project, List<String> methods) {
        super(project);
        this.methods = methods;
        this.methodsList = new JBList<>();

        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (String method : methods) {
            listModel.addElement(method);
        }
        methodsList.setModel(listModel);
        methodsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        methodsList.setSelectedIndex(0);

        methodsList.setPreferredSize(new Dimension(500, 400));

        setTitle("Choose Method to Analyse");
        init();
    }


    /**
     * Layouts dropdown menu.
     *
     */
    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JBScrollPane scrollPane = new JBScrollPane(methodsList);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }


    /**
     * Layouts dropdown menu window.
     *
     */
    @Override
    protected JComponent createSouthPanel() {
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton entireFileButton = new JButton("Entire File");
        JButton selectedCodeButton = new JButton("Selected Code");

        entireFileButton.addActionListener(e -> {
            selectedIndex = -1;
            close(DialogWrapper.OK_EXIT_CODE);
        });

        selectedCodeButton.addActionListener(e -> {
            isSelectedCode = true;
            close(DialogWrapper.OK_EXIT_CODE);
        });

        southPanel.add(entireFileButton);
        southPanel.add(selectedCodeButton);

        JPanel defaultButtons = (JPanel) super.createSouthPanel();
        for (Component button : defaultButtons.getComponents()) {
            southPanel.add(button);
        }

        return southPanel;
    }

    @Override
    protected void doOKAction() {
        selectedIndex = methodsList.getSelectedIndex();
        super.doOKAction();
    }


    /**
     * Returns the name of the method selected to optimise.
     *
     * @return the name of the method
     */
    public String getSelectedMethod() {
        return selectedIndex == -1 ? null : methods.get(selectedIndex);
    }


    /**
     * Verifies if the user selected a portion of the code with the cursor.
     *
     * @return true if a portion of code was highlighted, false otherwise
     */
    public boolean isSelectedCode(){
        return isSelectedCode;
    }
}
