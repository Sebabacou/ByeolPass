package password;

public class PasswordPolicy {
    private int length;
    private boolean useLowercase;
    private boolean useUppercase;
    private boolean useDigits;
    private boolean useSpecials;
    private boolean excludeAmbiguous;

    public PasswordPolicy(int length) {
        this.length = length;
        this.useLowercase = true;
        this.useUppercase = true;
        this.useDigits = true;
        this.useSpecials = true;
        this.excludeAmbiguous = false;
    }

    public int getLength() {
        return length;
    }

    public boolean useLowercase() {
        return useLowercase;
    }

    public boolean useUppercase() {
        return useUppercase;
    }

    public boolean useDigits() {
        return useDigits;
    }

    public boolean useSpecials() {
        return useSpecials;
    }

    public boolean excludeAmbiguous() {
        return excludeAmbiguous;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setUseLowercase(boolean useLowercase) {
        this.useLowercase = useLowercase;
    }

    public void setUseUppercase(boolean useUppercase) {
        this.useUppercase = useUppercase;
    }

    public void setUseDigits(boolean useDigits) {
        this.useDigits = useDigits;
    }

    public void setUseSpecials(boolean useSpecials) {
        this.useSpecials = useSpecials;
    }

    public void setExcludeAmbiguous(boolean excludeAmbiguous) {
        this.excludeAmbiguous = excludeAmbiguous;
    }

    public void setLettersOnly() {
        this.useLowercase = true;
        this.useUppercase = true;
        this.useDigits = false;
        this.useSpecials = false;
    }

    public void setDigitsOnly() {
        this.useLowercase = false;
        this.useUppercase = false;
        this.useDigits = true;
        this.useSpecials = false;
    }
}