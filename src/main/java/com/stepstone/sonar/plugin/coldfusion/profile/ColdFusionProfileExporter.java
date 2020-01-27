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
import com.stepstone.sonar.plugin.coldfusion.cflint.CFLintConfigExporter;

import com.google.common.base.Throwables;

import org.sonar.api.batch.rule.ActiveRules;
import org.sonar.api.profiles.ProfileExporter;
import org.sonar.api.profiles.RulesProfile;

import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.XMLStreamException;

public class ColdFusionProfileExporter extends ProfileExporter {

    public ColdFusionProfileExporter() {
        super("coldfusion-cflint", "CFLint Rule Set");
        setSupportedLanguages(ColdFusionPlugin.LANGUAGE_KEY);
    }

    @Override
    public void exportProfile(RulesProfile ruleProfile, Writer writer) {
        try {
            new CFLintConfigExporter(ruleProfile.getActiveRulesByRepository(ColdFusionPlugin.REPOSITORY_KEY)).save(writer);
        } catch (IOException | XMLStreamException e) {
            Throwables.propagate(e);
        }
    }

    public void exportProfile(ActiveRules activeRules, Writer writer) {
        try {
            new CFLintConfigExporter(activeRules.findByRepository(ColdFusionPlugin.REPOSITORY_KEY)).save(writer);
        } catch (IOException | XMLStreamException e) {
            Throwables.propagate(e);
        }

    }

}
