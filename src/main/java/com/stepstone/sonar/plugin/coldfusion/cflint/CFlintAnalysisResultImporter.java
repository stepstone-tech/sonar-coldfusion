package com.stepstone.sonar.plugin.coldfusion.cflint;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.stepstone.sonar.plugin.coldfusion.ColdFusionPlugin;
import com.stepstone.sonar.plugin.coldfusion.cflint.xml.IssueAttributes;
import com.stepstone.sonar.plugin.coldfusion.cflint.xml.LocationAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.component.ResourcePerspectives;
import org.sonar.api.issue.Issuable;
import org.sonar.api.rule.RuleKey;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class CFlintAnalysisResultImporter {

    private final Logger log = LoggerFactory.getLogger(CFlintAnalysisResultImporter.class);
    private final FileSystem fs;
    private final ResourcePerspectives perspectives;
    private XMLStreamReader stream;

    public CFlintAnalysisResultImporter(FileSystem fs, ResourcePerspectives perspectives) {
        this.fs = fs;
        this.perspectives = perspectives;
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
                    Preconditions.checkNotNull(inputFile);
                    Issuable issuable = getIssuable(inputFile);

                    Issuable.IssueBuilder builder = issuable.newIssueBuilder();

                    builder.ruleKey(RuleKey.of(ColdFusionPlugin.REPOSITORY_KEY, issueAttributes.getId().get()));
                    builder.line(locationAttributes.getLine().get());
                    builder.message(locationAttributes.getMessage().get());

                    issuable.addIssue(builder.build());
                }
            }
        }
    }

    private Issuable getIssuable(InputFile inputFile) {
        try {
            Issuable issuable = perspectives.as(Issuable.class, inputFile);
            Preconditions.checkNotNull(issuable);
            return issuable;
        } catch (NullPointerException e) {
            log.warn("File {} isn't in sonars repository", inputFile);
            throw e;
        }
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
