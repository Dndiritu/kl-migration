
public class Main {

    public static void main(String[] args) 
    {

        // Job 1: Extract
        Job job1 = new Job(
            "KL_CUS_EXTRACT_01",
            "CMD",
            "KL_CUSTOMER_FEEDS",
            "finsvc",
            "klx-app02"
        );

        // Job 2: Validate
        Job job2 = new Job(
            "KL_CUS_VALIDATE_02",
            "CMD",
            "KL_CUSTOMER_FEEDS",
            "etlsvc",
            "klx-db01"
        );

        // Job 3: Transform
        Job job3 = new Job(
            "KL_CUS_TRANSFORM_03",
            "CMD",
            "KL_CUSTOMER_FEEDS",
            "finsvc",
            "klx-app01"
        );

        // Job 4: Suspicious notification job
        Job job4 = new Job(
            "KL_CUS_NOTIFY_06!",
            "CMD",
            "KL_CUSTOMER_FEEDS",
            "etlsvc",
            "klx-app02"
        );

        // Print job descriptions
        job1.printDescription();
        job2.printDescription();
        job3.printDescription();
        job4.printDescription();


        System.out.println("\n--- JOB NAME VALIDATION ---");

        System.out.println(job1.isNameValid());
        System.out.println(job2.isNameValid());
        System.out.println(job3.isNameValid());
        System.out.println(job4.isNameValid());


    }
}
