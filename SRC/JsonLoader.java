
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonLoader {

    public static XMLData load(String filePath)
            throws XMLLoaderException {

        try (Reader reader = new FileReader(filePath)) {

            JsonObject root = JsonParser.parseReader(reader)
                                        .getAsJsonObject();

            List<Machine> machines = readMachines(root);
            List<Job> jobs = readJobs(root);

            List<Schedule> schedules = new ArrayList<>();
            List<Chain> chains = readChains(root, jobs, schedules);

            // Read schedules belonging to standalone jobs.
            JsonArray jobArray = root.getAsJsonArray("jobs");

            for (JsonElement element : jobArray) {
                JsonObject jobObject = element.getAsJsonObject();

                String chain = getString(jobObject, "chain");

                if (chain == null || chain.isBlank()) {

                    JsonObject schedule =
                        getObject(jobObject, "schedule");

                    if (schedule != null) {
                        schedules.add(new Schedule(
                            getString(jobObject, "name"),
                            getString(schedule, "days"),
                            getString(schedule, "time")
                        ));
                    }
                }
            }

            return new XMLData(
                machines, chains, jobs, schedules
            );

        } catch (Exception e) {
            throw new XMLLoaderException(
                "Unable to read or parse JSON file: " +
                filePath + ". " + e.getMessage(), e
            );
        }
    }

    private static List<Machine> readMachines(JsonObject root) {

        List<Machine> machines = new ArrayList<>();
        JsonArray machineArray = root.getAsJsonArray("machines");

        for (JsonElement element : machineArray) {
            JsonObject machine = element.getAsJsonObject();

            machines.add(new Machine(
                getString(machine, "name"),
                getString(machine, "os")
            ));
        }

        return machines;
    }

    private static List<Job> readJobs(JsonObject root) {

        List<Job> jobs = new ArrayList<>();
        JsonArray jobArray = root.getAsJsonArray("jobs");

        for (JsonElement element : jobArray) {

            JsonObject job = element.getAsJsonObject();

            String name = getString(job, "name");
            String type = getString(job, "type");
            String chain = getString(job, "chain");
            String owner = getString(job, "owner");
            String machine = getString(job, "machine");
            String description = getString(job, "description");

            List<String> dependencies = new ArrayList<>();

            JsonArray dependencyArray =
                job.getAsJsonArray("dependsOn");

            if (dependencyArray != null) {
                for (JsonElement dependency : dependencyArray) {
                    dependencies.add(dependency.getAsString());
                }
            }

            if ("CMD".equals(type)) {

                jobs.add(new CommandJob(
                    name,
                    chain,
                    owner,
                    machine,
                    description,
                    dependencies,
                    getString(job, "command")
                ));

            } else if ("FT".equals(type)) {

                jobs.add(new FileTransferJob(
                    name,
                    chain,
                    owner,
                    machine,
                    description,
                    dependencies,
                    getString(job, "source"),
                    getString(job, "target")
                ));
            }
        }

        return jobs;
    }

    private static List<Chain> readChains(
            JsonObject root,
            List<Job> jobs,
            List<Schedule> schedules) {

        List<Chain> chains = new ArrayList<>();

        Map<String, List<Job>> jobsByChain =
            new LinkedHashMap<>();

        for (Job job : jobs) {
            jobsByChain
                .computeIfAbsent(
                    job.getChain(), key -> new ArrayList<>()
                )
                .add(job);
        }

        JsonArray chainArray = root.getAsJsonArray("chains");

        for (JsonElement element : chainArray) {

            JsonObject chain = element.getAsJsonObject();

            String name = getString(chain, "name");

            JsonObject schedule =
                getObject(chain, "schedule");

            String days = getString(schedule, "days");
            String time = getString(schedule, "time");

            schedules.add(new Schedule(name, days, time));

            List<Job> chainJobs =
                jobsByChain.getOrDefault(
                    name, new ArrayList<>()
                );

            chains.add(new Chain(
                name, days, time, chainJobs
            ));
        }

        return chains;
    }

    private static String getString(
            JsonObject object, String property) {

        if (object == null ||
            !object.has(property) ||
            object.get(property).isJsonNull()) {
            return null;
        }

        return object.get(property).getAsString();
    }

    private static JsonObject getObject(
            JsonObject object, String property) {

        if (object == null ||
            !object.has(property) ||
            object.get(property).isJsonNull()) {
            return null;
        }

        return object.getAsJsonObject(property);
    }
}
