package com.stepstone.sonar.plugin.coldfusion;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.stepstone.sonar.plugin.coldfusion.cflint.CFLintAnalyzer;
import com.stepstone.sonar.plugin.coldfusion.cflint.CFlintAnalysisResultImporter;
import com.stepstone.sonar.plugin.coldfusion.cflint.CFlintConfigExporter;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.config.Settings;
import org.sonar.api.profiles.RulesProfile;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;

public class ColdFusionSensor implements org.sonar.api.batch.sensor.Sensor {

    private final Settings settings;
    private final FileSystem fs;
    private final RulesProfile ruleProfile;

    public ColdFusionSensor(Settings settings, FileSystem fs, RulesProfile ruleProfile) {
        Preconditions.checkNotNull(settings);
        Preconditions.checkNotNull(fs);
        Preconditions.checkNotNull(ruleProfile);

        this.settings = settings;
        this.fs = fs;
        this.ruleProfile = ruleProfile;
    }


    @Override
    public void describe(SensorDescriptor descriptor) {
        descriptor.onlyOnLanguage(ColdFusionPlugin.LANGUAGE_KEY);
        descriptor.createIssuesForRuleRepository(ColdFusionPlugin.REPOSITORY_KEY);
    }

    @Override
    public void execute(SensorContext context) {
        try {
            analyze();
            importResults(context);
        } catch (IOException | XMLStreamException e) {
            Throwables.propagate(e);
        }
    }

    protected void analyze() throws IOException, XMLStreamException {
        new CFLintAnalyzer(settings, fs).analyze(generateCflintConfig());
    }

    protected File generateCflintConfig() throws IOException, XMLStreamException {
        final File configFile = new File(fs.workDir(), "cflint-config.xml");
        new CFlintConfigExporter(ruleProfile).save(configFile);
        return configFile;
    }

    protected void importResults(SensorContext sensorContext) {
        new CFlintAnalysisResultImporter(fs, sensorContext).parse(new File(fs.workDir(), "cflint-result.xml"));
    }
}

