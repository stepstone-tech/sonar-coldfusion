package com.stepstone.sonar.plugin.coldfusion;

import org.junit.Assert;
import org.junit.Test;
import org.sonar.api.Plugin;
import org.sonar.api.SonarEdition;
import org.sonar.api.SonarQubeSide;
import org.sonar.api.SonarRuntime;
import org.sonar.api.utils.Version;
import org.sonar.api.internal.SonarRuntimeImpl;


public class ColdfusionPluginTest {


    private static final Version VERSION_9_0 = Version.create(9, 0);

    @Test
    public void testExtensions() {
        ColdFusionPlugin plugin = new ColdFusionPlugin();
        SonarRuntime runtime = SonarRuntimeImpl.forSonarQube(VERSION_9_0, SonarQubeSide.SERVER, SonarEdition.COMMUNITY);
        Plugin.Context context = new Plugin.Context(runtime);
        plugin.define(context);

        Assert.assertEquals(5, context.getExtensions().size());
    }
}
