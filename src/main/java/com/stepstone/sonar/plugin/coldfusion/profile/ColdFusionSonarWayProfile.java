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

package com.stepstone.sonar.plugin.coldfusion.profile;

import com.stepstone.sonar.plugin.coldfusion.ColdFusionPlugin;
import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;
import org.sonarsource.analyzer.commons.BuiltInQualityProfileJsonLoader;

public class ColdFusionSonarWayProfile implements BuiltInQualityProfilesDefinition {

    private static final String PROFILE_NAME = "Sonar way";
    private static final String DEFAULT_PROFILE_PATH = "com/stepstone/sonar/plugin/coldfusion/profile.json";

    @Override
    public void define(Context context) {
        NewBuiltInQualityProfile profile = context.createBuiltInQualityProfile(PROFILE_NAME, ColdFusionPlugin.LANGUAGE_KEY);
        BuiltInQualityProfileJsonLoader.load(profile, ColdFusionPlugin.REPOSITORY_KEY, DEFAULT_PROFILE_PATH);
        profile.done();
    }
}
