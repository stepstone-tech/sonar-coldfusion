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

package com.stepstone.sonar.plugin.coldfusion;

import com.google.common.base.Preconditions;
import com.stepstone.sonar.plugin.coldfusion.cflint.CFLintAnalyzer;
import com.stepstone.sonar.plugin.coldfusion.cflint.CFlintAnalysisResultImporter;
import com.stepstone.sonar.plugin.coldfusion.cflint.CFlintConfigExporter;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;

public class ColdFusionSensor implements Sensor {

    private final FileSystem fs;
    private final RulesProfile ruleProfile;
    private final Logger LOGGER = Loggers.get(ColdFusionSensor.class);

    public ColdFusionSensor(FileSystem fs, RulesProfile ruleProfile) {
        Preconditions.checkNotNull(fs);
        Preconditions.checkNotNull(ruleProfile);

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
            analyze(context);
            importResults(context);
        } catch (IOException | XMLStreamException e) {
            LOGGER.error("",e);
        }
    }

    protected void analyze(SensorContext context) throws IOException, XMLStreamException {
        new CFLintAnalyzer(context).analyze(generateCflintConfig());
    }

    protected File generateCflintConfig() throws IOException, XMLStreamException {
        final File configFile = new File(fs.workDir(), "cflint-config.xml");
        new CFlintConfigExporter(ruleProfile).save(configFile);
        return configFile;
    }

    protected void importResults(SensorContext sensorContext) {
        try {
            new CFlintAnalysisResultImporter(fs, sensorContext).parse(new File(fs.workDir(), "cflint-result.xml"));
        } catch (IOException | XMLStreamException e) {
            LOGGER.error(",e");
        }
    }

}

