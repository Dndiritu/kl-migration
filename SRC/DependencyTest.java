
import java.util.ArrayList;
import java.util.List;

public class DependencyTest {

    public static void main(String[] args) {

        try {

            XMLData data =
                XMLLoader.load("customer_export.xml");

            Chain targetChain = null;

            // Find KL_ORDERS_NIGHTLY
            for (Chain chain : data.getChains()) {

                if (chain.getName().equals("KL_ORDERS_NIGHTLY")) {
                    targetChain = chain;
                    break;
                }
            }

            if (targetChain == null) {
                System.out.println("Chain not found.");
                return;
            }

            List<Job> firstFive = new ArrayList<>();

            List<Job> allJobs = targetChain.getJobs();

            // Select the first five jobs
            for (int i = 0; i < Math.min(5, allJobs.size()); i++) {
                firstFive.add(allJobs.get(i));
            }

            System.out.println(
                "===== FIRST FIVE JOBS: KL_ORDERS_NIGHTLY ====="
            );

            for (Job job : firstFive) {

                System.out.println("\nJob: " + job.getName());

                System.out.println(
                    "Dependencies: " + job.getDependencies()
                );
            }

            // Check dependencies
            System.out.println(
                "\n===== INVALID DEPENDENCIES ====="
            );

            List<String> problems =
                targetChain.findDependenciesOutsideChain(firstFive);

            if (problems.isEmpty()) {

                System.out.println("No missing dependencies found.");

            } else {

                for (String problem : problems) {
                    System.out.println(problem);
                }
            }

        } catch (XMLLoaderException e) {

            System.out.println(
                "XML loading failed: " + e.getMessage()
            );
        }
    }
}
