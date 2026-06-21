package gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import password.PasswordAuditResult;
import password.PasswordAuditor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class AuditView extends BorderPane {

    private static final String DEFAULT_OUTPUT_FILE = "audit_report.txt";

    private final Stage ownerStage;
    private final PasswordAuditor auditor = new PasswordAuditor();

    private final TextField passwordField = new TextField();
    private final TextField inputFileField = new TextField();
    private final TextField outputFileField = new TextField(DEFAULT_OUTPUT_FILE);

    private final CheckBox rawOutputBox = new CheckBox("Raw output");
    private final CheckBox saveToFileBox = new CheckBox("Save to file");

    private final TextArea outputArea = new TextArea();
    private final Label statusLabel = new Label("Ready.");

    public AuditView(Stage ownerStage) {
        this.ownerStage = ownerStage;
        buildUi();
    }

    private void buildUi() {
        setPadding(new Insets(20));

        Label titleLabel = new Label("Password Audit");
        titleLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        Label subtitleLabel = new Label("Audit a single password or a file containing multiple passwords.");
        subtitleLabel.setStyle("-fx-font-size: 13px;");

        VBox headerBox = new VBox(6, titleLabel, subtitleLabel);
        headerBox.setPadding(new Insets(0, 0, 20, 0));

        passwordField.setPromptText("Enter a password to audit");

        Button auditPasswordButton = new Button("Audit password");
        auditPasswordButton.setOnAction(event -> handleSingleAudit());

        HBox passwordBox = new HBox(10, new Label("Password:"), passwordField, auditPasswordButton);
        passwordBox.setAlignment(Pos.CENTER_LEFT);

        inputFileField.setPromptText("Choose a password file");

        Button browseInputButton = new Button("Browse");
        browseInputButton.setOnAction(event -> chooseInputFile());

        Button auditFileButton = new Button("Audit file");
        auditFileButton.setOnAction(event -> handleFileAudit());

        HBox fileInputBox = new HBox(10, new Label("Input file:"), inputFileField, browseInputButton, auditFileButton);
        fileInputBox.setAlignment(Pos.CENTER_LEFT);

        Button browseOutputButton = new Button("Browse");
        browseOutputButton.setOnAction(event -> chooseOutputFile());

        HBox outputFileBox = new HBox(10, new Label("Output file:"), outputFileField, browseOutputButton);
        outputFileBox.setAlignment(Pos.CENTER_LEFT);

        outputFileField.disableProperty().bind(saveToFileBox.selectedProperty().not());
        browseOutputButton.disableProperty().bind(saveToFileBox.selectedProperty().not());

        Button clearButton = new Button("Clear");
        clearButton.setOnAction(event -> clearOutput());

        HBox actionBox = new HBox(10, rawOutputBox, saveToFileBox, clearButton);
        actionBox.setAlignment(Pos.CENTER_LEFT);

        VBox leftPanel = new VBox(
                16,
                new Label("Single password"),
                passwordBox,
                new Separator(),
                new Label("Batch audit"),
                fileInputBox,
                new Separator(),
                new Label("Output"),
                outputFileBox,
                actionBox
        );
        leftPanel.setPrefWidth(420);

        outputArea.setEditable(false);
        outputArea.setWrapText(true);
        outputArea.setPromptText("Audit results will appear here...");

        VBox centerPanel = new VBox(10, new Label("Result"), outputArea, statusLabel);
        VBox.setMargin(statusLabel, new Insets(4, 0, 0, 0));

        setTop(headerBox);
        setLeft(leftPanel);
        setCenter(centerPanel);

        BorderPane.setMargin(leftPanel, new Insets(0, 20, 0, 0));
    }

    private void handleSingleAudit() {
        try {
            String password = passwordField.getText();

            if (password == null || password.isBlank()) {
                throw new IllegalArgumentException("Please enter a password to audit.");
            }

            PasswordAuditResult result = auditor.audit(password);
            String content = rawOutputBox.isSelected()
                    ? formatRawSingle(password, result)
                    : formatPrettySingle(password, result);

            outputArea.setText(content);
            writeIfNeeded(content, "Single password audit completed.");

        } catch (IllegalArgumentException e) {
            showError("Audit error", e.getMessage());
        } catch (IOException e) {
            showError("File error", "Failed to write output file.");
        } catch (Exception e) {
            showError("Unexpected error", e.getMessage());
        }
    }

    private void handleFileAudit() {
        try {
            String fileName = inputFileField.getText().trim();

            if (fileName.isEmpty()) {
                throw new IllegalArgumentException("Please choose an input file.");
            }

            List<String> passwords = readPasswordsFromFile(fileName);
            List<PasswordAuditResult> results = new ArrayList<>();

            for (String password : passwords) {
                results.add(auditor.audit(password));
            }

            String content = rawOutputBox.isSelected()
                    ? formatRawBatch(passwords, results)
                    : formatPrettyBatch(fileName, passwords, results);

            outputArea.setText(content);
            writeIfNeeded(content, "Batch audit completed.");

        } catch (IllegalArgumentException e) {
            showError("Audit error", e.getMessage());
        } catch (IOException e) {
            showError("File error", "Failed to read or write file.");
        } catch (Exception e) {
            showError("Unexpected error", e.getMessage());
        }
    }

    private void writeIfNeeded(String content, String statusMessage) throws IOException {
        if (saveToFileBox.isSelected()) {
            String fileName = outputFileField.getText().trim().isEmpty()
                    ? DEFAULT_OUTPUT_FILE
                    : outputFileField.getText().trim();

            Files.writeString(Path.of(fileName), content);
            statusLabel.setText("Audit report saved to: " + fileName);
        } else {
            statusLabel.setText(statusMessage);
        }
    }

    private List<String> readPasswordsFromFile(String fileName) throws IOException {
        List<String> rawLines = Files.readAllLines(Path.of(fileName));
        List<String> passwords = new ArrayList<>();

        for (String line : rawLines) {
            String value = line.trim();
            if (!value.isEmpty()) {
                passwords.add(value);
            }
        }

        if (passwords.isEmpty()) {
            throw new IllegalArgumentException("The input file does not contain any password.");
        }

        return passwords;
    }

    private String formatRawSingle(String password, PasswordAuditResult result) {
        return password + " | score=" + result.getScore() + " | strength=" + result.getStrength() + System.lineSeparator();
    }

    private String formatRawBatch(List<String> passwords, List<PasswordAuditResult> results) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < passwords.size(); i++) {
            builder.append(passwords.get(i))
                    .append(" | score=")
                    .append(results.get(i).getScore())
                    .append(" | strength=")
                    .append(results.get(i).getStrength())
                    .append(System.lineSeparator());
        }

        return builder.toString();
    }

    private String formatPrettySingle(String password, PasswordAuditResult result) {
        StringBuilder builder = new StringBuilder();

        builder.append("Password audit result")
                .append(System.lineSeparator())
                .append(System.lineSeparator());

        builder.append("Password: ").append(password).append(System.lineSeparator());
        builder.append("Score: ").append(result.getScore()).append("/100").append(System.lineSeparator());
        builder.append("Strength: ").append(result.getStrength()).append(System.lineSeparator());
        builder.append(System.lineSeparator());

        builder.append("Checks:")
                .append(System.lineSeparator());
        builder.append("- Minimum length (12): ").append(toYesNo(result.hasMinimumLength())).append(System.lineSeparator());
        builder.append("- Lowercase: ").append(toYesNo(result.hasLowercase())).append(System.lineSeparator());
        builder.append("- Uppercase: ").append(toYesNo(result.hasUppercase())).append(System.lineSeparator());
        builder.append("- Digit: ").append(toYesNo(result.hasDigit())).append(System.lineSeparator());
        builder.append("- Special character: ").append(toYesNo(result.hasSpecial())).append(System.lineSeparator());
        builder.append("- Repeated pattern: ").append(toYesNo(result.hasRepeatedPattern())).append(System.lineSeparator());
        builder.append("- Sequential pattern: ").append(toYesNo(result.hasSequentialPattern())).append(System.lineSeparator());
        builder.append("- Keyboard pattern: ").append(toYesNo(result.hasKeyboardPattern())).append(System.lineSeparator());
        builder.append("- Common password: ").append(toYesNo(result.isCommonPassword())).append(System.lineSeparator());
        builder.append(System.lineSeparator());

        builder.append("Issues:")
                .append(System.lineSeparator());
        if (result.getIssues().isEmpty()) {
            builder.append("- None").append(System.lineSeparator());
        } else {
            for (String issue : result.getIssues()) {
                builder.append("- ").append(issue).append(System.lineSeparator());
            }
        }

        builder.append(System.lineSeparator());
        builder.append("Suggestions:")
                .append(System.lineSeparator());
        for (String suggestion : result.getSuggestions()) {
            builder.append("- ").append(suggestion).append(System.lineSeparator());
        }

        return builder.toString();
    }

    private String formatPrettyBatch(String fileName, List<String> passwords, List<PasswordAuditResult> results) {
        StringBuilder builder = new StringBuilder();

        int weakCount = 0;
        int mediumCount = 0;
        int strongCount = 0;

        for (PasswordAuditResult result : results) {
            switch (result.getStrength()) {
                case "Weak":
                    weakCount++;
                    break;
                case "Medium":
                    mediumCount++;
                    break;
                case "Strong":
                    strongCount++;
                    break;
                default:
                    break;
            }
        }

        builder.append("Batch audit result")
                .append(System.lineSeparator())
                .append(System.lineSeparator());

        builder.append("File: ").append(fileName).append(System.lineSeparator());
        builder.append("Passwords audited: ").append(passwords.size()).append(System.lineSeparator());
        builder.append("Weak: ").append(weakCount).append(System.lineSeparator());
        builder.append("Medium: ").append(mediumCount).append(System.lineSeparator());
        builder.append("Strong: ").append(strongCount).append(System.lineSeparator());
        builder.append(System.lineSeparator());

        for (int i = 0; i < passwords.size(); i++) {
            builder.append(i + 1)
                    .append(". ")
                    .append(passwords.get(i))
                    .append(" -> ")
                    .append(results.get(i).getStrength())
                    .append(" (")
                    .append(results.get(i).getScore())
                    .append("/100)")
                    .append(System.lineSeparator());
        }

        return builder.toString();
    }

    private void chooseInputFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose password file");
        File file = fileChooser.showOpenDialog(ownerStage);
        if (file != null) {
            inputFileField.setText(file.getAbsolutePath());
        }
    }

    private void chooseOutputFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose output file");
        fileChooser.setInitialFileName(outputFileField.getText().trim().isEmpty()
                ? DEFAULT_OUTPUT_FILE
                : outputFileField.getText().trim());

        File file = fileChooser.showSaveDialog(ownerStage);
        if (file != null) {
            outputFileField.setText(file.getAbsolutePath());
        }
    }

    private void clearOutput() {
        outputArea.clear();
        statusLabel.setText("Ready.");
    }

    private String toYesNo(boolean value) {
        return value ? "Yes" : "No";
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(ownerStage);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}