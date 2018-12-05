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
import org.sonar.api.batch.sensor.internal.SensorContextTester;
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

    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    @Test
    public void testBasicCFMAnalysis() throws IOException {
        DefaultFileSystem fileSystem = new DefaultFileSystem(tmpFolder.getRoot());
        fileSystem.setEncoding(Charsets.UTF_8);
        fileSystem.setWorkDir(tmpFolder.getRoot().toPath());

        File sourceDir = new File("src/test/resources");
        SensorContextTester context = SensorContextTester.create(sourceDir.toPath());
        context.setFileSystem(fileSystem);
        context.setRuntime(SonarRuntimeImpl.forSonarQube(Version.create(6, 7), SonarQubeSide.SCANNER));
        CommandExecutor commandExecutor = CommandExecutor.create();
        String javaHome = System.getProperty("java.home");
        Assert.assertTrue(javaHome!=null && !javaHome.equals(""));
        //FIXME get Java on Linux too and check there is java Home set
        if(OSValidator.isWindows()) {
            context.settings().appendProperty(ColdFusionPlugin.CFLINT_JAVA, javaHome + "/bin/java.exe");
        } else {
            context.settings().appendProperty(ColdFusionPlugin.CFLINT_JAVA, javaHome + "/bin/java");
        }

        context.settings().appendProperty("sonar.sources",sourceDir.getPath());
        // Mock visitor for metrics.
        FileLinesContext fileLinesContext = mock(FileLinesContext.class);
        FileLinesContextFactory fileLinesContextFactory = mock(FileLinesContextFactory.class);
        when(fileLinesContextFactory.createFor(any(InputFile.class))).thenReturn(fileLinesContext);
        context = Mockito.spy(context);
        ColdFusionSensor sensor = new ColdFusionSensor(context.fileSystem(), rulesProfile);
        sensor.execute(context);

        assertThat(context.measure(context.module().key(), CoreMetrics.FILES.key()).value()).isEqualTo(2);

        assertThat(context.measure(context.module().key(), CoreMetrics.LINES.key()).value()).isEqualTo(19);
    }

}
