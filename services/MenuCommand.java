package carsharing.services;

public class MenuCommand {
    String menuOption;
    Command command;

    public MenuCommand(String menuOption, Command command) {
        this.menuOption = menuOption;
        this.command = command;
    }

    public String getMenuOption() {
        return menuOption;
    }

    public void setMenuOption(String menuOption) {
        this.menuOption = menuOption;
    }

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }
}
