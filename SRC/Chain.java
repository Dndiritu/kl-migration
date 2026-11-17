
public class Chain {

    // =====================================
    // 1. PRIVATE FIELDS
    // =====================================

    private final String name;
    private final String days;
    private final String time;
    private final Job[] jobs;


    // =====================================
    // 2. CONSTRUCTOR
    // =====================================

    public Chain(String name, String days, String time, Job[] jobs) {

        this.name = name;
        this.days = days;
        this.time = time;
        this.jobs = jobs.clone();
    }


    // =====================================
    // 3. GETTERS
    // =====================================

    public String getName() {
        return name;
    }

    public String getDays() {
        return days;
    }

    public String getTime() {
        return time;
    }

    public Job[] getJobs() {
        return jobs.clone();
    }


    // =====================================
    // 4. PRINT JOBS IN RUN ORDER
    // =====================================

    public void printJobsInRunOrder() {

        System.out.println("\n===== CHAIN RUN ORDER =====");

        for (int i = 0; i < jobs.length; i++) {

            System.out.println(
                (i + 1) + ". " + jobs[i].getName()
            );
        }
    }


    // =====================================
    // 5. CHECK INVALID DEPENDENCIES
    // =====================================

    public String[] findInvalidDependencies() {

        java.util.ArrayList<String> invalidDependencies =
            new java.util.ArrayList<>();

        for (Job job : jobs) {

            for (String dependency : job.getDependencies()) {

                // Check 1: Self-dependency
                if (dependency.equals(job.getName())) {

                    invalidDependencies.add(
                        job.getName() + " depends on itself"
                    );

                    continue;
                }

                // Check 2: Dependency missing from chain
                boolean found = false;

                for (Job chainJob : jobs) {

                    if (chainJob.getName().equals(dependency)) {

                        found = true;
                        break;
                    }
                }

                if (!found) {

                    invalidDependencies.add(
                        job.getName() +
                        " references missing job: " + dependency
                    );
                }
            }
        }

        return invalidDependencies.toArray(new String[0]);
    }
}
