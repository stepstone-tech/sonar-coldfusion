package com.stepstone.sonar.plugin.coldfusion.profile;

import com.google.common.base.Charsets;
import org.sonar.api.profiles.ProfileDefinition;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.profiles.XMLProfileParser;
import org.sonar.api.utils.ValidationMessages;

import java.io.InputStreamReader;

public class ColdFusionSonarWayProfileImporter extends ProfileDefinition {

    private static final String DEFAULT_PROFILE_PATH = "/com/stepstone/sonar/plugin/coldfusion/profile.xml";

    private final XMLProfileParser xmlParser;

    public ColdFusionSonarWayProfileImporter(XMLProfileParser xmlParser) {
        this.xmlParser = xmlParser;
    }

    @Override
    public RulesProfile createProfile(ValidationMessages validation) {
        return xmlParser.parse(new InputStreamReader(getClass().getResourceAsStream(DEFAULT_PROFILE_PATH), Charsets.UTF_8), validation);
    }

}
