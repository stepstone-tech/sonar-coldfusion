package com.stepstone.sonar.plugin.coldfusion.rules;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.stepstone.sonar.plugin.coldfusion.ColdFusionPlugin;
import org.sonar.api.BatchExtension;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.server.rule.RulesDefinitionXmlLoader;
import org.sonar.squidbridge.rules.SqaleXmlLoader;

import java.io.InputStreamReader;
import java.util.Set;

public class ColdFusionSonarRulesDefinition implements RulesDefinition, BatchExtension {

    private Set<String> allRuleKeys = null;

    @Override
    public void define(Context context) {
        NewRepository repository = context
                .createRepository(ColdFusionPlugin.REPOSITORY_KEY, ColdFusionPlugin.LANGUAGE_KEY)
                .setName(ColdFusionPlugin.REPOSITORY_NAME);

        RulesDefinitionXmlLoader loader = new RulesDefinitionXmlLoader();
        loader.load(repository, new InputStreamReader(getClass().getResourceAsStream("/com/stepstone/sonar/plugin/coldfusion/rules.xml"), Charsets.UTF_8));
        SqaleXmlLoader.load(repository, "/com/stepstone/sonar/plugin/coldfusion/sqale.xml");

        ImmutableSet.Builder<String> builder = ImmutableSet.builder();
        for (NewRule rule : repository.rules()) {
            builder.add(rule.key());
        }
        allRuleKeys = builder.build();

        repository.done();
    }

    public Set<String> allRuleKeys() {
        Preconditions.checkNotNull(allRuleKeys);
        return allRuleKeys;
    }

}
