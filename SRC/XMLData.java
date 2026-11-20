
import java.util.ArrayList;
import java.util.List;

public class XMLData {

    private final List<Machine> machines;
    private final List<Chain> chains;
    private final List<Job> jobs;
    private final List<Schedule> schedules;

    public XMLData(List<Machine> machines, List<Chain> chains,
                   List<Job> jobs, List<Schedule> schedules) {
        this.machines = new ArrayList<>(machines);
        this.chains = new ArrayList<>(chains);
        this.jobs = new ArrayList<>(jobs);
        this.schedules = new ArrayList<>(schedules);
    }

    public List<Machine> getMachines() {
        return new ArrayList<>(machines);
    }

    public List<Chain> getChains() {
        return new ArrayList<>(chains);
    }

    public List<Job> getJobs() {
        return new ArrayList<>(jobs);
    }

    public List<Schedule> getSchedules() {
        return new ArrayList<>(schedules);
    }
}
