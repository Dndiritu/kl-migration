
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Job implements Checkable {

    private final String name;
    private final String chain;
    private final String owner;
    private final String machine;
    private final String description;

    private final List<JobDependency> dependencyDetails;
    private final List<JobParameter> parameters;

    // Backward-compatible constructor
    public Job(String name, String chain, String owner,
               String machine, String description,
               List<String> dependencies) {

        this(name, chain, owner, machine, description,
             dependencies.stream()
                 .map(dep -> new JobDependency(dep, "success"))
                 .collect(Collectors.toList()),
             new ArrayList<>());
    }

    // Extended constructor
    public Job(String name, String chain, String owner,
               String machine, String description,
               List<JobDependency> dependencies,
               List<JobParameter> parameters) {

        this.name = name;
        this.chain = chain;
        this.owner = owner;
        this.machine = machine;
        this.description = description;

        this.dependencyDetails = new ArrayList<>(dependencies);
        this.parameters = new ArrayList<>(parameters);
    }

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

    // Existing method: returns dependency names
    public List<String> getDependencies() {
        return dependencyDetails.stream()
            .map(JobDependency::getJobName)
            .collect(Collectors.toList());
    }

    public List<JobDependency> getDependencyDetails() {
        return new ArrayList<>(dependencyDetails);
    }

    public List<JobParameter> getParameters() {
        return new ArrayList<>(parameters);
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
