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

package com.stepstone.sonar.plugin.coldfusion.rules;

import com.stepstone.sonar.plugin.coldfusion.ColdFusionPlugin;
import org.sonar.api.batch.ScannerSide;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.server.rule.RulesDefinitionXmlLoader;
import org.sonar.squidbridge.rules.SqaleXmlLoader;

import java.io.InputStreamReader;

import static java.nio.charset.StandardCharsets.UTF_8;

@ScannerSide
public class ColdFusionSonarRulesDefinition implements RulesDefinition {

    private static final String DEFAULT_RULES_FILE = "/com/stepstone/sonar/plugin/coldfusion/rules.xml";

    private final RulesDefinitionXmlLoader rulesLoader;

    public ColdFusionSonarRulesDefinition(RulesDefinitionXmlLoader rulesLoader) {
        this.rulesLoader = rulesLoader;
    }

    @Override
    public void define(Context context) {
        NewRepository repository = context
                .createRepository(ColdFusionPlugin.REPOSITORY_KEY, ColdFusionPlugin.LANGUAGE_KEY)
                .setName(ColdFusionPlugin.REPOSITORY_NAME);

        rulesLoader.load(repository, new InputStreamReader(getClass().getResourceAsStream(DEFAULT_RULES_FILE), UTF_8));
        //SqaleXmlLoader.load(repository, DEFAULT_SQUALE_FILE);

        repository.done();
    }
}
