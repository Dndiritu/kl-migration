
import java.util.List;

public class RegistryTest {

    public static void main(String[] args) {

        System.out.println("===== JOB REGISTRY TEST =====");

        // Test 1: Blank job name
        try {
            JobRegistry.buildJob(
                "CMD",
                "",
                "TEST_CHAIN",
                "TEST",
                "MACHINE_01",
                "Test job",
                List.of(),
                "run",
                null,
                null
            );

        } catch (JobBuildException e) {
            System.out.println("EXPECTED ERROR: " + e.getMessage());
        }

        // Test 2: Unsupported job type
        try {
            JobRegistry.buildJob(
                "PYTHON",
                "TEST_JOB",
                "TEST_CHAIN",
                "TEST",
                "MACHINE_01",
                "Test job",
                List.of(),
                null,
                null,
                null
            );

        } catch (JobBuildException e) {
            System.out.println("EXPECTED ERROR: " + e.getMessage());
        }
    }
}

