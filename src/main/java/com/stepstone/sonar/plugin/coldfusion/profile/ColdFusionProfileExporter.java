package com.stepstone.sonar.plugin.coldfusion.profile;

import com.google.common.base.Throwables;
import com.stepstone.sonar.plugin.coldfusion.ColdFusionPlugin;
import com.stepstone.sonar.plugin.coldfusion.cflint.CFlintConfigExporter;
import org.sonar.api.profiles.ProfileExporter;
import org.sonar.api.profiles.RulesProfile;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.Writer;

public class ColdFusionProfileExporter extends ProfileExporter {

    public ColdFusionProfileExporter() {
        super("coldfusion-cflint", "CFLint Rule Set");
        setSupportedLanguages(ColdFusionPlugin.LANGUAGE_KEY);
    }

    @Override
    public void exportProfile(RulesProfile ruleProfile, Writer writer) {

        try {
            new CFlintConfigExporter(ruleProfile).save(writer);
        } catch (IOException | XMLStreamException e) {
            Throwables.propagate(e);
        }

    }

}
