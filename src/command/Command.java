package command;

import exception.UserInputException;

public interface Command {
    void execute(String[] args) throws UserInputException;
}