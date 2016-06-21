package com.stepstone.sonar.plugin.coldfusion.cflint.xml;


import com.google.common.base.Optional;
import org.apache.commons.lang.StringUtils;

import javax.xml.stream.XMLStreamReader;

class TagAttribute {

    protected Optional<String> getAttributeValue(String name, XMLStreamReader stream) {

        for (int i = 0; i < stream.getAttributeCount(); i++) {

            if (name.equalsIgnoreCase(stream.getAttributeLocalName(i))) {
                return Optional.of(StringUtils.trimToEmpty(stream.getAttributeValue(i)));
            }
        }

        return Optional.absent();
    }
}
