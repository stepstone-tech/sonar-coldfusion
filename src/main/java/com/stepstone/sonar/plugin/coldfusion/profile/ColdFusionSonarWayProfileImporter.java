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

import com.google.common.base.Throwables;
import com.stepstone.sonar.plugin.coldfusion.ColdFusionPlugin;
import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;

public class ColdFusionSonarWayProfileImporter implements BuiltInQualityProfilesDefinition {

    private static final String DEFAULT_PROFILE_PATH = "/com/stepstone/sonar/plugin/coldfusion/profile.xml";


    @Override
    public void define(Context context) {
        final NewBuiltInQualityProfile profile = context.createBuiltInQualityProfile(
            "Sonar Way", ColdFusionPlugin.LANGUAGE_NAME
        );
        try (final InputStream rulesXml = this.getClass().getClassLoader().getResourceAsStream(DEFAULT_PROFILE_PATH)){
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder builder = factory.newDocumentBuilder();
            final Document xmlDoc = builder.parse(rulesXml);
            final NodeList nodes = xmlDoc.getElementsByTagName("key");
            for (int i = 0; i < nodes.getLength(); i++) {
                final Node node = nodes.item(i);
                final String key = node.getTextContent();
                profile.activateRule(ColdFusionPlugin.REPOSITORY_KEY, key);
            }
        }catch (Exception e){
            Throwables.propagate(e);
        }

        profile.done();
    }

    public ColdFusionSonarWayProfileImporter() {
    }
}
