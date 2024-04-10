package emk4.GUI;

import javax.swing.*;
import java.awt.*;

public class View extends JFrame {

    private final JButton button;
    private final JTextArea textArea;
    private final JTextArea resultArea;

    public View() throws HeadlessException {
        JPanel panel = new JPanel();
        button = new JButton("Submit");
        textArea = new JTextArea("Hello");
        resultArea = new JTextArea("Result will appear here");
        resultArea.setEditable(false);

        panel.setLayout(new BorderLayout());
        panel.add(resultArea, BorderLayout.CENTER);
        panel.add(button, BorderLayout.SOUTH);
        panel.add(textArea, BorderLayout.NORTH);
        this.add(panel);

        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setSize(640, 480);
        this.setVisible(true);
    }

    public JButton getButton() {
        return button;
    }

    public JTextArea getTextArea() {
        return textArea;
    }

    public JTextArea getResultArea() {
        return resultArea;
    }
}
