
public class NameCheckTest {

    public static boolean isValidName(String name) {

        if (name == null || name.length() == 0) {
            return false;
        }

        for (int i = 0; i < name.length(); i++) {

            char character = name.charAt(i);

            boolean uppercase = character >= 'A' && character <= 'Z';
            boolean digit = character >= '0' && character <= '9';
            boolean underscore = character == '_';

            if (!uppercase && !digit && !underscore) {
                return false;
            }
        }

        return true;
    }

    public static void main(String[] args) {

        String[] testNames = {
            "kl orders validate 02",
            "KL-SHP-LOAD-04",
            "Kl_Fin_Reconcile_05",
            "KL_CUS_NOTIFY_06!",
            "",
            "   ",
            "kl_orders_validate_02",
            "KL_ORDERS_VALIDATE_02"
        };

        System.out.println("========== NAME VALIDATION TEST ==========");

        for (String name : testNames) {

            boolean valid = isValidName(name);

            System.out.println(
                "[" + name + "] : " +
                (valid ? "VALID" : "INVALID")
            );
        }
    }
}
