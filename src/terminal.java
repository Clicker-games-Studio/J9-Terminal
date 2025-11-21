import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class terminal extends JFrame {

    private JTextPane outputPane;
    private JTextField inputField;

    private SimpleAttributeSet normalStyle;
    private SimpleAttributeSet errorStyle;

    private String userName;

    public terminal() {

        loadUserName();  // Load or ask for username

        setTitle("J9 Terminal");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // ---- Styles ----
        normalStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(normalStyle, Color.WHITE);
        StyleConstants.setFontFamily(normalStyle, "Consolas");
        StyleConstants.setFontSize(normalStyle, 14);

        errorStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(errorStyle, new Color(255, 120, 120));
        StyleConstants.setFontFamily(errorStyle, "Consolas");
        StyleConstants.setFontSize(errorStyle, 14);

        // ---- Terminal Output Panel ----
        outputPane = new JTextPane();
        outputPane.setEditable(false);
        outputPane.setBackground(Color.BLACK);
        outputPane.setCaretColor(Color.WHITE);
        outputPane.setFont(new Font("Consolas", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(outputPane);

        // ---- Input Field ----
        inputField = new JTextField();
        inputField.setFont(new Font("Consolas", Font.PLAIN, 14));
        inputField.setBackground(Color.BLACK);
        inputField.setForeground(Color.WHITE);
        inputField.setCaretColor(Color.WHITE);

        inputField.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(60, 60, 60)));

        inputField.addActionListener(e -> {
            String cmd = inputField.getText().trim();
            inputField.setText("");
            addText("> " + cmd + "\n", normalStyle);
            runCommand(cmd);
        });

        add(scrollPane, BorderLayout.CENTER);
        add(inputField, BorderLayout.SOUTH);

        addText("J9 Terminal\n", normalStyle);
        addText("----------------------\n", normalStyle);
        addText("Welcome back, " + userName + "!\n\n", normalStyle);
    }

    // Load username from %appdata%\J9 Terminal
    private void loadUserName() {
        try {
            String appData = System.getenv("APPDATA");
            File dir = new File(appData + "\\J9 Terminal");
            if (!dir.exists()) dir.mkdirs();

            File file = new File(dir, "username.txt");

            if (file.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(file));
                userName = br.readLine();
                br.close();
            } else {
                userName = JOptionPane.showInputDialog(this, "Enter your name:", "J9 Terminal Setup", JOptionPane.PLAIN_MESSAGE);

                if (userName == null || userName.trim().isEmpty())
                    userName = "User";

                FileWriter fw = new FileWriter(file);
                fw.write(userName);
                fw.close();
            }

        } catch (Exception e) {
            userName = "User";
        }
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
            addText("\nExiting...\n", normalStyle);
            System.exit(0);
        }

        if (cmd.equalsIgnoreCase("clear") || cmd.equalsIgnoreCase("cls")) {
            outputPane.setText("");
            return;
        }

        try {
            Process process = Runtime.getRuntime().exec(cmd);

            BufferedReader stdout = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            BufferedReader stderr = new BufferedReader(
                    new InputStreamReader(process.getErrorStream()));

            String line;

            while ((line = stdout.readLine()) != null) {
                addText(line + "\n", normalStyle);
            }

            while ((line = stderr.readLine()) != null) {
                addText(line + "\n", errorStyle);
            }

        } catch (IOException e) {
            addText("Command not found: " + cmd + "\n", errorStyle);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new terminal().setVisible(true);
        });
    }
}
