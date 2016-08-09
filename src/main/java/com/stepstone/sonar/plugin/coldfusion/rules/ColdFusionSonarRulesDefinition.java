package com.stepstone.sonar.plugin.coldfusion.rules;

import com.stepstone.sonar.plugin.coldfusion.ColdFusionPlugin;
import org.sonar.api.batch.BatchSide;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.server.rule.RulesDefinitionXmlLoader;
import org.sonar.squidbridge.rules.SqaleXmlLoader;

import java.io.InputStreamReader;

import static java.nio.charset.StandardCharsets.UTF_8;

@BatchSide
public class ColdFusionSonarRulesDefinition implements RulesDefinition {

    private static final String DEFAULT_SQUALE_FILE = "/com/stepstone/sonar/plugin/coldfusion/sqale.xml";
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
        SqaleXmlLoader.load(repository, DEFAULT_SQUALE_FILE);

        repository.done();
    }
}
