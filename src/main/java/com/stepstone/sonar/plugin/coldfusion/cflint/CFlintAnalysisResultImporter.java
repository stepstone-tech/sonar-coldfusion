package com.stepstone.sonar.plugin.coldfusion.cflint;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.stepstone.sonar.plugin.coldfusion.ColdFusionPlugin;
import com.stepstone.sonar.plugin.coldfusion.cflint.xml.IssueAttributes;
import com.stepstone.sonar.plugin.coldfusion.cflint.xml.LocationAttributes;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;
import org.sonar.api.rule.RuleKey;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class CFlintAnalysisResultImporter {

    private final FileSystem fs;
    private final SensorContext sensorContext;
    private XMLStreamReader stream;

    public CFlintAnalysisResultImporter(FileSystem fs, SensorContext sensorContext) {
        this.fs = fs;
        this.sensorContext = sensorContext;
    }

    public void parse(File file) {

        try (FileReader reader = new FileReader(file)) {
            parse(reader);
        } catch (XMLStreamException | IOException e) {
            throw Throwables.propagate(e);
        } finally {
            closeXmlStream();
        }
    }

    private void parse(FileReader reader) throws XMLStreamException {
        stream = XMLInputFactory.newInstance().createXMLStreamReader(reader);

        parse();
    }

    private void parse() throws XMLStreamException {
        while (stream.hasNext()) {
            if (stream.next() == XMLStreamConstants.START_ELEMENT) {
                String tagName = stream.getLocalName();

                if ("issue".equals(tagName)) {

                    handleIssueTag(new IssueAttributes(stream));
                }
            }
        }
    }

    private void handleIssueTag(IssueAttributes issueAttributes) throws XMLStreamException {
        while (stream.hasNext()) {
            int next = stream.next();

            if (next == XMLStreamConstants.END_ELEMENT && "issue".equals(stream.getLocalName())) {

                break;

            } else if (next == XMLStreamConstants.START_ELEMENT) {

                String tagName = stream.getLocalName();

                if ("location".equals(tagName)) {

                    LocationAttributes locationAttributes = new LocationAttributes(stream);
                    InputFile inputFile = fs.inputFile(fs.predicates().hasAbsolutePath(locationAttributes.getFile().get()));

                    createNewIssue(issueAttributes, locationAttributes, inputFile);
                }
            }
        }
    }

    private void createNewIssue(IssueAttributes issueAttributes, LocationAttributes locationAttributes, InputFile inputFile) {
        Preconditions.checkNotNull(issueAttributes);
        Preconditions.checkNotNull(locationAttributes);
        Preconditions.checkNotNull(inputFile);

        final NewIssue issue = sensorContext.newIssue();

        final NewIssueLocation issueLocation = issue.newLocation();
        issueLocation.on(inputFile);
        issueLocation.at(inputFile.selectLine(locationAttributes.getLine().get()));
        issueLocation.message(locationAttributes.getMessage().get());

        issue.forRule(RuleKey.of(ColdFusionPlugin.REPOSITORY_KEY, issueAttributes.getId().get()));
        issue.addLocation(issueLocation);
        issue.save();
    }

    private void closeXmlStream() {
        if (stream != null) {
            try {
                stream.close();
            } catch (Exception e) {
                throw Throwables.propagate(e);
            }
        }
    }
}
