
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {

        try {
            // Load both exports
            XMLData xmlData =
                XMLLoader.load("customer_export.xml");

            XMLData jsonData =
                JsonLoader.load("customer_export.json");

            System.out.println("========== XML VS JSON COMPARISON ==========");

            // Compare total records
            compare("Machines",
                xmlData.getMachines().size(),
                jsonData.getMachines().size());

            compare("Chains",
                xmlData.getChains().size(),
                jsonData.getChains().size());

            compare("Total jobs",
                xmlData.getJobs().size(),
                jsonData.getJobs().size());

            compare("Schedules",
                xmlData.getSchedules().size(),
                jsonData.getSchedules().size());

            // Compare job names, preserving duplicates
            System.out.println("\n========== JOB NAME COMPARISON ==========");

            Map<String, Integer> xmlNames =
                getJobNameCounts(xmlData.getJobs());

            Map<String, Integer> jsonNames =
                getJobNameCounts(jsonData.getJobs());

            compareMaps("Job names and occurrences",
                xmlNames, jsonNames);

            // Compare unique job names
            int xmlUniqueNames =
                getUniqueJobNameCount(xmlData.getJobs());

            int jsonUniqueNames =
                getUniqueJobNameCount(jsonData.getJobs());

            compare("Unique job names",
                xmlUniqueNames, jsonUniqueNames);

            // Compare job types
            System.out.println("\n========== JOB TYPE COMPARISON ==========");

            Map<String, Integer> xmlTypes =
                getJobTypeCounts(xmlData.getJobs());

            Map<String, Integer> jsonTypes =
                getJobTypeCounts(jsonData.getJobs());

            compareMaps("Job counts by type",
                xmlTypes, jsonTypes);

            System.out.println("XML: " + xmlTypes);
            System.out.println("JSON: " + jsonTypes);

            // Compare jobs per chain
            System.out.println("\n========== JOBS PER CHAIN ==========");

            Map<String, Integer> xmlChains =
                getChainJobCounts(xmlData.getChains());

            Map<String, Integer> jsonChains =
                getChainJobCounts(jsonData.getChains());

            compareMaps("Jobs per chain",
                xmlChains, jsonChains);

            System.out.println("XML: " + xmlChains);
            System.out.println("JSON: " + jsonChains);

            // Build a safe registry using XML jobs
            System.out.println("\n========== SAFE JOB REGISTRY ==========");

            JobRegistry registry = new JobRegistry();

            registry.registerAll(xmlData.getJobs());

            registry.printDuplicates();

            System.out.println(
                "Total jobs preserved in registry: " +
                registry.getAllJobs().size()
            );

            // Final result
            System.out.println("\n========== FINAL RESULT ==========");

            boolean allMatch =
                xmlData.getMachines().size() == jsonData.getMachines().size()
                && xmlData.getChains().size() == jsonData.getChains().size()
                && xmlData.getJobs().size() == jsonData.getJobs().size()
                && xmlData.getSchedules().size() == jsonData.getSchedules().size()
                && xmlNames.equals(jsonNames)
                && xmlUniqueNames == jsonUniqueNames
                && xmlTypes.equals(jsonTypes)
                && xmlChains.equals(jsonChains);

            if (allMatch) {
                System.out.println(
                    "SUCCESS: XML and JSON loaders produced matching results."
                );
            } else {
                System.out.println(
                    "WARNING: Differences found between XML and JSON loaders."
                );
            }

        } catch (XMLLoaderException e) {

            System.out.println("IMPORT FAILED: " + e.getMessage());
        }
    }

    private static void compare(
            String label, int xmlCount, int jsonCount) {

        String status =
            xmlCount == jsonCount ? "MATCH" : "MISMATCH";

        System.out.println(
            label + " | XML: " + xmlCount +
            " | JSON: " + jsonCount +
            " | " + status
        );
    }

    private static void compareMaps(
            String label,
            Map<String, Integer> xml,
            Map<String, Integer> json) {

        System.out.println(
            label + ": " +
            (xml.equals(json) ? "MATCH" : "MISMATCH")
        );
    }

    private static Map<String, Integer> getJobNameCounts(
            List<Job> jobs) {

        Map<String, Integer> counts = new HashMap<>();

        for (Job job : jobs) {
            counts.merge(job.getName(), 1, Integer::sum);
        }

        return counts;
    }

    private static int getUniqueJobNameCount(List<Job> jobs) {

        return (int) jobs.stream()
            .map(Job::getName)
            .distinct()
            .count();
    }

    private static Map<String, Integer> getJobTypeCounts(
            List<Job> jobs) {

        Map<String, Integer> counts = new HashMap<>();

        for (Job job : jobs) {
            counts.merge(job.getType(), 1, Integer::sum);
        }

        return counts;
    }

    private static Map<String, Integer> getChainJobCounts(
            List<Chain> chains) {

        Map<String, Integer> counts = new LinkedHashMap<>();

        for (Chain chain : chains) {
            counts.put(chain.getName(), chain.getJobs().size());
        }

        return counts;
    }
}
