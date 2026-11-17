
public class Job {

    // 1. Private fields
    private final String name;
    private final String type;
    private final String chain;

    private String owner;
    private String machine;

    // Array holding job dependencies
    private final String[] dependencies;

    // 2. Parameterized constructor
    public Job(String name, String type, String chain,
               String owner, String machine,
               String[] dependencies) {

        this.name = name;
        this.type = type;
        this.chain = chain;
        this.owner = owner;
        this.machine = machine;
        this.dependencies = dependencies.clone();
    }

    // 3. Getters
    public String getName() {
        return name;
    }

    public String getType() {
        return type;
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

    public String[] getDependencies() {
        return dependencies.clone();
    }

    // 4. Setters
    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setMachine(String machine) {
        this.machine = machine;
    }

    // 5. Print job description
    public void printDescription() {
        System.out.println(
            "Job: " + name +
            " | Type: " + type +
            " | Chain: " + chain +
            " | Owner: " + owner +
            " | Machine: " + machine
        );
    }

    // 6. Validate job name
    public boolean isNameValid() {
        return name.matches("[A-Z0-9_]+");
    }

    // 7. Check whether a job depends on another job
    public boolean dependsOn(String jobName) {

        for (String dependency : dependencies) {

            if (dependency.equals(jobName)) {
                return true;
            }
        }

        return false;
    }
}
