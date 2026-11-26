
import java.util.List;

public class FileTransferJob extends Job {

    private final String source;
    private final String target;

    // Existing constructor
    public FileTransferJob(String name, String chain, String owner,
                           String machine, String description,
                           List<String> dependencies,
                           String source, String target) {

        super(name, chain, owner, machine, description, dependencies);
        this.source = source;
        this.target = target;
    }

    // Extended constructor
    public FileTransferJob(String name, String chain, String owner,
                           String machine, String description,
                           List<JobDependency> dependencies,
                           List<JobParameter> parameters,
                           String source, String target) {

        super(name, chain, owner, machine, description,
              dependencies, parameters);
        this.source = source;
        this.target = target;
    }

    public String getSource() {
        return source;
    }

    public String getTarget() {
        return target;
    }

    @Override
    public String getType() {
        return "FT";
    }

    @Override
    public String getTypeSpecificProblem() {
        if (source == null || source.isBlank()) {
            return "File transfer job has no source";
        }

        if (target == null || target.isBlank()) {
            return "File transfer job has no target";
        }

        return null;
    }
}
