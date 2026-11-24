
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class Validator {

    private static final Pattern VALID_NAME =
        Pattern.compile("[A-Z][A-Z0-9_]*");

    private static final List<String[]> issues = new ArrayList<>();

    public static void main(String[] args) {

        String inputFile = args.length > 0
            ? args[0]
            : "customer_export_fixed.xml";

        String reportFile = args.length > 1
            ? args[1]
            : "validation_report_fixed.csv";

        try {
            // Load the XML export rather than the JSON export.
            XMLData data = XMLLoader.load(inputFile);

            issues.clear();

            validate(data);

            writeReport(reportFile);

            System.out.println("\n========== VALIDATION REPORT ==========");
            System.out.println("Input file: " + inputFile);
            System.out.println("Total jobs checked: " + data.getJobs().size());
            System.out.println("Total issues found: " + issues.size());

            System.out.println("\n========== ISSUE BREAKDOWN ==========");

            Map<String, Integer> issueCounts = new HashMap<>();

            for (String[] issue : issues) {
                issueCounts.merge(issue[0], 1, Integer::sum);
            }

            for (Map.Entry<String, Integer> entry :
                    issueCounts.entrySet()) {
                System.out.println(
                    entry.getKey() + ": " + entry.getValue()
                );
            }

            System.out.println("\nReport saved as: " + reportFile);

        } catch (XMLLoaderException | IOException e) {
            System.out.println("Validation failed: " + e.getMessage());
        }
    }

    public static void validate(XMLData data) {

        List<Job> jobs = data.getJobs();

        Set<String> validChains = new HashSet<>();
        for (Chain chain : data.getChains()) {
            validChains.add(chain.getName());
        }

        Set<String> validMachines = new HashSet<>();
        for (Machine machine : data.getMachines()) {
            validMachines.add(machine.getName());
        }

        Set<String> validJobNames = new HashSet<>();
        Map<String, Integer> nameCounts = new HashMap<>();

        for (Job job : jobs) {
            if (job.getName() != null) {
                validJobNames.add(job.getName());
                nameCounts.merge(job.getName(), 1, Integer::sum);
            }
        }

        // 1. Validate individual jobs
        for (Job job : jobs) {

            String name = job.getName();

            // Check job name format
            if (name == null || !VALID_NAME.matcher(name).matches()) {
                addIssue(
                    "BADLY_FORMATTED_NAME",
                    name,
                    "Job name must contain uppercase letters, digits and underscores, starting with a letter"
                );
            }

            // Check owner
            if (job.getOwner() == null || job.getOwner().isBlank()) {
                addIssue(
                    "MISSING_OWNER",
                    name,
                    "Job has no owner"
                );
            }

            // Check chain
            if (job.getChain() == null ||
                job.getChain().isBlank() ||
                !validChains.contains(job.getChain())) {

                addIssue(
                    "UNDEFINED_CHAIN",
                    name,
                    "Chain is missing or not declared: " + job.getChain()
                );
            }

            // Check machine
            if (job.getMachine() == null ||
                job.getMachine().isBlank() ||
                !validMachines.contains(job.getMachine())) {

                addIssue(
                    "UNDEFINED_MACHINE",
                    name,
                    "Machine is missing or not declared: " + job.getMachine()
                );
            }

            // Check dependencies
            for (String dependency : job.getDependencies()) {

                if (!validJobNames.contains(dependency)) {
                    addIssue(
                        "MISSING_DEPENDENCY",
                        name,
                        "References nonexistent job: " + dependency
                    );
                }
            }

            // Check type-specific requirements
            String typeProblem = job.getTypeSpecificProblem();

            if (typeProblem != null) {
                addIssue(
                    "TYPE_SPECIFIC_PROBLEM",
                    name,
                    typeProblem
                );
            }
        }

        // 2. Detect duplicate job names
        for (Job job : jobs) {

            String name = job.getName();

            if (name != null && nameCounts.get(name) > 1) {
                addIssue(
                    "DUPLICATE_NAME",
                    name,
                    "Job name occurs " + nameCounts.get(name) + " times"
                );
            }
        }

        // 3. Validate schedules
        for (Schedule schedule : data.getSchedules()) {

            String problem = schedule.getValidationProblem();

            if (problem != null) {
                addIssue(
                    "INVALID_SCHEDULE",
                    schedule.getName(),
                    problem
                );
            }
        }

        // 4. Detect dependency cycles
        detectDependencyCycles(jobs);
    }

    // Detect circular dependencies between jobs
    private static void detectDependencyCycles(List<Job> jobs) {

        Map<String, Job> uniqueJobs = new HashMap<>();
        Map<String, Integer> counts = new HashMap<>();

        // Count job names
        for (Job job : jobs) {
            if (job.getName() != null) {
                counts.merge(job.getName(), 1, Integer::sum);
            }
        }

        // Exclude ambiguous duplicate names
        for (Job job : jobs) {

            String name = job.getName();

            if (name != null && counts.get(name) == 1) {
                uniqueJobs.put(name, job);
            }
        }

        // Build dependency graph
        Map<String, List<String>> graph = new HashMap<>();

        for (Job job : uniqueJobs.values()) {

            List<String> dependencies = new ArrayList<>();

            for (String dependency : job.getDependencies()) {

                if (uniqueJobs.containsKey(dependency)) {
                    dependencies.add(dependency);
                }
            }

            graph.put(job.getName(), dependencies);
        }

        Map<String, Integer> state = new HashMap<>();
        List<String> path = new ArrayList<>();
        Set<String> reportedCycles = new HashSet<>();

        for (String name : graph.keySet()) {

            if (state.getOrDefault(name, 0) == 0) {

                visitDependency(
                    name,
                    graph,
                    state,
                    path,
                    reportedCycles
                );
            }
        }
    }

    // Depth-first search to identify dependency cycles
    private static void visitDependency(
            String name,
            Map<String, List<String>> graph,
            Map<String, Integer> state,
            List<String> path,
            Set<String> reportedCycles) {

        // 0 = unvisited, 1 = visiting, 2 = visited
        state.put(name, 1);
        path.add(name);

        for (String dependency : graph.get(name)) {

            int dependencyState =
                state.getOrDefault(dependency, 0);

            if (dependencyState == 0) {

                visitDependency(
                    dependency,
                    graph,
                    state,
                    path,
                    reportedCycles
                );

            } else if (dependencyState == 1) {

                int cycleStart = path.indexOf(dependency);

                List<String> cycle = new ArrayList<>(
                    path.subList(cycleStart, path.size())
                );

                cycle.add(dependency);

                // Create a canonical key to avoid duplicate reports
                List<String> sortedNames = new ArrayList<>(
                    cycle.subList(0, cycle.size() - 1)
                );

                Collections.sort(sortedNames);

                String cycleKey = String.join("|", sortedNames);

                if (reportedCycles.add(cycleKey)) {

                    addIssue(
                        "DEPENDENCY_CYCLE",
                        name,
                        "Circular dependency detected: " +
                        String.join(" -> ", cycle)
                    );
                }
            }
        }

        path.remove(path.size() - 1);
        state.put(name, 2);
    }

    // Add an issue to the report
    private static void addIssue(
            String issueType,
            String jobName,
            String detail) {

        issues.add(new String[] {
            issueType,
            jobName == null ? "" : jobName,
            detail == null ? "" : detail
        });
    }

    // Write findings to CSV
    private static void writeReport(String fileName)
            throws IOException {

        try (BufferedWriter writer =
                Files.newBufferedWriter(
                    Path.of(fileName),
                    StandardCharsets.UTF_8
                )) {

            writer.write("issue_type,job_name,detail");
            writer.newLine();

            for (String[] issue : issues) {

                writer.write(
                    csvEscape(issue[0]) + "," +
                    csvEscape(issue[1]) + "," +
                    csvEscape(issue[2])
                );

                writer.newLine();
            }
        }
    }

    // Escape values correctly for CSV
    private static String csvEscape(String value) {

        if (value.contains(",") ||
            value.contains("\"") ||
            value.contains("\n") ||
            value.contains("\r")) {

            return "\"" + value.replace("\"", "\"\"") + "\"";
        }

        return value;
    }
}
