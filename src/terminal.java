import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class terminal extends JFrame {

    private JTextArea outputArea;
    private JTextField inputField;

    private String username = "User";
    private File configFolder;
    private File configFile;

    public terminal() {
        setTitle("J9 Terminal");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // UI Colors
        Color bg = Color.black;
        Color fg = Color.white;
        Font font = new Font("Consolas", Font.PLAIN, 16);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setBackground(bg);
        outputArea.setForeground(fg);
        outputArea.setFont(font);

        JScrollPane scroll = new JScrollPane(outputArea);

        inputField = new JTextField();
        inputField.setBackground(bg);
        inputField.setForeground(fg);
        inputField.setCaretColor(fg);
        inputField.setFont(font);

        // Load username or ask
        loadOrCreateUser();

        // Enter key = process command
        inputField.addActionListener(e -> {
            String cmd = inputField.getText().trim();
            inputField.setText("");
            processCommand(cmd);
        });

        add(scroll, BorderLayout.CENTER);
        add(inputField, BorderLayout.SOUTH);

        // Intro
        print("J9 Terminal");
        print("----------------------");
        print("Welcome back, " + username + "!");
        print("");
        printPrompt();
    }

    private void print(String msg) {
        outputArea.append(msg + "\n");
        outputArea.setCaretPosition(outputArea.getDocument().getLength());
    }

    private void printPrompt() {
        print(username + "@j9:~$ ");
    }

    private void loadOrCreateUser() {
        String appdata = System.getenv("APPDATA");
        configFolder = new File(appdata + "\\J9 Terminal");
        configFile = new File(configFolder, "config.txt");

        try {
            if (!configFolder.exists()) {
                configFolder.mkdirs();
            }

            if (!configFile.exists()) {
                // Ask for username
                username = JOptionPane.showInputDialog(null,
                        "Enter your username:",
                        "J9 Terminal Setup",
                        JOptionPane.QUESTION_MESSAGE);

                if (username == null || username.isBlank()) {
                    username = "User";
                }

                FileWriter fw = new FileWriter(configFile);
                fw.write(username);
                fw.close();
            } else {
                BufferedReader br = new BufferedReader(new FileReader(configFile));
                username = br.readLine();
                br.close();
                if (username == null || username.isBlank()) username = "User";
            }

        } catch (Exception e) {
            e.printStackTrace();
            username = "User";
        }
    }

    private void processCommand(String cmd) {

        print(username + "@j9:~$ " + cmd);

        switch (cmd.toLowerCase()) {
            case "help":
                print("J9 Terminal Commands:");
                print("  help      - Show this help");
                print("  clear     - Clear the screen");
                print("  exit      - Close J9 Terminal");
                break;

            case "clear":
                outputArea.setText("");
                break;

            case "exit":
                System.exit(0);
                break;

            case "":
                break;

            default:
                print("Unknown command: " + cmd);
                break;
        }

        printPrompt();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new terminal().setVisible(true));
    }
}
