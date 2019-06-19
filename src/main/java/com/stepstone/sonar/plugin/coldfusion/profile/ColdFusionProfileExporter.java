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
import com.stepstone.sonar.plugin.coldfusion.errors.ColdFusionPluginException;
import com.stepstone.sonar.plugin.coldfusion.cflint.CFLintConfigExporter;
import org.sonar.api.profiles.ProfileExporter;
import org.sonar.api.profiles.RulesProfile;

import javax.xml.stream.XMLStreamException;
import java.io.Writer;

public class ColdFusionProfileExporter extends ProfileExporter {

    public ColdFusionProfileExporter() {
        super("coldfusion-cflint", "CFLint Rule Set");
        setSupportedLanguages(ColdFusionPlugin.LANGUAGE_KEY);
    }

    @Override
    public void exportProfile(RulesProfile ruleProfile, Writer writer) {

        try {
            new CFLintConfigExporter(ruleProfile).save(writer);
        } catch (XMLStreamException e) {
            throw new ColdFusionPluginException("Could not export coldfusion-cflint profile", e);
        }

    }

}
