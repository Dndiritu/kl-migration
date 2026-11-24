
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class Fixer {

    private static final String INPUT_FILE = "customer_export.xml";
    private static final String OUTPUT_FILE = "customer_export_fixed.xml";
    private static final String CHANGE_LOG_FILE = "change_log.csv";

    public static void main(String[] args) {

        String inputFile = args.length > 0 ? args[0] : INPUT_FILE;
        String outputFile = args.length > 1 ? args[1] : OUTPUT_FILE;
        String changeLogFile = args.length > 2 ? args[2] : CHANGE_LOG_FILE;

        try {
            Document document = loadDocument(inputFile);

            List<Element> jobs = getJobs(document);

            System.out.println("Jobs found: " + jobs.size());

            Map<String, String> renameMap = buildRenameMap(jobs);

            System.out.println("\nProposed renames:");

            for (Map.Entry<String, String> entry : renameMap.entrySet()) {
                System.out.println(
                    entry.getKey() + " -> " + entry.getValue()
                );
            }

            checkForCollisions(jobs, renameMap);

            int dependenciesUpdated =
                applyRenames(document, jobs, renameMap);

            writeXml(document, outputFile);
            writeChangeLog(renameMap, changeLogFile);

            System.out.println("\nFixing completed successfully.");
            System.out.println("Jobs scanned: " + jobs.size());
            System.out.println("Jobs renamed: " + renameMap.size());
            System.out.println("Dependencies updated: " + dependenciesUpdated);
            System.out.println("Fixed XML: " + outputFile);
            System.out.println("Change log: " + changeLogFile);

        } catch (Exception e) {
            System.err.println("\nFixing failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static Document loadDocument(String fileName) throws Exception {

        DocumentBuilderFactory factory =
            DocumentBuilderFactory.newInstance();

        factory.setFeature(
            XMLConstants.FEATURE_SECURE_PROCESSING, true
        );

        factory.setFeature(
            "http://apache.org/xml/features/disallow-doctype-decl", true
        );

        factory.setFeature(
            "http://xml.org/sax/features/external-general-entities", false
        );

        factory.setFeature(
            "http://xml.org/sax/features/external-parameter-entities", false
        );

        factory.setXIncludeAware(false);
        factory.setExpandEntityReferences(false);

        DocumentBuilder builder = factory.newDocumentBuilder();

        return builder.parse(new File(fileName));
    }

    private static List<Element> getJobs(Document document) {

        List<Element> jobs = new ArrayList<>();

        NodeList nodeList = document.getElementsByTagName("job");

        for (int i = 0; i < nodeList.getLength(); i++) {
            jobs.add((Element) nodeList.item(i));
        }

        return jobs;
    }

    private static Map<String, String> buildRenameMap(
        List<Element> jobs
    ) {

        Map<String, String> renameMap = new LinkedHashMap<>();

        for (Element job : jobs) {

            String oldName = job.getAttribute("name");
            String newName = normalizeName(oldName);

            if (!oldName.equals(newName)) {

                if (renameMap.containsKey(oldName)) {
                    throw new IllegalStateException(
                        "Duplicate malformed original name found: "
                        + oldName + ". Manual review is required."
                    );
                }

                renameMap.put(oldName, newName);
            }
        }

        return renameMap;
    }

    private static String normalizeName(String name) {

        String normalized = name.trim().toUpperCase(Locale.ROOT);

        normalized = normalized.replaceAll("[^A-Z0-9]+", "_");
        normalized = normalized.replaceAll("_+", "_");
        normalized = normalized.replaceAll("^_+|_+$", "");

        if (normalized.isEmpty()) {
            normalized = "JOB_UNNAMED";
        }

        if (!Character.isLetter(normalized.charAt(0))) {
            normalized = "JOB_" + normalized;
        }

        if (!normalized.matches("[A-Z][A-Z0-9_]*")) {
            throw new IllegalArgumentException(
                "Generated job name is invalid: " + normalized
            );
        }

        return normalized;
    }

    private static void checkForCollisions(
        List<Element> jobs,
        Map<String, String> renameMap
    ) {

        Set<String> existingNames = new LinkedHashSet<>();
        Set<String> proposedNames = new LinkedHashSet<>();

        for (Element job : jobs) {
            existingNames.add(job.getAttribute("name"));
        }

        for (Map.Entry<String, String> entry : renameMap.entrySet()) {

            String proposedName = entry.getValue();

            if (existingNames.contains(proposedName)) {
                throw new IllegalStateException(
                    "Name collision detected for '"
                    + proposedName + "'. Manual review is required."
                );
            }

            if (!proposedNames.add(proposedName)) {
                throw new IllegalStateException(
                    "Multiple jobs produce the same target name '"
                    + proposedName + "'. Manual review is required."
                );
            }
        }
    }

    private static int applyRenames(
        Document document,
        List<Element> jobs,
        Map<String, String> renameMap
    ) {

        for (Element job : jobs) {

            String oldName = job.getAttribute("name");

            if (renameMap.containsKey(oldName)) {
                job.setAttribute("name", renameMap.get(oldName));
            }
        }

        NodeList dependencies =
            document.getElementsByTagName("dependsOn");

        int updatedCount = 0;

        for (int i = 0; i < dependencies.getLength(); i++) {

            Element dependency = (Element) dependencies.item(i);

            String oldDependencyName =
                dependency.getTextContent().trim();

            if (renameMap.containsKey(oldDependencyName)) {

                dependency.setTextContent(
                    renameMap.get(oldDependencyName)
                );

                updatedCount++;
            }
        }

        return updatedCount;
    }

    private static void writeXml(
        Document document,
        String fileName
    ) throws Exception {

        TransformerFactory transformerFactory =
            TransformerFactory.newInstance();

        transformerFactory.setFeature(
            XMLConstants.FEATURE_SECURE_PROCESSING, true
        );

        Transformer transformer =
            transformerFactory.newTransformer();

        transformer.setOutputProperty(
            OutputKeys.INDENT, "yes"
        );

        transformer.setOutputProperty(
            OutputKeys.ENCODING, "UTF-8"
        );

        transformer.transform(
            new DOMSource(document),
            new StreamResult(new File(fileName))
        );
    }

    private static void writeChangeLog(
        Map<String, String> renameMap,
        String fileName
    ) throws Exception {

        try (FileWriter writer = new FileWriter(fileName)) {

            writer.append("old_name,new_name\n");

            for (Map.Entry<String, String> entry : renameMap.entrySet()) {

                writer.append(csvEscape(entry.getKey()));
                writer.append(",");
                writer.append(csvEscape(entry.getValue()));
                writer.append("\n");
            }
        }
    }

    private static String csvEscape(String value) {

        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
}
