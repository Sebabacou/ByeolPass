package command;

import exception.UserInputException;
import password.PasswordAuditResult;
import password.PasswordAuditor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class AuditCommand implements Command {

    private static final String DEFAULT_OUTPUT_FILE = "audit_report.txt";

    @Override
    public void execute(String[] args) throws UserInputException {
        PasswordAuditor auditor = new PasswordAuditor();

        String password = extractPasswordTarget(args);
        String inputFile = extractFileTarget(args);
        boolean rawOutput = hasRawOption(args);
        String outputFile = extractOutputFileOption(args);

        if (password != null && inputFile != null) {
            throw new UserInputException("Use either a password or a file, not both.");
        }

        if (password == null && inputFile == null) {
            throw new UserInputException("Missing audit target. Use audit <password> or --file:X.");
        }

        String content;

        if (password != null) {
            PasswordAuditResult result = auditor.audit(password);
            content = rawOutput
                    ? formatRawSingle(password, result)
                    : formatPrettySingle(password, result);
        } else {
            List<String> passwords = readPasswordsFromFile(inputFile);
            List<PasswordAuditResult> results = new ArrayList<>();

            for (String currentPassword : passwords) {
                results.add(auditor.audit(currentPassword));
            }

            content = rawOutput
                    ? formatRawBatch(passwords, results)
                    : formatPrettyBatch(inputFile, passwords, results);
        }

        if (outputFile != null) {
            writeToFile(outputFile, content);

            if (!rawOutput) {
                System.out.println("Audit report saved to: " + outputFile);
            }
            return;
        }

        System.out.print(content);
    }

    private String extractPasswordTarget(String[] args) throws UserInputException {
        String password = null;

        for (int i = 1; i < args.length; i++) {
            if (args[i].startsWith("--password:")) {
                if (password != null) {
                    throw new UserInputException("Duplicate password target.");
                }

                String value = args[i].substring("--password:".length());
                if (value.isEmpty()) {
                    throw new UserInputException("Missing value for --password.");
                }

                password = value;
            } else if (!args[i].startsWith("--")) {
                if (password != null) {
                    throw new UserInputException("Duplicate password target.");
                }
                password = args[i];
            }
        }

        return password;
    }

    private String extractFileTarget(String[] args) throws UserInputException {
        String file = null;

        for (int i = 1; i < args.length; i++) {
            if (args[i].startsWith("--file:")) {
                if (file != null) {
                    throw new UserInputException("Duplicate --file option.");
                }

                String value = args[i].substring("--file:".length());
                if (value.isEmpty()) {
                    throw new UserInputException("Missing value for --file.");
                }

                file = value;
            }
        }

        return file;
    }

    private boolean hasRawOption(String[] args) throws UserInputException {
        boolean found = false;

        for (int i = 1; i < args.length; i++) {
            if (args[i].equals("--raw")) {
                if (found) {
                    throw new UserInputException("Duplicate --raw option.");
                }
                found = true;
            }
        }

        return found;
    }

    private String extractOutputFileOption(String[] args) throws UserInputException {
        String outputFile = null;
        boolean found = false;

        for (int i = 1; i < args.length; i++) {
            if (args[i].equals("--output")) {
                if (found) {
                    throw new UserInputException("Duplicate --output option.");
                }
                outputFile = DEFAULT_OUTPUT_FILE;
                found = true;
            } else if (args[i].startsWith("--output:")) {
                if (found) {
                    throw new UserInputException("Duplicate --output option.");
                }

                String value = args[i].substring("--output:".length());
                outputFile = value.isEmpty() ? DEFAULT_OUTPUT_FILE : value;
                found = true;
            }
        }

        return outputFile;
    }

    private List<String> readPasswordsFromFile(String fileName) throws UserInputException {
        try {
            List<String> rawLines = Files.readAllLines(Path.of(fileName));
            List<String> passwords = new ArrayList<>();

            for (String line : rawLines) {
                String value = line.trim();
                if (!value.isEmpty()) {
                    passwords.add(value);
                }
            }

            if (passwords.isEmpty()) {
                throw new UserInputException("The input file does not contain any password.");
            }

            return passwords;
        } catch (IOException e) {
            throw new UserInputException("Failed to read file: " + fileName);
        }
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

    private void writeToFile(String fileName, String content) throws UserInputException {
        try {
            Files.writeString(Path.of(fileName), content);
        } catch (IOException e) {
            throw new UserInputException("Failed to write file: " + fileName);
        }
    }

    private String toYesNo(boolean value) {
        return value ? "Yes" : "No";
    }
}