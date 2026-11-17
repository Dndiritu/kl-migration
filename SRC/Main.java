
public class Main {

    public static void main(String[] args) {

        // =====================================
        // 1. CREATE THE FIRST FIVE JOBS
        // =====================================

        Job job1 = new Job(
            "KL_ORD_EXTRACT_01",
            "CMD",
            "KL_ORDERS_NIGHTLY",
            "finsvc",
            "klx-app01",
            new String[] {}
        );

        Job job2 = new Job(
            "kl orders validate 02",
            "CMD",
            "KL_ORDERS_NIGHTLY",
            "batchsvc",
            "klx-app02",
            new String[] {"KL_ORD_EXTRACT_01"}
        );

        Job job3 = new Job(
            "KL_ORD_TRANSFORM_03",
            "CMD",
            "KL_ORDERS_NIGHTLY",
            "batchsvc",
            "klx-app01",
            new String[] {"kl orders validate 02"}
        );

        Job job4 = new Job(
            "KL_ORD_LOAD_04",
            "CMD",
            "KL_ORDERS_NIGHTLY",
            "batchsvc",
            "klx-db01",
            new String[] {"KL_ORD_TRANSFORM_03"}
        );

        Job job5 = new Job(
            "KL_ORD_RECONCILE_05",
            "CMD",
            "KL_ORDERS_NIGHTLY",
            "finsvc",
            "klx-db01",
            new String[] {"KL_ORD_TRANSFORM_3"}
        );


        // =====================================
        // 2. CREATE THE CHAIN
        // =====================================

        Chain ordersChain = new Chain(
            "KL_ORDERS_NIGHTLY",
            "mon-fri",
            "01:00",
            new Job[] {job1, job2, job3, job4, job5}
        );


        // =====================================
        // 3. PRINT CHAIN DETAILS
        // =====================================

        System.out.println("===== CHAIN DETAILS =====");

        System.out.println("Chain: " + ordersChain.getName());
        System.out.println("Days: " + ordersChain.getDays());
        System.out.println("Time: " + ordersChain.getTime());


        // =====================================
        // 4. PRINT JOBS IN RUN ORDER
        // =====================================

        ordersChain.printJobsInRunOrder();


        // =====================================
        // 5. CHECK INVALID DEPENDENCIES
        // =====================================

        System.out.println("\n===== INVALID DEPENDENCIES =====");

        String[] invalid =
            ordersChain.findInvalidDependencies();

        if (invalid.length == 0) {

            System.out.println("No invalid dependencies found.");

        } else {

            for (String issue : invalid) {

                System.out.println("Invalid: " + issue);
            }
        }


        // =====================================
        // 6. TEST UNUSUAL INPUT: SELF-DEPENDENCY
        // =====================================

        System.out.println("\n===== SELF-DEPENDENCY TEST =====");

        Job selfJob = new Job(
            "KL_TEST_SELF",
            "CMD",
            "TEST_CHAIN",
            "batchsvc",
            "klx-app01",
            new String[] {"KL_TEST_SELF"}
        );

        Chain testChain = new Chain(
            "TEST_CHAIN",
            "daily",
            "02:00",
            new Job[] {selfJob}
        );

        String[] selfIssues =
            testChain.findInvalidDependencies();

        for (String issue : selfIssues) {

            System.out.println("Invalid: " + issue);
        }


        // =====================================
        // 7. TEST UNUSUAL INPUT: EMPTY DEPENDENCIES
        // =====================================

        System.out.println("\n===== EMPTY DEPENDENCY TEST =====");

        Job emptyJob = new Job(
            "KL_TEST_EMPTY",
            "CMD",
            "TEST_CHAIN",
            "batchsvc",
            "klx-app01",
            new String[] {}
        );

        Chain emptyChain = new Chain(
            "EMPTY_CHAIN",
            "daily",
            "03:00",
            new Job[] {emptyJob}
        );

        String[] emptyIssues =
            emptyChain.findInvalidDependencies();

        if (emptyIssues.length == 0) {

            System.out.println("No invalid dependencies found.");

        }


        // =====================================
        // 8. TEST UNUSUAL INPUT: UNKNOWN DEPENDENCY
        // =====================================

        System.out.println("\n===== UNKNOWN DEPENDENCY TEST =====");

        Job unknownJob = new Job(
            "KL_TEST_UNKNOWN",
            "CMD",
            "TEST_CHAIN",
            "batchsvc",
            "klx-app01",
            new String[] {"KL_JOB_DOES_NOT_EXIST"}
        );

        Chain unknownChain = new Chain(
            "UNKNOWN_CHAIN",
            "daily",
            "04:00",
            new Job[] {unknownJob}
        );

        String[] unknownIssues =
            unknownChain.findInvalidDependencies();

        for (String issue : unknownIssues) {

            System.out.println("Invalid: " + issue);
        }

    }
}
