
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ConversionCheck {

    private static final List<String[]> mismatches =
        new ArrayList<>();

    public static void main(String[] args) {

        try {

            XMLData source =
                XMLLoader.load("customer_export.xml");

            XMLData converted =
                ConvertedLoader.load("converted_export.json");

            mismatches.clear();

            compareJobs(
                source.getJobs(),
                converted.getJobs()
            );

            compareChains(
                source.getChains(),
                converted.getChains()
            );

            compareSchedules(
                source.getSchedules(),
                converted.getSchedules()
            );

            writeCsv("conversion_mismatches.csv");

            System.out.println("===== CONVERSION CHECK =====");

            System.out.println(
                "Source jobs: " + source.getJobs().size()
            );

            System.out.println(
                "Converted jobs: " + converted.getJobs().size()
            );

            System.out.println(
                "Source chains: " + source.getChains().size()
            );

            System.out.println(
                "Converted chains: " + converted.getChains().size()
            );

            System.out.println(
                "Source schedules: " + source.getSchedules().size()
            );

            System.out.println(
                "Converted schedules: " + converted.getSchedules().size()
            );

            System.out.println(
                "Mismatches found: " + mismatches.size()
            );

            System.out.println(
                "Report saved: conversion_mismatches.csv"
            );

        } catch (XMLLoaderException | IOException e) {

            System.out.println(
                "Conversion check failed: " + e.getMessage()
            );
        }
    }

    // Compare all jobs, preserving duplicate occurrences.
    private static void compareJobs(
            List<Job> sourceJobs,
            List<Job> convertedJobs) {

        Map<String, List<Job>> convertedMap =
            new HashMap<>();

        // Group converted jobs by name.
        for (Job job : convertedJobs) {

            convertedMap
                .computeIfAbsent(
                    job.getName(),
                    k -> new ArrayList<>()
                )
                .add(job);
        }

        Map<String, Integer> matchedCounts =
            new HashMap<>();

        // Compare each source job with the corresponding
        // occurrence in the converted JSON.
        for (Job source : sourceJobs) {

            String name = source.getName();

            int occurrence =
                matchedCounts.getOrDefault(name, 0);

            matchedCounts.put(name, occurrence + 1);

            List<Job> matches = convertedMap.get(name);

            if (matches == null ||
                occurrence >= matches.size()) {

                addMismatch(
                    "Job",
                    name,
                    "Missing job",
                    "Present (occurrence " + (occurrence + 1) + ")",
                    "Missing"
                );

                continue;
            }

            Job converted = matches.get(occurrence);

            compareField(
                name,
                "Type",
                source.getType(),
                converted.getType()
            );

            compareField(
                name,
                "Chain",
                source.getChain(),
                converted.getChain()
            );

            compareField(
                name,
                "Owner",
                source.getOwner(),
                converted.getOwner()
            );

            compareField(
                name,
                "Machine",
                source.getMachine(),
                converted.getMachine()
            );

            compareField(
                name,
                "Description",
                source.getDescription(),
                converted.getDescription()
            );

            // Compare command paths.
            if (source instanceof CommandJob &&
                converted instanceof CommandJob) {

                CommandJob s = (CommandJob) source;
                CommandJob c = (CommandJob) converted;

                compareField(
                    name,
                    "Command",
                    s.getCommand(),
                    c.getCommand()
                );
            }

            // Compare file transfer paths.
            if (source instanceof FileTransferJob &&
                converted instanceof FileTransferJob) {

                FileTransferJob s = (FileTransferJob) source;
                FileTransferJob c = (FileTransferJob) converted;

                compareField(
                    name,
                    "Source file",
                    s.getSource(),
                    c.getSource()
                );

                compareField(
                    name,
                    "Target file",
                    s.getTarget(),
                    c.getTarget()
                );
            }

            // Compare dependency names and conditions.
            compareField(
                name,
                "Dependencies and conditions",
                dependencyValues(source),
                dependencyValues(converted)
            );

            // Compare parameters.
            compareField(
                name,
                "Parameters",
                parameterValues(source),
                parameterValues(converted)
            );
        }

        // Find additional jobs in converted JSON.
        for (Map.Entry<String, List<Job>> entry :
                convertedMap.entrySet()) {

            String name = entry.getKey();

            int convertedCount = entry.getValue().size();

            int sourceCount =
                matchedCounts.getOrDefault(name, 0);

            for (int i = sourceCount;
                 i < convertedCount;
                 i++) {

                addMismatch(
                    "Job",
                    name,
                    "Additional job",
                    "Missing",
                    "Present (occurrence " + (i + 1) + ")"
                );
            }
        }
    }

    // Convert dependency details into comparable text.
    private static String dependencyValues(Job job) {

        return job.getDependencyDetails()
            .stream()
            .map(d ->
                d.getJobName() + " [" + d.getCondition() + "]"
            )
            .sorted()
            .collect(Collectors.joining("; "));
    }

    // Convert parameters into comparable text.
    private static String parameterValues(Job job) {

        return job.getParameters()
            .stream()
            .map(p ->
                p.getName() + "=" + p.getValue()
            )
            .sorted()
            .collect(Collectors.joining("; "));
    }

    // Compare chain definitions and job membership.
    private static void compareChains(
            List<Chain> sourceChains,
            List<Chain> convertedChains) {

        Map<String, Chain> sourceMap =
            indexChains(sourceChains);

        Map<String, Chain> convertedMap =
            indexChains(convertedChains);

        for (Chain source : sourceChains) {

            String name = source.getName();

            Chain converted = convertedMap.get(name);

            if (converted == null) {

                addMismatch(
                    "Chain",
                    name,
                    "Missing chain",
                    "Present",
                    "Missing"
                );

                continue;
            }

            compareField(
                name,
                "Chain schedule days",
                source.getDays(),
                converted.getDays()
            );

            compareField(
                name,
                "Chain schedule time",
                source.getTime(),
                converted.getTime()
            );

            // Compare only jobs present in both chains.
            compareField(
                name,
                "Chain job membership",
                commonChainJobNames(source, converted, true),
                commonChainJobNames(source, converted, false)
            );
        }

        // Find additional chains in converted JSON.
        for (Chain converted : convertedChains) {

            if (!sourceMap.containsKey(converted.getName())) {

                addMismatch(
                    "Chain",
                    converted.getName(),
                    "Additional chain",
                    "Missing",
                    "Present"
                );
            }
        }
    }

    private static Map<String, Chain> indexChains(
            List<Chain> chains) {

        Map<String, Chain> map = new HashMap<>();

        for (Chain chain : chains) {

            map.putIfAbsent(
                chain.getName(),
                chain
            );
        }

        return map;
    }

    // Return job names common to both chains.
    // This avoids reporting missing jobs twice:
    // once as a job mismatch and again as a chain mismatch.
    private static String commonChainJobNames(
            Chain first,
            Chain second,
            boolean sourceSide) {

        List<String> firstNames = first.getJobs()
            .stream()
            .map(Job::getName)
            .collect(Collectors.toList());

        List<String> secondNames = second.getJobs()
            .stream()
            .map(Job::getName)
            .collect(Collectors.toList());

        List<String> common = new ArrayList<>();

        for (String name : firstNames) {

            if (secondNames.contains(name)) {
                common.add(name);
            }
        }

        return common.stream()
            .sorted()
            .collect(Collectors.joining("; "));
    }

    // Compare all schedules, including standalone job schedules.
    private static void compareSchedules(
            List<Schedule> sourceSchedules,
            List<Schedule> convertedSchedules) {

        Map<String, Schedule> sourceMap =
            indexSchedules(sourceSchedules);

        Map<String, Schedule> convertedMap =
            indexSchedules(convertedSchedules);

        for (Schedule source : sourceSchedules) {

            String name = source.getName();

            Schedule converted = convertedMap.get(name);

            if (converted == null) {

                addMismatch(
                    "Schedule",
                    name,
                    "Missing schedule",
                    "Present",
                    "Missing"
                );

                continue;
            }

            compareField(
                name,
                "Schedule days",
                source.getDays(),
                converted.getDays()
            );

            compareField(
                name,
                "Schedule time",
                source.getTime(),
                converted.getTime()
            );
        }

        // Find additional schedules.
        for (Schedule converted : convertedSchedules) {

            if (!sourceMap.containsKey(converted.getName())) {

                addMismatch(
                    "Schedule",
                    converted.getName(),
                    "Additional schedule",
                    "Missing",
                    "Present"
                );
            }
        }
    }

    private static Map<String, Schedule> indexSchedules(
            List<Schedule> schedules) {

        Map<String, Schedule> map = new HashMap<>();

        for (Schedule schedule : schedules) {

            map.putIfAbsent(
                schedule.getName(),
                schedule
            );
        }

        return map;
    }

    // Compare a single field.
    private static void compareField(
            String name,
            String field,
            Object source,
            Object converted) {

        if (!Objects.equals(source, converted)) {

            addMismatch(
                "Record",
                name,
                field,
                String.valueOf(source),
                String.valueOf(converted)
            );
        }
    }

    // Add a mismatch to the report.
    private static void addMismatch(
            String recordType,
            String recordName,
            String difference,
            String sourceValue,
            String convertedValue) {

        mismatches.add(new String[] {
            recordType,
            recordName,
            difference,
            sourceValue,
            convertedValue
        });
    }

    // Write the CSV report.
    private static void writeCsv(String fileName)
            throws IOException {

        try (BufferedWriter writer =
                Files.newBufferedWriter(
                    Paths.get(fileName),
                    StandardCharsets.UTF_8
                )) {

            writer.write(
                "Record Type,Record Name,Difference," +
                "Source Value,Converted Value"
            );

            writer.newLine();

            for (String[] row : mismatches) {

                for (int i = 0; i < row.length; i++) {

                    if (i > 0) {
                        writer.write(",");
                    }

                    writer.write(
                        escapeCsv(row[i])
                    );
                }

                writer.newLine();
            }
        }
    }

    // Escape values for valid CSV formatting.
    private static String escapeCsv(String value) {

        if (value == null) {
            value = "";
        }

        return "\"" +
            value.replace("\"", "\"\"") +
            "\"";
    }
}
