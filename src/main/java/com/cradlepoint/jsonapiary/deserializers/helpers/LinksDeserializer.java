package com.cradlepoint.jsonapiary.deserializers.helpers;

import com.cradlepoint.jsonapiary.envelopes.JsonApiEnvelope;
import com.cradlepoint.jsonapiary.envelopes.SimpleJsonApiEnvelope;
import com.fasterxml.jackson.databind.JsonNode;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

public class LinksDeserializer {

    /////////////////
    // Constructor //
    /////////////////

    /**
     * private void constructor
     */
    private LinksDeserializer() { }

    ////////////////////
    // Public Methods //
    ////////////////////

    /**
     * Deserializes Links into the passed in JsonApiEnvelope
     * @param jsonApiEnvelope
     * @param linksNode
     */
    public static void deserializeLinksInto(
            JsonApiEnvelope jsonApiEnvelope,
            JsonNode linksNode) {
        Iterator<String> linksFields = linksNode.fieldNames();
        while(linksFields.hasNext()) {
            String field = linksFields.next();
            String value = linksNode.get(field).textValue();
            jsonApiEnvelope.addLink(field, value);
        }
    }

}
