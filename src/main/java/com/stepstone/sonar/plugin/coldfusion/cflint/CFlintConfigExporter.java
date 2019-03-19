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
import org.sonar.api.batch.rule.ActiveRule;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;

public class CFlintConfigExporter {

    private final Collection<ActiveRule> ruleProfiles;
    private final String repositoryKey;

    public CFlintConfigExporter(Collection ruleProfile) {
        this(ruleProfile, ColdFusionPlugin.REPOSITORY_KEY);
    }

    public CFlintConfigExporter(Collection ruleProfile, String repositoryKey) {
        this.ruleProfiles = ruleProfile;
        this.repositoryKey = repositoryKey;
    }

    public void save(File configFile) throws IOException, XMLStreamException {
        try (FileWriter writer = new FileWriter(configFile)) {
            save(writer);
        }
    }

    public void save(Writer writer) throws IOException, XMLStreamException {
        final XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
        XMLStreamWriter xtw=null;
        try {
            xtw = xmlOutputFactory.createXMLStreamWriter(writer);

            xtw.writeStartDocument();
            xtw.writeStartElement("config");

            for (ActiveRule activeRule : ruleProfiles) {
                xtw.writeStartElement("includes");
                xtw.writeAttribute("code", activeRule.ruleKey().toString());
                xtw.writeEndElement();
            }

            xtw.writeEndElement();
            xtw.writeEndDocument();
        } finally {
            if(xtw!=null) {
                xtw.close();
            }
        }

    }
}
