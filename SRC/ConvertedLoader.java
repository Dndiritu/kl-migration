
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;

public class ConvertedLoader {

    public static XMLData load(String filePath)
            throws XMLLoaderException {

        try (Reader reader = new FileReader(filePath)) {

            Gson gson = new Gson();

            ConvertedExport data =
                gson.fromJson(reader, ConvertedExport.class);

            if (data == null) {
                throw new IllegalArgumentException(
                    "JSON file is empty or invalid."
                );
            }

            List<Job> jobs = readJobs(data);
            List<Machine> machines = readMachines(data);
            List<Schedule> schedules = new ArrayList<>();

            List<Chain> chains =
                readChains(data, jobs, schedules);

            // Read schedules belonging to standalone jobs.
            for (ProcessDefinition definition :
                    data.processDefinitions) {

                if (definition.submitFrame != null) {

                    schedules.add(new Schedule(
                        definition.name,
                        definition.submitFrame.days,
                        definition.submitFrame.startTime
                    ));
                }
            }

            return new XMLData(
                machines,
                chains,
                jobs,
                schedules
            );

        } catch (Exception e) {

            throw new XMLLoaderException(
                "Unable to read or parse converted JSON file: "
                + filePath + ". " + e.getMessage(),
                e
            );
        }
    }

    // Read jobs from processDefinitions
    private static List<Job> readJobs(ConvertedExport data) {

        List<Job> jobs = new ArrayList<>();

        for (ProcessDefinition definition :
                data.processDefinitions) {

            String name = definition.name;
            String type = definition.definitionType;
            String owner = definition.runAs;
            String machine = definition.server;
            String description = definition.description;

            // Read parameters
            List<JobParameter> parameters =
                new ArrayList<>();

            if (definition.parameters != null) {

                for (Parameter parameter :
                        definition.parameters) {

                    parameters.add(new JobParameter(
                        parameter.name,
                        parameter.value
                    ));
                }
            }

            // Read dependencies and conditions
            List<JobDependency> dependencies =
                new ArrayList<>();

            if (definition.preconditions != null) {

                for (Precondition precondition :
                        definition.preconditions) {

                    dependencies.add(new JobDependency(
                        precondition.waitFor,
                        convertCondition(precondition.onStatus)
                    ));
                }
            }

            // Determine the job's chain.
            // Chain membership is defined by chains[].steps.
            String chainName =
                findChainName(data, definition.name);

            if ("OS".equals(type)) {

                jobs.add(new CommandJob(
                    name,
                    chainName,
                    owner,
                    machine,
                    description,
                    dependencies,
                    parameters,
                    definition.script
                ));

            } else if ("FILE_TRANSFER".equals(type)) {

                jobs.add(new FileTransferJob(
                    name,
                    chainName,
                    owner,
                    machine,
                    description,
                    dependencies,
                    parameters,
                    definition.sourceFile,
                    definition.targetFile
                ));

            } else {

                throw new IllegalArgumentException(
                    "Unknown definition type '" + type +
                    "' for job: " + name
                );
            }
        }

        return jobs;
    }

    // Convert NewPlatform status into LegacySched condition
    private static String convertCondition(String status) {

        if (status == null) {
            return null;
        }

        if ("Completed".equalsIgnoreCase(status)) {
            return "success";
        }

        if ("Error".equalsIgnoreCase(status)) {
            return "failure";
        }

        return status;
    }

    // Find the chain to which a job belongs
    private static String findChainName(
            ConvertedExport data, String jobName) {

        for (ConvertedChain chain : data.chains) {

            if (chain.steps != null &&
                chain.steps.contains(jobName)) {

                return chain.name;
            }
        }

        return "";
    }

    // Read unique machines from process definitions
    private static List<Machine> readMachines(
            ConvertedExport data) {

        List<Machine> machines = new ArrayList<>();
        Set<String> machineNames = new LinkedHashSet<>();

        for (ProcessDefinition definition :
                data.processDefinitions) {

            if (definition.server != null &&
                !definition.server.isBlank()) {

                machineNames.add(definition.server);
            }
        }

        for (String machineName : machineNames) {

            machines.add(new Machine(
                machineName,
                null
            ));
        }

        return machines;
    }

    // Read chains and preserve their job execution order
    private static List<Chain> readChains(
            ConvertedExport data,
            List<Job> jobs,
            List<Schedule> schedules) {

        List<Chain> chains = new ArrayList<>();

        Map<String, Job> jobsByName =
            new LinkedHashMap<>();

        for (Job job : jobs) {
            jobsByName.putIfAbsent(job.getName(), job);
        }

        for (ConvertedChain convertedChain : data.chains) {

            String chainName = convertedChain.name;

            String days = null;
            String time = null;

            if (convertedChain.submitFrame != null) {

                days = convertedChain.submitFrame.days;
                time = convertedChain.submitFrame.startTime;

                schedules.add(new Schedule(
                    chainName,
                    days,
                    time
                ));
            }

            List<Job> chainJobs = new ArrayList<>();

            if (convertedChain.steps != null) {

                for (String stepName :
                        convertedChain.steps) {

                    Job job = jobsByName.get(stepName);

                    if (job != null) {
                        chainJobs.add(job);
                    }
                }
            }

            chains.add(new Chain(
                chainName,
                days,
                time,
                chainJobs
            ));
        }

        return chains;
    }

    // ==========================================
    // Gson JSON MODEL CLASSES
    // ==========================================

    private static class ConvertedExport {

        String platform;
        String sourceSystem;
        String customer;

        List<ProcessDefinition> processDefinitions;
        List<ConvertedChain> chains;
    }

    private static class ProcessDefinition {

        String name;
        String definitionType;
        String runAs;
        String server;
        String description;

        String script;
        String sourceFile;
        String targetFile;

        List<Parameter> parameters;
        List<Precondition> preconditions;

        SubmitFrame submitFrame;
    }

    private static class Parameter {

        String name;
        String value;
    }

    private static class Precondition {

        String waitFor;
        String onStatus;
    }

    private static class ConvertedChain {

        String name;
        SubmitFrame submitFrame;
        List<String> steps;
    }

    private static class SubmitFrame {

        String days;
        String startTime;
    }
}

