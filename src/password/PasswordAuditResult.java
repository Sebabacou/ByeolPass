package password;

import java.util.ArrayList;
import java.util.List;

public class PasswordAuditResult {
    private int score;
    private String strength;
    private boolean hasMinimumLength;
    private boolean hasLowercase;
    private boolean hasUppercase;
    private boolean hasDigit;
    private boolean hasSpecial;
    private boolean hasRepeatedPattern;
    private boolean hasSequentialPattern;
    private boolean hasKeyboardPattern;
    private boolean isCommonPassword;

    private final List<String> issues = new ArrayList<>();
    private final List<String> suggestions = new ArrayList<>();

    public int getScore() {
        return score;
    }

    public String getStrength() {
        return strength;
    }

    public boolean hasMinimumLength() {
        return hasMinimumLength;
    }

    public boolean hasLowercase() {
        return hasLowercase;
    }

    public boolean hasUppercase() {
        return hasUppercase;
    }

    public boolean hasDigit() {
        return hasDigit;
    }

    public boolean hasSpecial() {
        return hasSpecial;
    }

    public boolean hasRepeatedPattern() {
        return hasRepeatedPattern;
    }

    public boolean hasSequentialPattern() {
        return hasSequentialPattern;
    }

    public boolean hasKeyboardPattern() {
        return hasKeyboardPattern;
    }

    public boolean isCommonPassword() {
        return isCommonPassword;
    }

    public List<String> getIssues() {
        return issues;
    }

    public List<String> getSuggestions() {
        return suggestions;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setStrength(String strength) {
        this.strength = strength;
    }

    public void setHasMinimumLength(boolean hasMinimumLength) {
        this.hasMinimumLength = hasMinimumLength;
    }

    public void setHasLowercase(boolean hasLowercase) {
        this.hasLowercase = hasLowercase;
    }

    public void setHasUppercase(boolean hasUppercase) {
        this.hasUppercase = hasUppercase;
    }

    public void setHasDigit(boolean hasDigit) {
        this.hasDigit = hasDigit;
    }

    public void setHasSpecial(boolean hasSpecial) {
        this.hasSpecial = hasSpecial;
    }

    public void setHasRepeatedPattern(boolean hasRepeatedPattern) {
        this.hasRepeatedPattern = hasRepeatedPattern;
    }

    public void setHasSequentialPattern(boolean hasSequentialPattern) {
        this.hasSequentialPattern = hasSequentialPattern;
    }

    public void setHasKeyboardPattern(boolean hasKeyboardPattern) {
        this.hasKeyboardPattern = hasKeyboardPattern;
    }

    public void setCommonPassword(boolean commonPassword) {
        isCommonPassword = commonPassword;
    }

    public void addIssue(String issue) {
        issues.add(issue);
    }

    public void addSuggestion(String suggestion) {
        suggestions.add(suggestion);
    }
}