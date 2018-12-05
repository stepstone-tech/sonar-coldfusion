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

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.stepstone.sonar.plugin.coldfusion.ColdFusionPlugin;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.config.Configuration;
import org.sonar.api.utils.command.Command;
import org.sonar.api.utils.command.CommandExecutor;
import org.sonar.api.utils.command.StreamConsumer;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;

public class CFLintAnalyzer {

    private final Logger LOGGER = Loggers.get(CFLintAnalyzer.class);
    private final Configuration settings;
    private final FileSystem fs;

    public CFLintAnalyzer(SensorContext sensorContext) {
        Preconditions.checkNotNull(sensorContext);

        this.settings = sensorContext.config();
        this.fs = sensorContext.fileSystem();
    }

    public void analyze(File configFile) throws IOException, XMLStreamException {

        final Command command = Command.create(settings.get(ColdFusionPlugin.CFLINT_JAVA).get());

        addCflintJavaOpts(command);

        command.addArgument("-jar")
                .addArgument(extractCflintJar().getPath())
                .addArgument("-xml")
                .addArgument("-folder")
                .addArgument(settings.get("sonar.sources").get())
                .addArgument("-xmlfile")
                .addArgument(fs.workDir() + File.separator + "cflint-result.xml")
                .addArgument("-configfile")
                .addArgument(configFile.getPath());

        int exitCode = CommandExecutor.create().execute(command, new LogInfoStreamConsumer(), new LogErrorStreamConsumer(), Integer.MAX_VALUE);
        if (exitCode != 0) {
            throw new IllegalStateException("The CFLint analyzer failed with exit code: " + exitCode);
        }
    }

    protected File extractCflintJar() throws IOException {
        return new CFLintExtractor(fs.workDir()).extract();
    }

    protected void addCflintJavaOpts(Command command) {
        if(settings.get(ColdFusionPlugin.CFLINT_JAVA_OPTS).isPresent()) {
            final String cflintJavaOpts = settings.get(ColdFusionPlugin.CFLINT_JAVA_OPTS).get();

            if (!Strings.isNullOrEmpty(cflintJavaOpts)) {
                final String[] arguments = cflintJavaOpts.split(" ");
                for (String argument : arguments) {
                    command.addArgument(argument);
                }
            }
        }
    }

    private class LogInfoStreamConsumer implements StreamConsumer {

        @Override
        public void consumeLine(String line) {
            LOGGER.info("Consuming line {}", line);
        }

    }

    private class LogErrorStreamConsumer implements StreamConsumer {

        @Override
        public void consumeLine(String line) {
            LOGGER.error("Error consuming line {}",line);
        }
    }

}
