
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XMLLoader {

    public static XMLData load(String filePath)
            throws XMLLoaderException {

        File file = new File(filePath);

        if (!file.exists() || !file.isFile()) {
            throw new XMLLoaderException(
                "XML file not found: " + filePath
            );
        }

        try {
            DocumentBuilderFactory factory =
                DocumentBuilderFactory.newInstance();

            factory.setFeature(
                XMLConstants.FEATURE_SECURE_PROCESSING, true
            );
            factory.setFeature(
                "http://apache.org/xml/features/disallow-doctype-decl",
                true
            );
            factory.setFeature(
                "http://xml.org/sax/features/external-general-entities",
                false
            );
            factory.setFeature(
                "http://xml.org/sax/features/external-parameter-entities",
                false
            );
            factory.setXIncludeAware(false);
            factory.setExpandEntityReferences(false);

            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(file);
            document.getDocumentElement().normalize();

            Element root = document.getDocumentElement();

            List<Machine> machines = readMachines(root);
            List<Job> jobs = readJobs(root);
            List<Schedule> schedules = new ArrayList<>();
            List<Chain> chains = readChains(root, jobs, schedules);

            // Read schedules belonging to standalone jobs.
            Element jobsElement = getFirstChild(root, "jobs");
            NodeList jobNodes = jobsElement.getElementsByTagName("job");

            for (int i = 0; i < jobNodes.getLength(); i++) {
                Element jobElement = (Element) jobNodes.item(i);

                if (jobElement.getAttribute("chain").isBlank()) {
                    Element scheduleElement =
                        getFirstChild(jobElement, "schedule");

                    if (scheduleElement != null) {
                        schedules.add(new Schedule(
                            jobElement.getAttribute("name"),
                            scheduleElement.getAttribute("days"),
                            scheduleElement.getAttribute("time")
                        ));
                    }
                }
            }

            return new XMLData(
                machines, chains, jobs, schedules
            );

        } catch (XMLLoaderException e) {
            throw e;
        } catch (Exception e) {
            throw new XMLLoaderException(
                "Unable to read or parse XML file: " +
                filePath + ". " + e.getMessage(), e
            );
        }
    }

    private static List<Machine> readMachines(Element root) {
        List<Machine> machines = new ArrayList<>();

        Element machinesElement =
            getFirstChild(root, "machines");

        NodeList nodes =
            machinesElement.getElementsByTagName("machine");

        for (int i = 0; i < nodes.getLength(); i++) {
            Element element = (Element) nodes.item(i);

            machines.add(new Machine(
                element.getAttribute("name"),
                element.getAttribute("os")
            ));
        }

        return machines;
    }

    private static List<Job> readJobs(Element root)
            throws XMLLoaderException {

        List<Job> jobs = new ArrayList<>();

        Element jobsElement = getFirstChild(root, "jobs");
        NodeList nodes = jobsElement.getElementsByTagName("job");

        for (int i = 0; i < nodes.getLength(); i++) {
            Element element = (Element) nodes.item(i);

            String name = element.getAttribute("name");
            String type = element.getAttribute("type");
            String chain = element.getAttribute("chain");
            String owner = element.getAttribute("owner");
            String machine = element.getAttribute("machine");

            String description =
                getChildText(element, "description");

            List<String> dependencies = new ArrayList<>();

            NodeList dependencyNodes =
                element.getElementsByTagName("dependsOn");

            for (int j = 0; j < dependencyNodes.getLength(); j++) {
                dependencies.add(
                    dependencyNodes.item(j).getTextContent().trim()
                );
            }

            if ("CMD".equals(type)) {
                jobs.add(new CommandJob(
                    name, chain, owner, machine, description,
                    dependencies,
                    getChildText(element, "command")
                ));

            } else if ("FT".equals(type)) {
                jobs.add(new FileTransferJob(
                    name, chain, owner, machine, description,
                    dependencies,
                    getChildText(element, "source"),
                    getChildText(element, "target")
                ));

            } else {
                throw new XMLLoaderException(
                    "Unknown job type '" + type +
                    "' for job: " + name
                );
            }
        }

        return jobs;
    }

    private static List<Chain> readChains(
            Element root,
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

        Element chainsElement =
            getFirstChild(root, "chains");

        NodeList nodes =
            chainsElement.getElementsByTagName("chain");

        for (int i = 0; i < nodes.getLength(); i++) {
            Element element = (Element) nodes.item(i);

            String name = element.getAttribute("name");

            Element scheduleElement =
                getFirstChild(element, "schedule");

            String days = scheduleElement.getAttribute("days");
            String time = scheduleElement.getAttribute("time");

            schedules.add(new Schedule(name, days, time));

            List<Job> chainJobs =
                jobsByChain.getOrDefault(name, new ArrayList<>());

            chains.add(new Chain(name, days, time, chainJobs));
        }

        return chains;
    }

    private static Element getFirstChild(
            Element parent, String tagName) {

        NodeList nodes = parent.getElementsByTagName(tagName);

        for (int i = 0; i < nodes.getLength(); i++) {
            Element element = (Element) nodes.item(i);

            if (element.getParentNode() == parent) {
                return element;
            }
        }

        return null;
    }

    private static String getChildText(
            Element parent, String tagName) {

        Element child = getFirstChild(parent, tagName);

        if (child == null) {
            return null;
        }

        return child.getTextContent().trim();
    }
}
