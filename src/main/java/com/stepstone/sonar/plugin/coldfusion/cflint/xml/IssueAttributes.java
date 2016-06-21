package com.stepstone.sonar.plugin.coldfusion.cflint.xml;

import com.google.common.base.Optional;

import javax.xml.stream.XMLStreamReader;

public class IssueAttributes extends TagAttribute {

    private Optional<String> id;

    public IssueAttributes(XMLStreamReader stream) {
        this.id = getAttributeValue("id", stream);
    }

    public Optional<String> getId() {
        return id;
    }

}
