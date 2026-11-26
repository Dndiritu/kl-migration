
public class JobDependency {

    private final String jobName;
    private final String condition;

    public JobDependency(String jobName, String condition) {
        this.jobName = jobName;
        this.condition = condition;
    }

    public String getJobName() {
        return jobName;
    }

    public String getCondition() {
        return condition;
    }
}
