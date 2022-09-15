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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.stream.XMLStreamException;

import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.config.Configuration;
import org.sonar.api.utils.command.Command;
import org.sonar.api.utils.command.StreamConsumer;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import com.cflint.api.CFLintAPI;
import com.cflint.api.CFLintResult;
import com.cflint.config.CFLintConfiguration;
import com.cflint.config.CFLintPluginInfo;
import com.cflint.config.ConfigBuilder;
import com.cflint.exception.CFLintConfigurationException;
import com.cflint.exception.CFLintScanException;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.stepstone.sonar.plugin.coldfusion.ColdFusionPlugin;

public class CFLintAnalyzer {

    private final Logger logger = Loggers.get(CFLintAnalyzer.class);
    private final Configuration settings;
    private final FileSystem fs;

    public CFLintAnalyzer(SensorContext sensorContext) {
        Preconditions.checkNotNull(sensorContext);

        this.settings = sensorContext.config();
        this.fs = sensorContext.fileSystem();
    }

    public void analyze(File configFile) throws IOException, XMLStreamException {
        File executableJar = null;
        try {
            // Command command = Command.create(settings.get(ColdFusionPlugin.CFLINT_JAVA).orElseThrow(
            //     IllegalStateException::new
            // ));
            // addCflintJavaOpts(command);
            // executableJar = extractCflintJar();

            List<String> filesToScan = new ArrayList<>();

            for (InputFile file : fs.inputFiles(fs.predicates().hasLanguage(ColdFusionPlugin.LANGUAGE_KEY)))
                filesToScan.add(file.absolutePath());
            
            try {
                ConfigBuilder cflintConfigBuilder = new ConfigBuilder(new CFLintPluginInfo());
                cflintConfigBuilder.addCustomConfig(configFile.getPath());

                CFLintAPI linter = new CFLintAPI(
                    cflintConfigBuilder.build()
                );
                linter.setVerbose(true);

                CFLintResult lintResult = linter.scan(filesToScan);

                try (final Writer xmlwriter = createXMLWriter(fs.workDir() + File.separator + "cflint-result.xml", StandardCharsets.UTF_8)) {
                    lintResult.writeXml(xmlwriter);
                }
            } catch(CFLintScanException se) {
                se.printStackTrace();
            }catch(Exception ce) {
                throw new IOException(ce);
            } 
            

            // command.addArgument("-jar")
            //     .addArgument(executableJar.getPath())
            //     .addArgument("-xml")
            //     // .addArgument("-folder")
            //     // .addArgument(settings.get("sonar.projectBaseDir").orElseThrow(
            //     //     IllegalStateException::new
            //     // ))
            //     .addArgument("-file")
            //     .addArgument(fileArg)
            //     .addArgument("-xmlfile")
            //     .addArgument(fs.workDir() + File.separator + "cflint-result.xml")
            //     .addArgument("-configfile")
            //     .addArgument(configFile.getPath())
            //     .addArgument("-v");


            // CommandExecutor executor = CommandExecutor.create();
            // int exitCode = executor.execute(command, new LogInfoStreamConsumer(), new LogErrorStreamConsumer(), Integer.MAX_VALUE);

            // if (exitCode != 0) {
            //     throw new IllegalStateException("The CFLint analyzer failed with exit code: " + exitCode);
            // }

            
        } finally {
            //cleanup
            if(executableJar!= null && executableJar.exists()) {
                executableJar.deleteOnExit();
            }
        }

    }

    protected File extractCflintJar() throws IOException {
        return new CFLintExtractor(fs.workDir()).extract();
    }

    protected void addCflintJavaOpts(Command command) {
        final String cflintJavaOpts = settings.get(ColdFusionPlugin.CFLINT_JAVA_OPTS).orElse("");
        if (!Strings.isNullOrEmpty(cflintJavaOpts)) {
            final String[] arguments = cflintJavaOpts.split(" ");
            for (String argument : arguments) {
                command.addArgument(argument);
            }
        }
    }

    private class LogInfoStreamConsumer implements StreamConsumer {

        @Override
        public void consumeLine(String line) {
            logger.info("Consuming line {}", line);
        }

    }

    private class LogErrorStreamConsumer implements StreamConsumer {

        @Override
        public void consumeLine(String line) {
            logger.error("Error consuming line {}", line);
        }
    }

    private Writer createXMLWriter(final String xmlOutFile, final Charset encoding) throws IOException {
        final OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(xmlOutFile), encoding);
        try {
            out.append(String.format("<?xml version=\"1.0\" encoding=\"%s\" ?>%n", encoding));
        } catch (final IOException e) {
            throw new IOException(e);
        }
        return out;
    }

}
