package gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import password.PasswordGenerator;
import password.PasswordPolicy;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class GenerateView extends BorderPane {

    private static final String DEFAULT_OUTPUT_FILE = "password.txt";

    private final Stage ownerStage;
    private final PasswordGenerator generator = new PasswordGenerator();

    private final Spinner<Integer> lengthSpinner = new Spinner<>(1, 512, 12);
    private final Spinner<Integer> numberSpinner = new Spinner<>(1, 100, 1);

    private final CheckBox noSpecialBox = new CheckBox("Exclude special characters");
    private final CheckBox noAmbiguousBox = new CheckBox("Exclude ambiguous characters (O, 0, I, l, 1)");
    private final CheckBox lettersOnlyBox = new CheckBox("Use letters only");
    private final CheckBox digitsOnlyBox = new CheckBox("Use digits only");
    private final CheckBox noUppercaseBox = new CheckBox("Exclude uppercase letters");
    private final CheckBox noLowercaseBox = new CheckBox("Exclude lowercase letters");
    private final CheckBox rawOutputBox = new CheckBox("Raw output");
    private final CheckBox saveToFileBox = new CheckBox("Save to file");

    private final TextField outputFileField = new TextField(DEFAULT_OUTPUT_FILE);
    private final TextArea outputArea = new TextArea();
    private final Label statusLabel = new Label("Ready.");

    public GenerateView(Stage ownerStage) {
        this.ownerStage = ownerStage;
        buildUi();
    }

    private void buildUi() {
        setPadding(new Insets(20));

        Label titleLabel = new Label("Password Generation");
        titleLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        Label subtitleLabel = new Label("Generate one or multiple passwords with custom rules.");
        subtitleLabel.setStyle("-fx-font-size: 13px;");

        VBox headerBox = new VBox(6, titleLabel, subtitleLabel);
        headerBox.setPadding(new Insets(0, 0, 20, 0));

        GridPane configGrid = new GridPane();
        configGrid.setHgap(12);
        configGrid.setVgap(12);

        lengthSpinner.setEditable(true);
        numberSpinner.setEditable(true);

        configGrid.add(new Label("Password length:"), 0, 0);
        configGrid.add(lengthSpinner, 1, 0);

        configGrid.add(new Label("Number of passwords:"), 0, 1);
        configGrid.add(numberSpinner, 1, 1);

        VBox optionsBox = new VBox(
                8,
                noSpecialBox,
                noAmbiguousBox,
                lettersOnlyBox,
                digitsOnlyBox,
                noUppercaseBox,
                noLowercaseBox,
                rawOutputBox,
                saveToFileBox
        );

        outputFileField.setPrefWidth(260);

        Button browseButton = new Button("Browse");
        browseButton.setOnAction(event -> chooseOutputFile());

        HBox fileBox = new HBox(10, new Label("Output file:"), outputFileField, browseButton);
        fileBox.setAlignment(Pos.CENTER_LEFT);

        outputFileField.disableProperty().bind(saveToFileBox.selectedProperty().not());
        browseButton.disableProperty().bind(saveToFileBox.selectedProperty().not());

        Button generateButton = new Button("Generate");
        generateButton.setDefaultButton(true);
        generateButton.setOnAction(event -> handleGenerate());

        Button clearButton = new Button("Clear");
        clearButton.setOnAction(event -> clearOutput());

        HBox buttonBox = new HBox(10, generateButton, clearButton);
        buttonBox.setAlignment(Pos.CENTER_LEFT);

        VBox leftPanel = new VBox(
                16,
                new Label("Configuration"),
                configGrid,
                new Separator(),
                new Label("Options"),
                optionsBox,
                fileBox,
                buttonBox
        );
        leftPanel.setPrefWidth(380);

        outputArea.setEditable(false);
        outputArea.setWrapText(false);
        outputArea.setPromptText("Generated passwords will appear here...");

        VBox centerPanel = new VBox(10, new Label("Output"), outputArea, statusLabel);
        VBox.setMargin(statusLabel, new Insets(4, 0, 0, 0));

        setTop(headerBox);
        setLeft(leftPanel);
        setCenter(centerPanel);

        BorderPane.setMargin(leftPanel, new Insets(0, 20, 0, 0));
    }

    private void handleGenerate() {
        try {
            int length = lengthSpinner.getValue();
            int number = numberSpinner.getValue();

            PasswordPolicy policy = buildPolicy(length);
            List<String> passwords = generator.generateMany(number, policy);

            String content = rawOutputBox.isSelected()
                    ? formatRawOutput(passwords)
                    : formatPrettyOutput(passwords);

            outputArea.setText(content);

            if (saveToFileBox.isSelected()) {
                String fileName = outputFileField.getText().trim().isEmpty()
                        ? DEFAULT_OUTPUT_FILE
                        : outputFileField.getText().trim();

                writeToFile(fileName, content);
                statusLabel.setText("Passwords saved to: " + fileName);
            } else {
                statusLabel.setText("Generated " + passwords.size() + " password(s).");
            }

        } catch (IllegalArgumentException e) {
            showError("Generation error", e.getMessage());
        } catch (IOException e) {
            showError("File error", "Failed to write output file.");
        } catch (Exception e) {
            showError("Unexpected error", e.getMessage());
        }
    }

    private PasswordPolicy buildPolicy(int length) {
        if (lettersOnlyBox.isSelected() && digitsOnlyBox.isSelected()) {
            throw new IllegalArgumentException("Cannot combine 'letters only' and 'digits only'.");
        }

        PasswordPolicy policy = new PasswordPolicy(length);

        if (lettersOnlyBox.isSelected()) {
            policy.setLettersOnly();
        }

        if (digitsOnlyBox.isSelected()) {
            policy.setDigitsOnly();
        }

        if (noSpecialBox.isSelected()) {
            policy.setUseSpecials(false);
        }

        if (noAmbiguousBox.isSelected()) {
            policy.setExcludeAmbiguous(true);
        }

        if (noUppercaseBox.isSelected()) {
            policy.setUseUppercase(false);
        }

        if (noLowercaseBox.isSelected()) {
            policy.setUseLowercase(false);
        }

        return policy;
    }

    private String formatRawOutput(List<String> passwords) {
        StringBuilder builder = new StringBuilder();

        for (String password : passwords) {
            builder.append(password).append(System.lineSeparator());
        }

        return builder.toString();
    }

    private String formatPrettyOutput(List<String> passwords) {
        StringBuilder builder = new StringBuilder();

        if (passwords.size() == 1) {
            builder.append("Generated password:")
                    .append(System.lineSeparator())
                    .append(passwords.get(0))
                    .append(System.lineSeparator());
            return builder.toString();
        }

        builder.append("Generated passwords:")
                .append(System.lineSeparator());

        for (int i = 0; i < passwords.size(); i++) {
            builder.append(i + 1)
                    .append(". ")
                    .append(passwords.get(i))
                    .append(System.lineSeparator());
        }

        return builder.toString();
    }

    private void writeToFile(String fileName, String content) throws IOException {
        Files.writeString(Path.of(fileName), content);
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

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(ownerStage);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}