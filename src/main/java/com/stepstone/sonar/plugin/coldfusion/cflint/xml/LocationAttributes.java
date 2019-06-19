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

package com.stepstone.sonar.plugin.coldfusion.cflint.xml;


import java.util.Optional;
import javax.xml.stream.XMLStreamReader;

public class LocationAttributes extends TagAttribute {

    private final String file;
    private final Optional<Integer> line;
    private final Optional<String> message;

    public LocationAttributes(XMLStreamReader stream) {
        file = getAttributeValue("file", stream).get();
        message = getAttributeValue("message", stream);

        Optional<String> currentLine = getAttributeValue("line", stream);

        this.line = currentLine.map(Integer::parseInt);
    }

    public String getFile() {
        return file;
    }

    public Optional<Integer> getLine() {
        return line;
    }

    public Optional<String> getMessage() {
        return message;
    }
}
