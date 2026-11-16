
public class Job {

    // 1. Attributes
    private String name;
    private String type;
    private String chain;
    private String owner;
    private String machine;

    // 2. Constructor
    public Job(String name, String type, String chain, String owner, String machine)
     {
        this.name = name;
        this.type = type;
        this.chain = chain;
        this.owner = owner;
        this.machine = machine;
    }

    // 3. Method to describe the job
    public void printDescription() 
    {
        System.out.println("Job: " + name
                + " | Type: " + type
                + " | Chain: " + chain
                + " | Owner: " + owner
                + " | Machine: " + machine);
    }

    public boolean isNameValid() 
    {

        return name.matches("[A-Z0-9_]+");

    }

}
