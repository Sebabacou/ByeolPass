package command;

import exception.UserInputException;
import password.PasswordGenerator;
import password.PasswordPolicy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class GenerateCommand implements Command {

    private static final int DEFAULT_LENGTH = 12;
    private static final String DEFAULT_OUTPUT_FILE = "password.txt";

    @Override
    public void execute(String[] args) throws UserInputException {
        PasswordGenerator generator = new PasswordGenerator();

        int length = extractLengthOption(args);
        int number = extractNumberOption(args);
        boolean rawOutput = hasRawOption(args);
        String outputFile = extractOutputFileOption(args);

        PasswordPolicy policy = buildPolicy(length, args, 1);
        List<String> passwords = generator.generateMany(number, policy);

        String content = rawOutput
                ? formatRawOutput(passwords)
                : formatPrettyOutput(passwords);

        if (outputFile != null) {
            writeToFile(outputFile, content);

            if (!rawOutput) {
                System.out.println("Passwords saved to: " + outputFile);
            }
            return;
        }

        System.out.print(content);
    }

    private int extractLengthOption(String[] args) throws UserInputException {
        int length = DEFAULT_LENGTH;
        boolean found = false;

        for (int i = 1; i < args.length; i++) {
            if (args[i].startsWith("--len:")) {
                if (found) {
                    throw new UserInputException("Duplicate --len option.");
                }

                String value = args[i].substring("--len:".length());

                if (value.isEmpty()) {
                    throw new UserInputException("Missing value for --len.");
                }

                try {
                    length = Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    throw new UserInputException("Invalid value for --len: " + value);
                }

                if (length <= 0) {
                    throw new UserInputException("Length must be greater than 0.");
                }

                found = true;
            }
        }

        return length;
    }

    private int extractNumberOption(String[] args) throws UserInputException {
        int number = 1;
        boolean found = false;

        for (int i = 1; i < args.length; i++) {
            if (args[i].startsWith("--number:")) {
                if (found) {
                    throw new UserInputException("Duplicate --number option.");
                }

                String value = args[i].substring("--number:".length());

                if (value.isEmpty()) {
                    throw new UserInputException("Missing value for --number.");
                }

                try {
                    number = Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    throw new UserInputException("Invalid value for --number: " + value);
                }

                if (number <= 0) {
                    throw new UserInputException("Number must be greater than 0.");
                }

                found = true;
            }
        }

        return number;
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

    private PasswordPolicy buildPolicy(int length, String[] args, int startIndex) throws UserInputException {
        PasswordPolicy policy = new PasswordPolicy(length);

        boolean lettersOnly = false;
        boolean digitsOnly = false;

        for (int i = startIndex; i < args.length; i++) {
            switch (args[i]) {
                case "--letters-only":
                    lettersOnly = true;
                    break;

                case "--digits-only":
                    digitsOnly = true;
                    break;

                case "--no-special":
                    policy.setUseSpecials(false);
                    break;

                case "--no-ambiguous":
                    policy.setExcludeAmbiguous(true);
                    break;

                case "--no-uppercase":
                    policy.setUseUppercase(false);
                    break;

                case "--no-lowercase":
                    policy.setUseLowercase(false);
                    break;

                case "--raw":
                case "--output":
                    break;

                default:
                    if (!args[i].startsWith("--number:")
                            && !args[i].startsWith("--len:")
                            && !args[i].startsWith("--output:")) {
                        throw new UserInputException("Unknown option: " + args[i]);
                    }
                    
                    break;
            }
        }

        if (lettersOnly && digitsOnly) {
            throw new UserInputException("Cannot combine --letters-only and --digits-only.");
        }

        if (lettersOnly) {
            policy.setLettersOnly();
        }

        if (digitsOnly) {
            policy.setDigitsOnly();
        }

        for (int i = startIndex; i < args.length; i++) {
            switch (args[i]) {
                case "--no-uppercase":
                    policy.setUseUppercase(false);
                    break;

                case "--no-lowercase":
                    policy.setUseLowercase(false);
                    break;

                case "--no-special":
                    policy.setUseSpecials(false);
                    break;

                default:
                    break;
            }
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
            builder.append("Generated password:").append(System.lineSeparator());
            builder.append(passwords.get(0)).append(System.lineSeparator());
            return builder.toString();
        }

        builder.append("Generated passwords:").append(System.lineSeparator());
        for (int i = 0; i < passwords.size(); i++) {
            builder.append(i + 1)
                    .append(". ")
                    .append(passwords.get(i))
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
}