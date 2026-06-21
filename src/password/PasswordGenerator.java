package password;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PasswordGenerator {
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final String SPECIALS = "!@#$%^&*()-_=+<>?";
    private static final String AMBIGUOUS = "O0Il1";

    private final SecureRandom random = new SecureRandom();

    public String generate(PasswordPolicy policy) {
        validatePolicy(policy);

        String lowercase = policy.excludeAmbiguous() ? removeAmbiguous(LOWERCASE) : LOWERCASE;
        String uppercase = policy.excludeAmbiguous() ? removeAmbiguous(UPPERCASE) : UPPERCASE;
        String digits = policy.excludeAmbiguous() ? removeAmbiguous(DIGITS) : DIGITS;
        String specials = SPECIALS;

        List<Character> passwordChars = new ArrayList<>();
        StringBuilder allChars = new StringBuilder();

        if (policy.useLowercase()) {
            passwordChars.add(randomChar(lowercase));
            allChars.append(lowercase);
        }

        if (policy.useUppercase()) {
            passwordChars.add(randomChar(uppercase));
            allChars.append(uppercase);
        }

        if (policy.useDigits()) {
            passwordChars.add(randomChar(digits));
            allChars.append(digits);
        }

        if (policy.useSpecials()) {
            passwordChars.add(randomChar(specials));
            allChars.append(specials);
        }

        while (passwordChars.size() < policy.getLength()) {
            passwordChars.add(randomChar(allChars.toString()));
        }

        Collections.shuffle(passwordChars, random);

        StringBuilder password = new StringBuilder();
        for (char c : passwordChars) {
            password.append(c);
        }

        return password.toString();
    }

    public List<String> generateMany(int count, PasswordPolicy policy) {
        if (count <= 0) {
            throw new IllegalArgumentException("Count must be greater than 0.");
        }

        List<String> passwords = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            passwords.add(generate(policy));
        }

        return passwords;
    }

    private void validatePolicy(PasswordPolicy policy) {
        int enabledGroups = 0;

        if (policy.useLowercase()) enabledGroups++;
        if (policy.useUppercase()) enabledGroups++;
        if (policy.useDigits()) enabledGroups++;
        if (policy.useSpecials()) enabledGroups++;

        if (enabledGroups == 0) {
            throw new IllegalArgumentException("No character type is enabled.");
        }

        if (policy.getLength() < enabledGroups) {
            throw new IllegalArgumentException(
                    "Length must be at least " + enabledGroups + " to satisfy the policy."
            );
        }
    }

    private char randomChar(String chars) {
        return chars.charAt(random.nextInt(chars.length()));
    }

    private String removeAmbiguous(String chars) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < chars.length(); i++) {
            char c = chars.charAt(i);
            if (AMBIGUOUS.indexOf(c) == -1) {
                result.append(c);
            }
        }

        return result.toString();
    }
}