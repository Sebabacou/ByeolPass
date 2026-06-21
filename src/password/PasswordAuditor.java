package password;

import java.util.HashSet;
import java.util.Set;

public class PasswordAuditor {

    private static final Set<String> COMMON_PASSWORDS = new HashSet<>();

    static {
        COMMON_PASSWORDS.add("123456");
        COMMON_PASSWORDS.add("123456789");
        COMMON_PASSWORDS.add("password");
        COMMON_PASSWORDS.add("password123");
        COMMON_PASSWORDS.add("qwerty");
        COMMON_PASSWORDS.add("azerty");
        COMMON_PASSWORDS.add("admin");
        COMMON_PASSWORDS.add("admin123");
        COMMON_PASSWORDS.add("welcome");
        COMMON_PASSWORDS.add("letmein");
        COMMON_PASSWORDS.add("000000");
        COMMON_PASSWORDS.add("111111");
        COMMON_PASSWORDS.add("abc123");
    }

    public PasswordAuditResult audit(String password) {
        if (password == null) {
            throw new IllegalArgumentException("Password cannot be null.");
        }

        PasswordAuditResult result = new PasswordAuditResult();

        boolean hasMinimumLength = password.length() >= 12;
        boolean hasLowercase = containsLowercase(password);
        boolean hasUppercase = containsUppercase(password);
        boolean hasDigit = containsDigit(password);
        boolean hasSpecial = containsSpecial(password);
        boolean hasRepeatedPattern = hasRepeatedPattern(password);
        boolean hasSequentialPattern = hasSequentialPattern(password);
        boolean hasKeyboardPattern = hasKeyboardPattern(password);
        boolean isCommonPassword = COMMON_PASSWORDS.contains(password.toLowerCase());

        result.setHasMinimumLength(hasMinimumLength);
        result.setHasLowercase(hasLowercase);
        result.setHasUppercase(hasUppercase);
        result.setHasDigit(hasDigit);
        result.setHasSpecial(hasSpecial);
        result.setHasRepeatedPattern(hasRepeatedPattern);
        result.setHasSequentialPattern(hasSequentialPattern);
        result.setHasKeyboardPattern(hasKeyboardPattern);
        result.setCommonPassword(isCommonPassword);

        int score = computeScore(
                password,
                hasMinimumLength,
                hasLowercase,
                hasUppercase,
                hasDigit,
                hasSpecial,
                hasRepeatedPattern,
                hasSequentialPattern,
                hasKeyboardPattern,
                isCommonPassword
        );

        result.setScore(score);
        result.setStrength(computeStrength(score));

        fillIssues(result);
        fillSuggestions(result);

        return result;
    }

    private int computeScore(
            String password,
            boolean hasMinimumLength,
            boolean hasLowercase,
            boolean hasUppercase,
            boolean hasDigit,
            boolean hasSpecial,
            boolean hasRepeatedPattern,
            boolean hasSequentialPattern,
            boolean hasKeyboardPattern,
            boolean isCommonPassword
    ) {
        int score = 0;

        if (password.length() >= 8) {
            score += 10;
        }

        if (hasMinimumLength) {
            score += 20;
        }

        if (password.length() >= 16) {
            score += 10;
        }

        if (hasLowercase) score += 10;
        if (hasUppercase) score += 10;
        if (hasDigit) score += 10;
        if (hasSpecial) score += 15;

        int typeCount = 0;
        if (hasLowercase) typeCount++;
        if (hasUppercase) typeCount++;
        if (hasDigit) typeCount++;
        if (hasSpecial) typeCount++;

        if (typeCount == 4) {
            score += 10;
        } else if (typeCount == 3) {
            score += 5;
        }

        if (hasRepeatedPattern) score -= 10;
        if (hasSequentialPattern) score -= 10;
        if (hasKeyboardPattern) score -= 10;
        if (isCommonPassword) score -= 35;

        if (score < 0) {
            score = 0;
        }

        if (score > 100) {
            score = 100;
        }

        return score;
    }

    private String computeStrength(int score) {
        if (score >= 80) {
            return "Strong";
        }
        if (score >= 50) {
            return "Medium";
        }
        return "Weak";
    }

    private void fillIssues(PasswordAuditResult result) {
        if (!result.hasMinimumLength()) {
            result.addIssue("Password is shorter than 12 characters.");
        }
        if (!result.hasLowercase()) {
            result.addIssue("Password does not contain lowercase letters.");
        }
        if (!result.hasUppercase()) {
            result.addIssue("Password does not contain uppercase letters.");
        }
        if (!result.hasDigit()) {
            result.addIssue("Password does not contain digits.");
        }
        if (!result.hasSpecial()) {
            result.addIssue("Password does not contain special characters.");
        }
        if (result.hasRepeatedPattern()) {
            result.addIssue("Password contains repeated characters.");
        }
        if (result.hasSequentialPattern()) {
            result.addIssue("Password contains sequential patterns.");
        }
        if (result.hasKeyboardPattern()) {
            result.addIssue("Password contains keyboard-like patterns.");
        }
        if (result.isCommonPassword()) {
            result.addIssue("Password matches a common weak password.");
        }
    }

    private void fillSuggestions(PasswordAuditResult result) {
        if (!result.hasMinimumLength()) {
            result.addSuggestion("Use at least 12 characters.");
        }
        if (!result.hasLowercase()) {
            result.addSuggestion("Add lowercase letters.");
        }
        if (!result.hasUppercase()) {
            result.addSuggestion("Add uppercase letters.");
        }
        if (!result.hasDigit()) {
            result.addSuggestion("Add digits.");
        }
        if (!result.hasSpecial()) {
            result.addSuggestion("Add special characters.");
        }
        if (result.hasRepeatedPattern()) {
            result.addSuggestion("Avoid repeated characters like aaa or 111.");
        }
        if (result.hasSequentialPattern()) {
            result.addSuggestion("Avoid sequences like 1234 or abcd.");
        }
        if (result.hasKeyboardPattern()) {
            result.addSuggestion("Avoid predictable keyboard patterns like qwerty or azerty.");
        }
        if (result.isCommonPassword()) {
            result.addSuggestion("Use a less common and more unique password.");
        }

        if (result.getSuggestions().isEmpty()) {
            result.addSuggestion("No major weakness detected.");
        }
    }

    private boolean containsLowercase(String password) {
        for (int i = 0; i < password.length(); i++) {
            if (Character.isLowerCase(password.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    private boolean containsUppercase(String password) {
        for (int i = 0; i < password.length(); i++) {
            if (Character.isUpperCase(password.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    private boolean containsDigit(String password) {
        for (int i = 0; i < password.length(); i++) {
            if (Character.isDigit(password.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    private boolean containsSpecial(String password) {
        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);
            if (!Character.isLetterOrDigit(c)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasRepeatedPattern(String password) {
        for (int i = 0; i < password.length() - 2; i++) {
            char a = password.charAt(i);
            char b = password.charAt(i + 1);
            char c = password.charAt(i + 2);

            if (a == b && b == c) {
                return true;
            }
        }
        return false;
    }

    private boolean hasSequentialPattern(String password) {
        String value = password.toLowerCase();

        for (int i = 0; i < value.length() - 2; i++) {
            char a = value.charAt(i);
            char b = value.charAt(i + 1);
            char c = value.charAt(i + 2);

            if ((b == a + 1 && c == b + 1) || (b == a - 1 && c == b - 1)) {
                if (Character.isLetterOrDigit(a) && Character.isLetterOrDigit(b) && Character.isLetterOrDigit(c)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean hasKeyboardPattern(String password) {
        String value = password.toLowerCase();

        return value.contains("qwerty")
                || value.contains("azerty")
                || value.contains("asdf")
                || value.contains("1234")
                || value.contains("0000")
                || value.contains("password");
    }
}