package com.stepstone.sonar.plugin.coldfusion.rules;

import com.stepstone.sonar.plugin.coldfusion.ColdFusionPlugin;
import org.sonar.squidbridge.commonrules.api.CommonRulesEngine;
import org.sonar.squidbridge.commonrules.api.CommonRulesRepository;

public class ColdFusionCommonRulesEngine extends CommonRulesEngine {

    public ColdFusionCommonRulesEngine() {
        super(ColdFusionPlugin.LANGUAGE_KEY);
    }

    @Override
    protected void doEnableRules(CommonRulesRepository repository) {
        repository.enableInsufficientCommentDensityRule(null)
                .enableDuplicatedBlocksRule()
                .enableInsufficientLineCoverageRule(null);
    }

}
