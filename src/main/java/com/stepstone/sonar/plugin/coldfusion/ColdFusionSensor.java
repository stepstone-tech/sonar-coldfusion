package com.stepstone.sonar.plugin.coldfusion;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.stepstone.sonar.plugin.coldfusion.cflint.CFlintAnalysisResultImporter;
import com.stepstone.sonar.plugin.coldfusion.cflint.CFlintConfigExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile.Type;
import org.sonar.api.component.ResourcePerspectives;
import org.sonar.api.config.Settings;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Project;
import org.sonar.api.utils.command.Command;
import org.sonar.api.utils.command.CommandExecutor;
import org.sonar.api.utils.command.StreamConsumer;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;

public class ColdFusionSensor implements Sensor {

    private final Logger log = LoggerFactory.getLogger(ColdFusionSensor.class);
    private final Settings settings;
    private final FileSystem fs;
    private final RulesProfile ruleProfile;
    private final ResourcePerspectives perspectives;

    public ColdFusionSensor(Settings settings, FileSystem fs, RulesProfile ruleProfile, ResourcePerspectives perspectives) {
        Preconditions.checkNotNull(settings);
        Preconditions.checkNotNull(fs);
        Preconditions.checkNotNull(ruleProfile);
        Preconditions.checkNotNull(perspectives);

        this.settings = settings;
        this.fs = fs;
        this.ruleProfile = ruleProfile;
        this.perspectives = perspectives;
    }

    @Override
    public boolean shouldExecuteOnProject(Project project) {
        return filesToAnalyze().iterator().hasNext();
    }

    @Override
    public void analyse(Project project, SensorContext context) {
        try {
            analyze();
            importResults();
        } catch (IOException | XMLStreamException e) {
            Throwables.propagate(e);
        }
    }

    protected void analyze() throws IOException, XMLStreamException {
        final File configFile = generateCflintConfig();
        final Command command = Command.create(settings.getString(ColdFusionPlugin.CFLINT_JAVA));

        addCflintJavaOpts(command);

        command.addArgument("-jar")
                .addArgument(settings.getString(ColdFusionPlugin.CFLINT_JAR_PATH))
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

    protected void addCflintJavaOpts(Command command) {
        final String cflintJavaOpts = settings.getString(ColdFusionPlugin.CFLINT_JAVA_OPTS);

        if (cflintJavaOpts != null) {
            final String[] arguments = cflintJavaOpts.split(" ");
            for (String argument : arguments) {
                command.addArgument(argument);
            }
        }
    }

    protected File generateCflintConfig() throws IOException, XMLStreamException {
        final File configFile = new File(fs.workDir(), "cflint-config.xml");
        new CFlintConfigExporter(ruleProfile).save(configFile);
        return configFile;
    }

    protected void importResults() {
        new CFlintAnalysisResultImporter(fs, perspectives).parse(new File(fs.workDir(), "cflint-result.xml"));
    }

    private Iterable<File> filesToAnalyze() {
        return fs.files(fs.predicates().and(fs.predicates().hasType(Type.MAIN), fs.predicates().hasLanguage(ColdFusionPlugin.LANGUAGE_KEY)));
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
