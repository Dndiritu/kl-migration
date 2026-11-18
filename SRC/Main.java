public class Main {

    public static void main(String[] args) {

        Job[] jobs = {

            new CommandJob(
                "KL_CUS_EXTRACT_01",
                "KL_CUSTOMER_FEEDS",
                "finsvc",
                "klx-app02",
                "Extract step 1 for KL_CUSTOMER_FEEDS",
                new String[] {},
                "/opt/kl/cus/extract_01.sh"
            ),

            new CommandJob(
                "KL_CUS_VALIDATE_02",
                "KL_CUSTOMER_FEEDS",
                "etlsvc",
                "klx-db01",
                "Validate step 2 for KL_CUSTOMER_FEEDS",
                new String[] {"KL_CUS_EXTRACT_01"},
                "/opt/kl/cus/validate_02.sh"
            ),

            new FileTransferJob(
                "KL_CUS_EXPORT_12",
                "KL_CUSTOMER_FEEDS",
                "finsvc",
                "klx-app02",
                "Export step 12 for KL_CUSTOMER_FEEDS",
                new String[] {"KL_CUS_AGGREGATE_11"},
                "/data/cus/out/export_12.csv",
                "\\\\kwin-fs01\\drop\\cus\\export_12.csv"
            ),

            new FileTransferJob(
                "KL_CUS_TRANSFER_07",
                "KL_CUSTOMER_FEEDS",
                "batchsvc",
                "klx-app01",
                "Transfer step 7 for KL_CUSTOMER_FEEDS",
                new String[] {"KL_CUS_NOTIFY_06!"},
                "/data/cus/out/transfer_07.csv",
                ""
            )
        };

        // Standalone schedules from customer_export.xml
        Schedule[] schedules = {

            new Schedule("KL_HEALTHCHECK_HOURLY", "daily", "00:00"),

            new Schedule("KL_LOG_ROTATE", "daily", "25:30"),

            new Schedule("KL_CERT_CHECK", "mon-fry", "07:00"),

            new Schedule("KL_TEMP_CLEANUP", "daily", "12:00")
        };

        // Put jobs and schedules together through the interface
        Checkable[] items = new Checkable[
            jobs.length + schedules.length
        ];

        for (int i = 0; i < jobs.length; i++) {
            items[i] = jobs[i];
        }

        for (int i = 0; i < schedules.length; i++) {
            items[jobs.length + i] = schedules[i];
        }

        // One loop checks both job types and schedules
        System.out.println("===== VALIDATION RESULTS =====");

        for (Checkable item : items) {

            String name;

            if (item instanceof Job) {
                Job job = (Job) item;
                name = job.getName();
            } else {
                Schedule schedule = (Schedule) item;
                name = schedule.getName();
            }

            System.out.println("\nChecking: " + name);

            if (item.isValid()) {
                System.out.println("Status: VALID");
            } else {
                System.out.println(
                    "Status: INVALID - " + item.getValidationProblem()
                );
            }
        }
    }
}