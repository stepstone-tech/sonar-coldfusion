package com.stepstone.sonar.plugin.coldfusion;

import org.junit.Assert;
import org.junit.Test;
import org.sonar.api.Plugin;
import org.sonar.api.SonarQubeSide;
import org.sonar.api.SonarRuntime;
import org.sonar.api.internal.SonarRuntimeImpl;
import org.sonar.api.utils.Version;

public class ColdfusionPluginTest {


    private static final Version VERSION_7_6 = Version.create(7, 6);

    @Test
    public void testExtensions() {
        ColdFusionPlugin plugin = new ColdFusionPlugin();
        SonarRuntime runtime = SonarRuntimeImpl.forSonarQube(VERSION_7_6, SonarQubeSide.SERVER);
        Plugin.Context context = new Plugin.Context(runtime);
        plugin.define(context);

        Assert.assertEquals(5, context.getExtensions().size());
    }
}
