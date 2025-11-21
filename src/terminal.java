import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class terminal extends JFrame {

    private JTextPane outputPane;
    private JTextField inputField;

    private SimpleAttributeSet outputStyle;
    private SimpleAttributeSet errorStyle;
    private SimpleAttributeSet inputStyle;

    public terminal() {
        setTitle("J9 Terminal");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // ---- Styles ----
        outputStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(outputStyle, Color.GREEN);
        StyleConstants.setFontFamily(outputStyle, "Consolas");
        StyleConstants.setFontSize(outputStyle, 14);

        errorStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(errorStyle, Color.RED);
        StyleConstants.setFontFamily(errorStyle, "Consolas");
        StyleConstants.setFontSize(errorStyle, 14);

        inputStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(inputStyle, Color.GREEN);
        StyleConstants.setFontFamily(inputStyle, "Consolas");
        StyleConstants.setFontSize(inputStyle, 14);

        // ---- Terminal Output Panel ----
        outputPane = new JTextPane();
        outputPane.setEditable(false);
        outputPane.setBackground(Color.BLACK);
        outputPane.setCaretColor(Color.GREEN);  // blinking cursor
        outputPane.setFont(new Font("Consolas", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(outputPane);

        // ---- Input Field ----
        inputField = new JTextField();
        inputField.setFont(new Font("Consolas", Font.PLAIN, 14));
        inputField.setBackground(Color.BLACK);
        inputField.setForeground(Color.GREEN);
        inputField.setCaretColor(Color.GREEN);   // cursor color

        // Box cursor (fake) by drawing a custom border
        inputField.setBorder(BorderFactory.createLineBorder(new Color(0, 255, 0), 1));

        // Handle Enter key
        inputField.addActionListener(e -> {
            String cmd = inputField.getText().trim();
            inputField.setText("");
            addText("J9> " + cmd + "\n", outputStyle);
            runCommand(cmd);
        });

        add(scrollPane, BorderLayout.CENTER);
        add(inputField, BorderLayout.SOUTH);

        addText("=== J9 Terminal (GUI CMD-Style) ===\n", outputStyle);
        addText("Type commands. Type 'exit' to quit.\n\n", outputStyle);
    }

    private void addText(String text, AttributeSet style) {
        try {
            StyledDocument doc = outputPane.getStyledDocument();
            doc.insertString(doc.getLength(), text, style);
            outputPane.setCaretPosition(doc.getLength());
        } catch (Exception ignored) {}
    }

    private void runCommand(String cmd) {

        if (cmd.equalsIgnoreCase("exit")) {
            addText("\nClosing terminal...\n", outputStyle);
            System.exit(0);
        }

        try {
            Process process = Runtime.getRuntime().exec(cmd);

            BufferedReader stdout = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            BufferedReader stderr = new BufferedReader(
                    new InputStreamReader(process.getErrorStream()));

            String line;

            while ((line = stdout.readLine()) != null) {
                addText(line + "\n", outputStyle);
            }

            while ((line = stderr.readLine()) != null) {
                addText("ERR: " + line + "\n", errorStyle);
            }

        } catch (IOException e) {
            addText("Error: " + e.getMessage() + "\n", errorStyle);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new terminal().setVisible(true);
        });
    }
}
