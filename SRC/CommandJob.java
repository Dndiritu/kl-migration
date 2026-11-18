
public class CommandJob extends Job {

    private final String command;

    public CommandJob(String name, String chain, String owner,
                      String machine, String description,
                      String[] dependencies, String command) {

        super(name, chain, owner, machine, description, dependencies);
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    @Override
    public String getType() {
        return "CMD";
    }

    @Override
    public String getTypeSpecificProblem() {
        if (command == null || command.isBlank()) {
            return "Command job has no command";
        }
        return null;
    }
}
