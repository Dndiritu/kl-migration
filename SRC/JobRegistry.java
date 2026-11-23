
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JobRegistry {

    private final Map<String, List<Job>> registry =
        new LinkedHashMap<>();

    // Add a job without overwriting existing jobs
    public void register(Job job) {

        if (job == null) {
            throw new IllegalArgumentException("Cannot register a null job.");
        }

        registry.computeIfAbsent(
            job.getName(),
            key -> new ArrayList<>()
        ).add(job);
    }

    // Register all jobs
    public void registerAll(List<Job> jobs) {

        for (Job job : jobs) {
            register(job);
        }
    }

    // Retrieve every job with a particular name
    public List<Job> getJobs(String name) {

        return new ArrayList<>(
            registry.getOrDefault(name, List.of())
        );
    }

    // Return all registered jobs
    public List<Job> getAllJobs() {

        List<Job> allJobs = new ArrayList<>();

        for (List<Job> jobs : registry.values()) {
            allJobs.addAll(jobs);
        }

        return allJobs;
    }

    // Identify and report duplicate names
    public void printDuplicates() {

        System.out.println("\n===== DUPLICATE JOB NAMES =====");

        boolean found = false;

        for (Map.Entry<String, List<Job>> entry
                : registry.entrySet()) {

            if (entry.getValue().size() > 1) {

                found = true;

                System.out.println(
                    entry.getKey() + " appears " +
                    entry.getValue().size() + " times."
                );
            }
        }

        if (!found) {
            System.out.println("No duplicate names found.");
        }
    }

    // Build a job safely
    public static Job buildJob(
            String type,
            String name,
            String chain,
            String owner,
            String machine,
            String description,
            List<String> dependencies,
            String command,
            String source,
            String target) throws JobBuildException {

        if (name == null || name.isBlank()) {
            throw new JobBuildException(
                "Cannot build job: job name cannot be blank."
            );
        }

        if (type == null || type.isBlank()) {
            throw new JobBuildException(
                "Cannot build job '" + name +
                "': job type cannot be blank."
            );
        }

        if (dependencies == null) {
            dependencies = List.of();
        }

        switch (type.trim().toUpperCase()) {

            case "CMD":
                return new CommandJob(
                    name, chain, owner, machine,
                    description, dependencies, command
                );

            case "FT":
                return new FileTransferJob(
                    name, chain, owner, machine,
                    description, dependencies, source, target
                );

            default:
                throw new JobBuildException(
                    "Cannot build job '" + name +
                    "': unsupported job type '" + type +
                    "'. Supported types are CMD and FT."
                );
        }
    }
}
