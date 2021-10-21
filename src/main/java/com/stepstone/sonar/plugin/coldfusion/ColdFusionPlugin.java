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

import com.stepstone.sonar.plugin.coldfusion.profile.ColdFusionProfileExporter;
import com.stepstone.sonar.plugin.coldfusion.profile.ColdFusionSonarWayProfile;
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
                multiValues = true,
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
                ColdFusionSonarWayProfile.class,
                ColdFusionProfileExporter.class
        );

    }
}
