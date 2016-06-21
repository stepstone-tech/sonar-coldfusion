package com.stepstone.sonar.plugin.coldfusion.cflint;

import com.stepstone.sonar.plugin.coldfusion.ColdFusionPlugin;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.rules.ActiveRule;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class CFlintConfigExporter {

    private final RulesProfile ruleProfile;
    private final String repositoryKey;

    public CFlintConfigExporter(RulesProfile ruleProfile) {
        this(ruleProfile, ColdFusionPlugin.REPOSITORY_KEY);
    }

    public CFlintConfigExporter(RulesProfile ruleProfile, String repositoryKey) {
        this.ruleProfile = ruleProfile;
        this.repositoryKey = repositoryKey;
    }

    public void save(File configFile) throws IOException, XMLStreamException {
        try (FileWriter writer = new FileWriter(configFile)) {
            save(writer);
        }
    }

    public void save(Writer writer) throws IOException, XMLStreamException {
        final XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
        XMLStreamWriter xtw = xmlOutputFactory.createXMLStreamWriter(writer);

        xtw.writeStartDocument();
        xtw.writeStartElement("config");

        for (ActiveRule activeRule : ruleProfile.getActiveRulesByRepository(repositoryKey)) {
            xtw.writeStartElement("includes");
            xtw.writeAttribute("code", activeRule.getRuleKey());
            xtw.writeEndElement();
        }

        xtw.writeEndElement();
        xtw.writeEndDocument();
        xtw.close();
    }
}
