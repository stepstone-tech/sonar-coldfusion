package com.wellsky;

import com.stepstone.sonar.plugin.coldfusion.ColdFusionPlugin;
import com.stepstone.sonar.plugin.coldfusion.ColdFusionSensor;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;
import org.sonar.api.SonarQubeSide;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.internal.DefaultFileSystem;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.batch.fs.internal.TestInputFileBuilder;
import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.api.batch.sensor.measure.Measure;
import org.sonar.api.internal.SonarRuntimeImpl;
import org.sonar.api.internal.apachecommons.codec.Charsets;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.FileLinesContext;
import org.sonar.api.measures.FileLinesContextFactory;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.utils.Version;
import org.sonar.api.utils.command.CommandExecutor;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ColdfusionSensorTest {

    private RulesProfile rulesProfile = RulesProfile.create(RulesProfile.SONAR_WAY_NAME, ColdFusionPlugin.LANGUAGE_NAME);
    private File baseDir = new File("src/test/resources").getAbsoluteFile();
    private SensorContextTester context = SensorContextTester.create(baseDir);

    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    @Test
    public void testBasicCFMAnalysis() throws IOException {
        DefaultFileSystem fileSystem = new DefaultFileSystem(tmpFolder.getRoot());
        fileSystem.setEncoding(Charsets.UTF_8);
        fileSystem.setWorkDir(tmpFolder.getRoot().toPath());

        context.setFileSystem(fileSystem);
        context.setRuntime(SonarRuntimeImpl.forSonarQube(Version.create(6, 7), SonarQubeSide.SCANNER));

        context.settings().appendProperty("sonar.projectBaseDir", baseDir.getPath());
        addFilesToFs();

        CommandExecutor commandExecutor = CommandExecutor.create();
        String javaHome = System.getProperty("java.home");
        Assert.assertTrue(javaHome!=null && !javaHome.equals(""));

        if(OSValidator.isWindows()) {
            context.settings().appendProperty(ColdFusionPlugin.CFLINT_JAVA, javaHome + "/bin/java.exe");
        } else {
            context.settings().appendProperty(ColdFusionPlugin.CFLINT_JAVA, javaHome + "/bin/java");
        }

        ColdFusionSensor sensor = new ColdFusionSensor(context.fileSystem(), rulesProfile);
        sensor.execute(context);

        Integer nloc = 0;
        Integer comments = 0;
        for (InputFile o : context.fileSystem().inputFiles()) {
            Measure<Integer> measureNloc = context.measure(o.key(),CoreMetrics.NCLOC.key());
            Measure<Integer> measureComment = context.measure(o.key(),CoreMetrics.COMMENT_LINES.key());
            nloc+=measureNloc.value();
            comments+=measureComment.value();
        }

        assertThat(nloc).isEqualTo(36);
        assertThat(comments).isEqualTo(9);

    }

    private void addFilesToFs() {
        DefaultInputFile inputFileMetrics1 = new TestInputFileBuilder(context.module().key(), baseDir.getAbsoluteFile(), new File("src/test/resources/testmetrics1.cfm").getAbsoluteFile()).setLanguage(ColdFusionPlugin.LANGUAGE_KEY).build();
        context.fileSystem().add(inputFileMetrics1);
        DefaultInputFile inputFileMetrics2 = new TestInputFileBuilder(context.module().key(), baseDir.getAbsoluteFile(), new File("src/test/resources/testmetrics2.cfm").getAbsoluteFile()).setLanguage(ColdFusionPlugin.LANGUAGE_KEY).build();
        context.fileSystem().add(inputFileMetrics2);
        DefaultInputFile inputFileMetrics3 = new TestInputFileBuilder(context.module().key(), baseDir.getAbsoluteFile(), new File("src/test/resources/EpisodeClaim.cfc").getAbsoluteFile()).setLanguage(ColdFusionPlugin.LANGUAGE_KEY).build();
        context.fileSystem().add(inputFileMetrics3);
    }

}
