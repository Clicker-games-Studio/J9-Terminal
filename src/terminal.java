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

        loadOrCreateUser();

        inputField.addActionListener(e -> {
            String cmd = inputField.getText().trim();
            inputField.setText("");
            processCommand(cmd);
        });

        add(scroll, BorderLayout.CENTER);
        add(inputField, BorderLayout.SOUTH);

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
        outputArea.append(username + "@j9:~$ ");
        outputArea.setCaretPosition(outputArea.getDocument().getLength());
    }

    private void loadOrCreateUser() {
        String appdata = System.getenv("APPDATA");
        configFolder = new File(appdata + "\\J9 Terminal");
        configFile = new File(configFolder, "config.txt");

        try {
            if (!configFolder.exists()) configFolder.mkdirs();

            if (!configFile.exists()) {
                username = JOptionPane.showInputDialog(null,
                        "Enter your username:",
                        "J9 Terminal Setup",
                        JOptionPane.QUESTION_MESSAGE);

                if (username == null || username.trim().isEmpty()) {
                    username = "User";
                }

                FileWriter fw = new FileWriter(configFile);
                fw.write(username);
                fw.close();
            } else {
                BufferedReader br = new BufferedReader(new FileReader(configFile));
                username = br.readLine();
                br.close();

                if (username == null || username.trim().isEmpty()) {
                    username = "User";
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            username = "User";
        }
    }

    private void processCommand(String cmd) {

        // Display user input
        print(cmd);

        switch (cmd.toLowerCase()) {
            case "help":
                print("J9 Terminal Commands:");
                print("  help           - Show this help");
                print("  clear          - Clear the screen");
                print("  exit           - Close J9 Terminal");
                print("  j9 --version   - Show J9 Terminal version");
                print("  j9 restart     - Restart J9 Terminal");
                break;

            case "clear":
                outputArea.setText("");
                break;

            case "exit":
                System.exit(0);
                break;

            case "j9 --version":
                print("J9 Terminal version 1.0.0");
                break;

            case "j9 restart":
                restartTerminal();
                break;

            case "":
                break;

            default:
                print("Unknown command: " + cmd);
                break;
        }

        printPrompt();
    }

    private void restartTerminal() {
        try {
            // Get java binary and classpath
            String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
            File currentJar = new File(terminal.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI());

            // If running from JAR, restart it
            if(currentJar.getName().endsWith(".jar")) {
                ProcessBuilder builder = new ProcessBuilder(javaBin, "-jar", currentJar.getPath());
                builder.start();
            } else {
                // If running from IDE or class files, restart using class name
                String classPath = System.getProperty("java.class.path");
                ProcessBuilder builder = new ProcessBuilder(javaBin, "-cp", classPath, terminal.class.getName());
                builder.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new terminal().setVisible(true));
    }
}
