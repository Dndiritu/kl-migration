public class Schedule implements Checkable {

    private final String name;
    private final String days;
    private final String time;

    private static final String[] VALID_DAYS = {
        "daily", "mon-fri", "mon-sat", "sun", "fri", "sat"
    };

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

        // Check the day value
        boolean validDay = false;

        for (String valid : VALID_DAYS) {
            if (valid.equals(days)) {
                validDay = true;
                break;
            }
        }

        if (!validDay) {
            return false;
        }

        // Check the time format
        if (time == null || !time.matches("\\d{2}:\\d{2}")) {
            return false;
        }

        // Convert hours and minutes to integers
        String[] parts = time.split(":");

        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);

        return hours >= 0 && hours <= 23
                && minutes >= 0 && minutes <= 59;
    }

    @Override
    public String getValidationProblem() {

        boolean validDay = false;

        for (String valid : VALID_DAYS) {
            if (valid.equals(days)) {
                validDay = true;
                break;
            }
        }

        if (!validDay) {
            return "Invalid schedule days: " + days;
        }

        if (time == null || !time.matches("\\d{2}:\\d{2}")) {
            return "Invalid time format: " + time;
        }

        String[] parts = time.split(":");

        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);

        if (hours < 0 || hours > 23 || minutes < 0 || minutes > 59) {
            return "Invalid 24-hour time: " + time;
        }

        return null;
    }
}