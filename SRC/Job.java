
import java.util.List;
import java.util.ArrayList;

public abstract class Job implements Checkable {

    private final String name;
    private final String chain;
    private final String owner;
    private final String machine;
    private final String description;
    private final List<String> dependencies;

    public Job(String name, String chain, String owner,
               String machine, String description,
               List<String> dependencies) {

        this.name = name;
        this.chain = chain;
        this.owner = owner;
        this.machine = machine;
        this.description = description;
        this.dependencies = new ArrayList<>(dependencies);
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getChain() {
        return chain;
    }

    public String getOwner() {
        return owner;
    }

    public String getMachine() {
        return machine;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getDependencies() {
        return new ArrayList<>(dependencies);
    }

    public abstract String getType();

    public abstract String getTypeSpecificProblem();

    public void printDescription() {
        System.out.println("Job: " + name);
        System.out.println("Type: " + getType());
        System.out.println("Chain: " + chain);
        System.out.println("Owner: " + owner);
        System.out.println("Machine: " + machine);
        System.out.println("Description: " + description);
    }

    @Override
    public boolean isValid() {
        return getTypeSpecificProblem() == null;
    }

    @Override
    public String getValidationProblem() {
        return getTypeSpecificProblem();
    }
}
