
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Main {

    // =====================================
    // 1. JOB BUILDER WITH CUSTOM EXCEPTION
    // =====================================

    public static Job buildJob(
            String name,
            String type,
            String chain,
            String owner,
            String machine,
            String description,
            List<String> dependencies,
            String command,
            String source,
            String target
    ) throws JobBuildException {

        if (name == null || name.isBlank()) {
            throw new JobBuildException(
                "Job name cannot be blank."
            );
        }

        if (type == null || type.isBlank()) {
            throw new JobBuildException(
                "Job type cannot be blank."
            );
        }

        if (type.equals("CMD")) {
            return new CommandJob(
                name, chain, owner, machine,
                description, dependencies, command
            );
        }

        if (type.equals("FT")) {
            return new FileTransferJob(
                name, chain, owner, machine,
                description, dependencies, source, target
            );
        }

        throw new JobBuildException(
            "Unsupported job type '" + type +
            "'. Supported job types are CMD and FT."
        );
    }


    // =====================================
    // 2. MAIN METHOD
    // =====================================

    public static void main(String[] args) {

        // Store jobs using a List
        List<Job> jobs = new ArrayList<>();

        jobs.add(new CommandJob(
            "KL_CUS_EXTRACT_01",
            "KL_CUSTOMER_FEEDS",
            "finsvc",
            "klx-app02",
            "Extract step 1 for KL_CUSTOMER_FEEDS",
            List.of(),
            "/opt/kl/cus/extract_01.sh"
        ));

        jobs.add(new CommandJob(
            "KL_CUS_VALIDATE_02",
            "KL_CUSTOMER_FEEDS",
            "etlsvc",
            "klx-db01",
            "Validate step 2 for KL_CUSTOMER_FEEDS",
            List.of("KL_CUS_EXTRACT_01"),
            "/opt/kl/cus/validate_02.sh"
        ));

        jobs.add(new FileTransferJob(
            "KL_CUS_EXPORT_12",
            "KL_CUSTOMER_FEEDS",
            "finsvc",
            "klx-app02",
            "Export step 12 for KL_CUSTOMER_FEEDS",
            List.of("KL_CUS_AGGREGATE_11"),
            "/data/cus/out/export_12.csv",
            "\\\\kwin-fs01\\drop\\cus\\export_12.csv"
        ));

        jobs.add(new FileTransferJob(
            "KL_CUS_TRANSFER_07",
            "KL_CUSTOMER_FEEDS",
            "batchsvc",
            "klx-app01",
            "Transfer step 7 for KL_CUSTOMER_FEEDS",
            List.of("KL_CUS_NOTIFY_06!"),
            "/data/cus/out/transfer_07.csv",
            ""
        ));


        // =====================================
        // 3. CREATE SCHEDULES USING A LIST
        // =====================================

        List<Schedule> schedules = new ArrayList<>();

        schedules.add(new Schedule(
            "KL_HEALTHCHECK_HOURLY", "daily", "00:00"
        ));

        schedules.add(new Schedule(
            "KL_LOG_ROTATE", "daily", "25:30"
        ));

        schedules.add(new Schedule(
            "KL_CERT_CHECK", "mon-fry", "07:00"
        ));

        schedules.add(new Schedule(
            "KL_TEMP_CLEANUP", "daily", "12:00"
        ));


        // =====================================
        // 4. COMBINE JOBS AND SCHEDULES
        // =====================================

        List<Checkable> items = new ArrayList<>();

        items.addAll(jobs);
        items.addAll(schedules);


        // =====================================
        // 5. MAP FOR JOB LOOKUP
        // =====================================

        Map<String, Job> jobsByName = new LinkedHashMap<>();

        List<String> duplicateNames = new ArrayList<>();

        for (Job job : jobs) {

            if (jobsByName.containsKey(job.getName())) {

                duplicateNames.add(job.getName());

            } else {

                jobsByName.put(job.getName(), job);
            }
        }

        System.out.println("===== DUPLICATE JOB NAMES =====");

        if (duplicateNames.isEmpty()) {

            System.out.println("No duplicate job names found.");

        } else {

            for (String name : duplicateNames) {
                System.out.println("Duplicate: " + name);
            }
        }


        // =====================================
        // 6. FIND JOB BY NAME
        // =====================================

        System.out.println("\n===== JOB LOOKUP =====");

        String searchName = "KL_CUS_VALIDATE_02";

        Job foundJob = jobsByName.get(searchName);

        if (foundJob != null) {

            System.out.println("Found: " + foundJob.getName());
            System.out.println("Type: " + foundJob.getType());

        } else {

            System.out.println("Job not found: " + searchName);
        }


        // =====================================
        // 7. COUNT JOBS BY TYPE
        // =====================================

        Map<String, Integer> jobCounts = new LinkedHashMap<>();

        for (Job job : jobs) {

            String type = job.getType();

            jobCounts.put(
                type,
                jobCounts.getOrDefault(type, 0) + 1
            );
        }

        System.out.println("\n===== JOB COUNTS =====");

        for (Map.Entry<String, Integer> entry : jobCounts.entrySet()) {

            System.out.println(
                entry.getKey() + ": " + entry.getValue()
            );
        }


        // =====================================
        // 8. VALIDATE JOBS AND SCHEDULES
        // =====================================

        System.out.println("\n===== VALIDATION RESULTS =====");

        for (Checkable item : items) {

            String name;

            if (item instanceof Job) {

                name = ((Job) item).getName();

            } else {

                name = ((Schedule) item).getName();
            }

            System.out.println("\nChecking: " + name);

            if (item.isValid()) {

                System.out.println("Status: VALID");

            } else {

                System.out.println(
                    "Status: INVALID - " +
                    item.getValidationProblem()
                );
            }
        }


        // =====================================
        // 9. CUSTOM EXCEPTION TEST
        // =====================================

        System.out.println("\n===== CUSTOM EXCEPTION TEST =====");

        try {

            Job testJob = buildJob(
                "KL_TEST_UNKNOWN",
                "PYTHON",
                "KL_CUSTOMER_FEEDS",
                "finsvc",
                "klx-app02",
                "Unknown type test",
                List.of(),
                null,
                null,
                null
            );

            jobs.add(testJob);

        } catch (JobBuildException e) {

            System.out.println(
                "Job creation failed: " + e.getMessage()
            );
        }

    }
}
