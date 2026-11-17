
public class Main {

    public static void main(String[] args) {

        // =====================================
        // 1. CREATE JOB OBJECTS
        // =====================================

        Job job1 = new Job(
            "KL_CUS_EXTRACT_01",
            "CMD",
            "KL_CUSTOMER_FEEDS",
            "finsvc",
            "klx-app02",
            new String[] {}
        );

        Job job2 = new Job(
            "KL_CUS_VALIDATE_02",
            "CMD",
            "KL_CUSTOMER_FEEDS",
            "etlsvc",
            "klx-db01",
            new String[] {"KL_CUS_EXTRACT_01"}
        );

        Job job3 = new Job(
            "KL_CUS_TRANSFORM_03",
            "CMD",
            "KL_CUSTOMER_FEEDS",
            "finsvc",
            "klx-app01",
            new String[] {"KL_CUS_VALIDATE_02"}
        );

        Job job4 = new Job(
            "KL_CUS_NOTIFY_06!",
            "CMD",
            "KL_CUSTOMER_FEEDS",
            "etlsvc",
            "klx-app02",
            new String[] {"KL_CUS_RECONCILE_05"}
        );


        // =====================================
        // 2. PRINT JOB DESCRIPTIONS
        // =====================================

        System.out.println("===== JOB DESCRIPTIONS =====");

        job1.printDescription();
        job2.printDescription();
        job3.printDescription();
        job4.printDescription();


        // =====================================
        // 3. JOB NAME VALIDATION
        // =====================================

        System.out.println("\n===== JOB NAME VALIDATION =====");

        System.out.println(job1.getName() + ": " + job1.isNameValid());
        System.out.println(job2.getName() + ": " + job2.isNameValid());
        System.out.println(job3.getName() + ": " + job3.isNameValid());
        System.out.println(job4.getName() + ": " + job4.isNameValid());


        // =====================================
        // 4. TEST GETTERS
        // =====================================

        System.out.println("\n===== TEST GETTERS =====");

        System.out.println("Job name: " + job1.getName());
        System.out.println("Job type: " + job1.getType());
        System.out.println("Job chain: " + job1.getChain());
        System.out.println("Job owner: " + job1.getOwner());
        System.out.println("Job machine: " + job1.getMachine());


        // =====================================
        // 5. TEST SETTERS
        // =====================================

        System.out.println("\n===== TEST SETTERS =====");

        job1.setOwner("etlsvc");
        job1.setMachine("klx-app03");

        job1.printDescription();


        // =====================================
        // 6. TEST DEPENDENCIES
        // =====================================

        System.out.println("\n===== DEPENDENCY CHECKS =====");

        System.out.println(
            "Does " + job2.getName() +
            " depend on KL_CUS_EXTRACT_01? " +
            job2.dependsOn("KL_CUS_EXTRACT_01")
        );

        System.out.println(
            "Does " + job3.getName() +
            " depend on KL_CUS_EXTRACT_01? " +
            job3.dependsOn("KL_CUS_EXTRACT_01")
        );

        System.out.println(
            "Does " + job4.getName() +
            " depend on KL_CUS_RECONCILE_05? " +
            job4.dependsOn("KL_CUS_RECONCILE_05")
        );

    }
}
