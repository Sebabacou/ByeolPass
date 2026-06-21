import cli.HelpPrinter;
import command.Command;
import command.GenerateCommand;
import exception.UserInputException;

public class Main {
    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                throw new UserInputException("Missing command.");
            }

            String mode = args[0];
            Command command = null;

            switch (mode) {
                case "help":
                case "--help":
                case "-h":
                    HelpPrinter.printHelp();
                    return;

                case "generate":
                    command = new GenerateCommand();
                    break;

                case "audit":
                    System.out.println("WIP");
//                    command = new AuditCommand();
                    break;

                default:
                    throw new UserInputException("Unknown command: " + mode);
            }

            command.execute(args);

        } catch (UserInputException e) {
            System.out.println("Error: " + e.getMessage());
            System.out.println();
            HelpPrinter.printHelp();
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
        }
    }
}