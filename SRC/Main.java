
public class Main {

    public static void main(String[] args) {

        Job[] jobs = {

            // CMD job 1
            new CommandJob(
                "KL_CUS_EXTRACT_01",
                "KL_CUSTOMER_FEEDS",
                "finsvc",
                "klx-app02",
                "Extract step 1 for KL_CUSTOMER_FEEDS",
                new String[] {},
                "/opt/kl/cus/extract_01.sh"
            ),

            // CMD job 2
            new CommandJob(
                "KL_CUS_VALIDATE_02",
                "KL_CUSTOMER_FEEDS",
                "etlsvc",
                "klx-db01",
                "Validate step 2 for KL_CUSTOMER_FEEDS",
                new String[] {"KL_CUS_EXTRACT_01"},
                "/opt/kl/cus/validate_02.sh"
            ),

            // FT job 1
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

            // FT job 2 - source XML has an empty target
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

        // Check all jobs using one loop
        for (Job job : jobs) {

            System.out.println("\n------------------------");

            job.printDescription();

            String problem = job.getTypeSpecificProblem();

            if (problem != null) {
                System.out.println("PROBLEM: " + problem);
            } else {
                System.out.println("Status: No type-specific problems found");
            }
        }
    }
}
