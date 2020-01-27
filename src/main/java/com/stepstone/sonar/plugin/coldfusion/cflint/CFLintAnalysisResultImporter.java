/*
Copyright 2016 StepStone GmbH

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package com.stepstone.sonar.plugin.coldfusion.cflint;

import com.stepstone.sonar.plugin.coldfusion.ColdFusionPlugin;
import com.stepstone.sonar.plugin.coldfusion.cflint.xml.IssueAttributes;
import com.stepstone.sonar.plugin.coldfusion.cflint.xml.LocationAttributes;

import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class CFLintAnalysisResultImporter {

    private final FileSystem fs;
    private final SensorContext sensorContext;
    private XMLStreamReader stream;
    private final Logger logger = Loggers.get(CFLintAnalysisResultImporter.class);

    public CFLintAnalysisResultImporter(FileSystem fs, SensorContext sensorContext) {
        this.fs = fs;
        this.sensorContext = sensorContext;
    }

    public void parse(File file) throws IOException, XMLStreamException {

        try (FileReader reader = new FileReader(file)) {
            parse(reader);
        } catch (XMLStreamException | IOException e) {
            logger.error("", e);
            throw e;
        } finally {
            closeXmlStream();
        }
    }

    private void parse(FileReader reader) throws XMLStreamException {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.FALSE);
        factory.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.FALSE);
        stream = factory.createXMLStreamReader(reader);

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
            }
            else if (next == XMLStreamConstants.START_ELEMENT) {

                String tagName = stream.getLocalName();

                if ("location".equals(tagName)) {
                    LocationAttributes locationAttributes = new LocationAttributes(stream);

                    InputFile inputFile = fs.inputFile(fs.predicates().hasAbsolutePath(locationAttributes.getFile()));
                    createNewIssue(issueAttributes, locationAttributes, inputFile);
                }
            }
        }
    }

    private void createNewIssue(IssueAttributes issueAttributes, LocationAttributes locationAttributes, InputFile inputFile) {
        if(issueAttributes == null){
            logger.debug("Problem creating issue for file {} issueAttributes is null", inputFile);
        }
        if(locationAttributes == null){
            logger.debug("Problem creating issue for file {} locationAttributes is null", inputFile);
        }
        if(inputFile==null){
            logger.debug("Problem creating issue for file inputFile is null");
        }
        if(issueAttributes == null || locationAttributes == null || inputFile == null){
            return;
        }

        if(locationAttributes.getLine().isPresent() && locationAttributes.getLine().get()>inputFile.lines()){
            logger
                .error("Problem creating issue for file {}, issue is line {} but file has {} lines", inputFile, locationAttributes.getLine().get(), inputFile.lines());
            return;
        }

        logger.debug("create New Issue {} for file {}", issueAttributes, inputFile.filename());
        final NewIssue issue = sensorContext.newIssue();

        final NewIssueLocation issueLocation = issue.newLocation();
        issueLocation.on(inputFile);
        issueLocation.at(inputFile.selectLine(locationAttributes.getLine().get()));
        issueLocation.message(locationAttributes.getMessage().get());

        issue.forRule(RuleKey.of(ColdFusionPlugin.REPOSITORY_KEY, issueAttributes.getId().get()));
        issue.at(issueLocation);
        issue.save();
    }

    private void closeXmlStream() throws XMLStreamException {
        if (stream != null) {
            try {
                stream.close();
            } catch (Exception e) {
                throw e;
            }
        }
    }
}
