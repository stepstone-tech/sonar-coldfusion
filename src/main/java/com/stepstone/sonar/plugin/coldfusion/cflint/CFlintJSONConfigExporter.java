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

package com.stepstone.sonar.plugin.coldfusion.cflint;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;

import javax.xml.stream.XMLStreamException;

public class CFlintJSONConfigExporter {

    private final Collection<String> ruleKeys;
    public CFlintJSONConfigExporter(Collection<String> ruleKeys) {
        this.ruleKeys = ruleKeys;
    }

    public void save(File configFile) throws IOException, XMLStreamException {
        try (FileWriter writer = new FileWriter(configFile)) {
            save(writer);
        }
    }

    public void save(Writer writer) throws IOException, XMLStreamException {
        writer.append("{ \"includes\": [");

        boolean first = true;
        for (String ruleKey : ruleKeys) {
            if (first) first=false;
            else writer.append(",");

            writer.append(" {\"code\":\"" + ruleKey + "\"}");
        }

        writer.append(" ] }");
        writer.close();
    }
}
