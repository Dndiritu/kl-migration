
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Main {

    public static void main(String[] args) {

        try {
            XMLData data = XMLLoader.load("customer_export.xml");

            List<Machine> machines = data.getMachines();
            List<Chain> chains = data.getChains();
            List<Job> jobs = data.getJobs();
            List<Schedule> schedules = data.getSchedules();

            System.out.println("========== XML IMPORT SUMMARY ==========");
            System.out.println("Machines: " + machines.size());
            System.out.println("Chains: " + chains.size());
            System.out.println("Total jobs: " + jobs.size());
            System.out.println("Schedules: " + schedules.size());

            // Duplicate job detection
            System.out.println("\n========== DUPLICATE JOB NAMES ==========");

            Map<String, Integer> nameCounts = new HashMap<>();

            for (Job job : jobs) {
                nameCounts.merge(job.getName(), 1, Integer::sum);
            }

            boolean duplicatesFound = false;

            for (Map.Entry<String, Integer> entry : nameCounts.entrySet()) {
                if (entry.getValue() > 1) {
                    System.out.println(
                        entry.getKey() + ": " +
                        entry.getValue() + " occurrences"
                    );
                    duplicatesFound = true;
                }
            }

            if (!duplicatesFound) {
                System.out.println("No duplicate job names found.");
            }

            // Job counts by type
            int commandCount = 0;
            int fileTransferCount = 0;

            for (Job job : jobs) {
                if (job instanceof CommandJob) {
                    commandCount++;
                } else if (job instanceof FileTransferJob) {
                    fileTransferCount++;
                }
            }

            System.out.println("\n========== JOBS BY TYPE ==========");
            System.out.println("CMD jobs: " + commandCount);
            System.out.println("FT jobs: " + fileTransferCount);

            // Jobs belonging to each chain
            System.out.println("\n========== JOBS BY CHAIN ==========");

            for (Chain chain : chains) {
                System.out.println(
                    chain.getName() + ": " +
                    chain.getJobs().size() + " jobs"
                );
            }

            // Identify standalone and unknown-chain jobs
            Set<String> definedChains = new HashSet<>();

            for (Chain chain : chains) {
                definedChains.add(chain.getName());
            }

            int unassigned = 0;
            int unknownChain = 0;

            for (Job job : jobs) {
                if (job.getChain() == null ||
                    job.getChain().isBlank()) {
                    unassigned++;
                } else if (!definedChains.contains(job.getChain())) {
                    unknownChain++;
                }
            }

            System.out.println("\n========== UNGROUPED JOBS ==========");
            System.out.println("Jobs without a chain: " + unassigned);
            System.out.println(
                "Jobs referencing unknown chains: " + unknownChain
            );

            // Schedule validation
            System.out.println("\n========== SCHEDULE VALIDATION ==========");

            int validSchedules = 0;
            int invalidSchedules = 0;

            for (Schedule schedule : schedules) {

                System.out.println("\nChecking: " + schedule.getName());

                if (schedule.isValid()) {
                    validSchedules++;
                    System.out.println("Status: VALID");
                } else {
                    invalidSchedules++;
                    System.out.println(
                        "Status: INVALID - " +
                        schedule.getValidationProblem()
                    );
                }
            }

            System.out.println("\n========== SCHEDULE SUMMARY ==========");
            System.out.println("Valid schedules: " + validSchedules);
            System.out.println("Invalid schedules: " + invalidSchedules);

            System.out.println("\n========== IMPORT COMPLETE ==========");

        } catch (XMLLoaderException e) {
            System.out.println(
                "XML IMPORT FAILED: " + e.getMessage()
            );
        }
    }
}
