package com.stepstone.sonar.plugin.coldfusion;

import com.stepstone.sonar.plugin.coldfusion.profile.ColdFusionProfileExporter;
import com.stepstone.sonar.plugin.coldfusion.profile.ColdFusionSonarWayProfileImporter;
import com.stepstone.sonar.plugin.coldfusion.rules.ColdFusionSonarRulesDefinition;
import org.sonar.api.Plugin;
import org.sonar.api.Properties;
import org.sonar.api.Property;

@Properties({
        @Property(
                key = ColdFusionPlugin.FILE_SUFFIXES_KEY,
                defaultValue = ColdFusionPlugin.FILE_SUFFIXES_DEFVALUE,
                name = "File suffixes",
                description = "Comma-separated list of suffixes of files to analyze.",
                project = true,
                global = true
        ),
        @Property(
                key = ColdFusionPlugin.CFLINT_JAVA,
                defaultValue = "java",
                name = "Java executable",
                description = "",
                project = true,
                global = true
        ),
        @Property(
                key = ColdFusionPlugin.CFLINT_JAVA_OPTS,
                defaultValue = "",
                name = "Java executable options",
                description = "Additional parameters passed to java process. E.g. -Xmx1g",
                project = true,
                global = true
        ),
})
public class ColdFusionPlugin implements Plugin {

    public static final String LANGUAGE_KEY = "cf";
    public static final String LANGUAGE_NAME = "ColdFusion";

    public static final String FILE_SUFFIXES_KEY = "sonar.cf.file.suffixes";
    public static final String FILE_SUFFIXES_DEFVALUE = ".cfc,.cfm";

    public static final String REPOSITORY_KEY = "coldfusionsquid";
    public static final String REPOSITORY_NAME = "SonarQube";

    public static final String CFLINT_JAVA = "sonar.cf.cflint.java";
    public static final String CFLINT_JAVA_OPTS = "sonar.cf.cflint.java.opts";

    @Override
    public void define(Context context) {
        context.addExtensions(
                ColdFusion.class,
                ColdFusionSensor.class,
                ColdFusionSonarRulesDefinition.class,
                ColdFusionSonarWayProfileImporter.class,
                ColdFusionProfileExporter.class
        );

    }
}
