package cli;

public class HelpPrinter {
    public static void printHelp() {
        System.out.println("Password Tool - Help");
        System.out.println();
        System.out.println("Usage:");
        System.out.println("  java Main help");
        System.out.println("  java Main generate [options]");
        System.out.println("  java Main audit <password> [options]");
        System.out.println("  java Main audit --password:X [options]");
        System.out.println("  java Main audit --file:X [options]");
        System.out.println();

        System.out.println("Generate options:");
        System.out.println("  --len:X            Password length (default: 12)");
        System.out.println("  --number:X         Generate X passwords");
        System.out.println("  --no-special       Exclude special characters");
        System.out.println("  --no-ambiguous     Exclude ambiguous characters (O, 0, I, l, 1)");
        System.out.println("  --letters-only     Use letters only");
        System.out.println("  --digits-only      Use digits only");
        System.out.println("  --no-uppercase     Exclude uppercase letters");
        System.out.println("  --no-lowercase     Exclude lowercase letters");
        System.out.println("  --raw              Print passwords only, one per line");
        System.out.println("  --output           Write output to password.txt");
        System.out.println("  --output:X         Write output to file X");
        System.out.println();

        System.out.println("Audit options:");
        System.out.println("  --password:X       Audit a single password");
        System.out.println("  --file:X           Audit passwords from a file");
        System.out.println("  --raw              Print audit result in compact mode");
        System.out.println("  --output           Write output to audit_report.txt");
        System.out.println("  --output:X         Write output to file X");
        System.out.println();

        System.out.println("Examples:");
        System.out.println("  java Main generate");
        System.out.println("  java Main generate --len:16");
        System.out.println("  java Main generate --number:5 --raw");
        System.out.println("  java Main generate --output:my_passwords.txt");
        System.out.println("  java Main audit Password123!");
        System.out.println("  java Main audit --password:Password123!");
        System.out.println("  java Main audit --file:passwords.txt");
        System.out.println("  java Main audit --file:passwords.txt --output:report.txt");
    }
}