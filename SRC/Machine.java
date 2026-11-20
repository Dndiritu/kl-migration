
public class Machine {

    private final String name;
    private final String os;

    public Machine(String name, String os) {
        this.name = name;
        this.os = os;
    }

    public String getName() {
        return name;
    }

    public String getOs() {
        return os;
    }

    @Override
    public String toString() {
        return "Machine{name='" + name + "', os='" + os + "'}";
    }
}
