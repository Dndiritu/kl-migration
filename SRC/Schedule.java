
import java.util.List;

public class Schedule implements Checkable {

    private final String name;
    private final String days;
    private final String time;

    private static final List<String> VALID_DAYS = List.of(
        "daily", "mon-fri", "mon-sat", "sun", "fri", "sat"
    );

    public Schedule(String name, String days, String time) {
        this.name = name;
        this.days = days;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public String getDays() {
        return days;
    }

    public String getTime() {
        return time;
    }

    @Override
    public boolean isValid() {
        return getValidationProblem() == null;
    }

    @Override
    public String getValidationProblem() {

        if (!VALID_DAYS.contains(days)) {
            return "Invalid schedule days: " + days;
        }

        if (time == null || !time.matches("\\d{2}:\\d{2}")) {
            return "Invalid time format: " + time;
        }

        int hours = Integer.parseInt(time.substring(0, 2));
        int minutes = Integer.parseInt(time.substring(3, 5));

        if (hours > 23 || minutes > 59) {
            return "Invalid 24-hour time: " + time;
        }

        return null;
    }
}
