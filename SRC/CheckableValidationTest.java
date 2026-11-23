
import java.util.List;

public class CheckableValidationTest {

    public static void main(String[] args) {

        Checkable[] items = {

            // CMD job with missing command
            new CommandJob(
                "KL_FIN_BACKUP_10",
                "KL_FINANCE_CLOSE",
                "Finance",
                "MACHINE_01",
                "Finance backup",
                List.of(),
                ""
            ),

            // FT job with missing target
            new FileTransferJob(
                "KL_CUS_TRANSFER_07",
                "KL_CUSTOMER_FEEDS",
                "Customer",
                "MACHINE_02",
                "Customer file transfer",
                List.of(),
                "/data/customers.csv",
                ""
            ),

            // Invalid schedule: incorrect day
            new Schedule(
                "KL_CERT_CHECK",
                "mon-fry",
                "08:00"
            ),

            // Invalid schedule: incorrect time
            new Schedule(
                "KL_LOG_ROTATE",
                "daily",
                "25:30"
            ),

            // Valid CMD job
            new CommandJob(
                "KL_ORD_EXTRACT_01",
                "KL_ORDERS_NIGHTLY",
                "Operations",
                "MACHINE_01",
                "Extract orders",
                List.of(),
                "extract_orders"
            )
        };

        System.out.println("===== MIXED VALIDATION RESULTS =====");

        for (Checkable item : items) {

            if (!item.isValid()) {

                System.out.println(
                    "INVALID: " + item.getValidationProblem()
                );

            } else {

                System.out.println("VALID");
            }
        }
    }
}
