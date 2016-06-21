package com.stepstone.sonar.plugin.coldfusion.rules;

import com.stepstone.sonar.plugin.coldfusion.ColdFusionPlugin;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.rule.CheckFactory;
import org.sonar.api.component.ResourcePerspectives;
import org.sonar.squidbridge.commonrules.api.CommonRulesDecorator;

public class ColdFusionCommonRulesDecorator extends CommonRulesDecorator {

    public ColdFusionCommonRulesDecorator(FileSystem fs, CheckFactory checkFactory, ResourcePerspectives perspectives) {
        super(ColdFusionPlugin.LANGUAGE_KEY, fs, checkFactory, perspectives);
    }

}
