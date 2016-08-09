package com.stepstone.sonar.plugin.coldfusion.cflint;

import com.google.common.base.Preconditions;
import com.stepstone.sonar.plugin.coldfusion.ColdFusionPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.config.Settings;
import org.sonar.api.utils.command.Command;
import org.sonar.api.utils.command.CommandExecutor;
import org.sonar.api.utils.command.StreamConsumer;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;

public class CFLintAnalyzer {

    private final Logger log = LoggerFactory.getLogger(CFLintAnalyzer.class);
    private final Settings settings;
    private final FileSystem fs;

    public CFLintAnalyzer(Settings settings, FileSystem fs) {
        Preconditions.checkNotNull(settings);
        Preconditions.checkNotNull(fs);

        this.settings = settings;
        this.fs = fs;
    }

    public void analyze(File configFile) throws IOException, XMLStreamException {

        final Command command = Command.create(settings.getString(ColdFusionPlugin.CFLINT_JAVA));

        addCflintJavaOpts(command);

        command.addArgument("-jar")
                .addArgument(extractCflintJar().getPath())
                .addArgument("-xml")
                .addArgument("-file")
                .addArgument(fs.baseDir().getPath())
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
        final String cflintJavaOpts = settings.getString(ColdFusionPlugin.CFLINT_JAVA_OPTS);

        if (cflintJavaOpts != null) {
            final String[] arguments = cflintJavaOpts.split(" ");
            for (String argument : arguments) {
                command.addArgument(argument);
            }
        }
    }

    private class LogInfoStreamConsumer implements StreamConsumer {

        @Override
        public void consumeLine(String line) {
            log.info(line);
        }

    }

    private class LogErrorStreamConsumer implements StreamConsumer {

        @Override
        public void consumeLine(String line) {
            log.error(line);
        }

    }

}
