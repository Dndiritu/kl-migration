
import java.util.ArrayList;
import java.util.List;

public class Chain {

    private final String name;
    private final String days;
    private final String time;
    private final List<Job> jobs;

    public Chain(String name, String days, String time, List<Job> jobs) {

        this.name = name;
        this.days = days;
        this.time = time;
        this.jobs = new ArrayList<>(jobs);
    }

    public String getName() {
        return name;
    }

    public String getDays() {
        return days;
    }

    public String getTime() {
        return time;
    }

    public List<Job> getJobs() {
        return new ArrayList<>(jobs);
    }

    public void printJobsInRunOrder() {

        System.out.println("\n===== CHAIN RUN ORDER =====");

        int position = 1;

        for (Job job : jobs) {
            System.out.println(position + ". " + job.getName());
            position++;
        }
    }

    public List<String> findInvalidDependencies() {

        List<String> invalidDependencies = new ArrayList<>();

        for (Job job : jobs) {

            for (String dependency : job.getDependencies()) {

                if (dependency.equals(job.getName())) {

                    invalidDependencies.add(
                        job.getName() + " depends on itself"
                    );

                    continue;
                }

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

        return invalidDependencies;
    }
}
