package com.stepstone.sonar.plugin.coldfusion.cflint.xml;


import com.google.common.base.Optional;

import javax.xml.stream.XMLStreamReader;

public class LocationAttributes extends TagAttribute {

    private final Optional<String> file;
    private final Optional<Integer> line;
    private final Optional<String> message;

    public LocationAttributes(XMLStreamReader stream) {
        file = getAttributeValue("file", stream);
        message = getAttributeValue("message", stream);

        Optional<String> line = getAttributeValue("line", stream);

        if (line.isPresent()) {
            this.line = Optional.of(Integer.parseInt(line.get()));
        } else {
            this.line = Optional.absent();
        }
    }

    public Optional<String> getFile() {
        return file;
    }

    public Optional<Integer> getLine() {
        return line;
    }

    public Optional<String> getMessage() {
        return message;
    }
}
