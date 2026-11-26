
public class LoaderTest {

    public static void main(String[] args) {

        try {

            XMLData xmlData =
                XMLLoader.load("customer_export.xml");

            XMLData jsonData =
                ConvertedLoader.load("converted_export.json");

            System.out.println("===== LOADER TEST =====");

            System.out.println(
                "XML jobs: " + xmlData.getJobs().size()
            );

            System.out.println(
                "JSON jobs: " + jsonData.getJobs().size()
            );

            System.out.println(
                "XML chains: " + xmlData.getChains().size()
            );

            System.out.println(
                "JSON chains: " + jsonData.getChains().size()
            );

            System.out.println(
                "XML machines: " + xmlData.getMachines().size()
            );

            System.out.println(
                "JSON machines: " + jsonData.getMachines().size()
            );

            System.out.println(
                "XML schedules: " + xmlData.getSchedules().size()
            );

            System.out.println(
                "JSON schedules: " + jsonData.getSchedules().size()
            );

            // Check dependency conditions
            System.out.println("\n===== DEPENDENCY TEST =====");

            for (Job job : jsonData.getJobs()) {

                if ("KL_INV_NOTIFY_06".equals(job.getName())) {

                    System.out.println("Job: " + job.getName());

                    for (JobDependency dependency :
                            job.getDependencyDetails()) {

                        System.out.println(
                            "Depends on: " +
                            dependency.getJobName()
                        );

                        System.out.println(
                            "Condition: " +
                            dependency.getCondition()
                        );
                    }
                }
            }

            // Check parameters
            System.out.println("\n===== PARAMETER TEST =====");

            for (Job job : jsonData.getJobs()) {

                if (!job.getParameters().isEmpty()) {

                    System.out.println(
                        "Job: " + job.getName()
                    );

                    for (JobParameter parameter :
                            job.getParameters()) {

                        System.out.println(
                            parameter.getName() +
                            " = " + parameter.getValue()
                        );
                    }

                    break;
                }
            }

        } catch (XMLLoaderException e) {

            System.out.println(
                "LOADER ERROR: " + e.getMessage()
            );
        }
    }
}
