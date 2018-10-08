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

package com.stepstone.sonar.plugin.coldfusion;

import org.sonar.api.config.Configuration;
import org.sonar.api.resources.AbstractLanguage;

public class ColdFusion extends AbstractLanguage {

    private final Configuration configuration;

    public ColdFusion(Configuration configuration) {
        super(ColdFusionPlugin.LANGUAGE_KEY, ColdFusionPlugin.LANGUAGE_NAME);
        this.configuration = configuration;
    }

    @Override
    public String[] getFileSuffixes() {
        return configuration.getStringArray(ColdFusionPlugin.FILE_SUFFIXES_KEY);
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

        return !(configuration != null ? !configuration.equals(that.configuration) : that.configuration != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (configuration != null ? configuration.hashCode() : 0);
        return result;
    }
}
