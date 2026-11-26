
import java.util.List;

public class CommandJob extends Job {

    private final String command;

    // Existing constructor
    public CommandJob(String name, String chain, String owner,
                      String machine, String description,
                      List<String> dependencies, String command) {

        super(name, chain, owner, machine, description, dependencies);
        this.command = command;
    }

    // Extended constructor
    public CommandJob(String name, String chain, String owner,
                      String machine, String description,
                      List<JobDependency> dependencies,
                      List<JobParameter> parameters,
                      String command) {

        super(name, chain, owner, machine, description,
              dependencies, parameters);
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
