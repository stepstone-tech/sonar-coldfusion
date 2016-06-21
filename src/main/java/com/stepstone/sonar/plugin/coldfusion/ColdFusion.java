package com.stepstone.sonar.plugin.coldfusion;

import org.sonar.api.config.Settings;
import org.sonar.api.resources.AbstractLanguage;

public class ColdFusion extends AbstractLanguage {

    private final Settings settings;

    public ColdFusion(Settings settings) {
        super(ColdFusionPlugin.LANGUAGE_KEY, ColdFusionPlugin.LANGUAGE_NAME);
        this.settings = settings;
    }

    @Override
    public String[] getFileSuffixes() {
        return settings.getStringArray(ColdFusionPlugin.FILE_SUFFIXES_KEY);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        ColdFusion that = (ColdFusion) o;

        return !(settings != null ? !settings.equals(that.settings) : that.settings != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (settings != null ? settings.hashCode() : 0);
        return result;
    }
}
