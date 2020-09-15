package com.cradlepoint.jsonapiary;

import com.cradlepoint.jsonapiary.annotations.JsonApiType;
import com.cradlepoint.jsonapiary.deserializers.JsonApiEnvelopeDeserializer;
import com.cradlepoint.jsonapiary.envelopes.JsonApiEnvelope;
import com.cradlepoint.jsonapiary.envelopes.SimpleJsonApiEnvelope;
import com.cradlepoint.jsonapiary.serializers.JsonApiEnvelopeSerializer;
import com.cradlepoint.jsonapiary.serializers.JsonApiErrorSerializer;
import com.fasterxml.jackson.core.json.PackageVersion;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class JsonApiModule extends SimpleModule {

    ////////////////
    // Attributes //
    ////////////////

    private static final String MODULE_NAME = "jsonapiary";
    private Map<String, Class> jsonApiTypeMap;

    /////////////////
    // Constructor //
    /////////////////

    /**
     * Constructor
     */
    public JsonApiModule() {
        super(PackageVersion.VERSION);
        init(SimpleJsonApiEnvelope.class, new ArrayList<Class>());
    }

    /**
     * Constructor receiving list of types that have been JsonAPIary annotated.
     * @param jsonApiTypes
     */
    public JsonApiModule(List<Class> jsonApiTypes) {
        super(PackageVersion.VERSION);
        init(SimpleJsonApiEnvelope.class, jsonApiTypes);
    }

    /**
     * Constructor, receives both envelope type and types that have been JsonAPIary annotated.
     * @param jsonApiTypes
     */
    public JsonApiModule(Class<? extends JsonApiEnvelope> envelopeType, List<Class> jsonApiTypes) {
        super(PackageVersion.VERSION);
        init(envelopeType, jsonApiTypes);
    }

    /////////////////////
    // Private Methods //
    /////////////////////

    /**
     * Initialize JsonApi Module
     * @param envelopeType
     * @param jsonApiTypes
     */
    private void init(Class<? extends JsonApiEnvelope> envelopeType, List<Class> jsonApiTypes) {
        // Generate Type Map //
        jsonApiTypeMap = new Hashtable<String, Class>();
        if(jsonApiTypes != null) {
            for(Class jsonApiType : jsonApiTypes) {
                // Verify type is JsonAPI ready //
                if(jsonApiType.isAnnotationPresent(JsonApiType.class)) {
                    String jsonApiTypeName = jsonApiType.getSimpleName();

                    // Determine the Type name //
                    JsonApiType typeAnnotation = (JsonApiType) jsonApiType.getDeclaredAnnotation(JsonApiType.class);
                    if(typeAnnotation != null && !typeAnnotation.value().isEmpty()) {
                        jsonApiTypeName = typeAnnotation.value();
                    }

                    // Check for collisions //
                    if(jsonApiTypeMap.containsKey(jsonApiTypeName)) {
                        Class existingClass = jsonApiTypeMap.get(jsonApiTypeName);
                        if(existingClass != jsonApiType) {
                            String issue = "Found two types that generate the same JsonAPI \"type\" value of " +
                                    jsonApiTypeName + " ... those types being: " + existingClass.getName() + " and " +
                                    jsonApiType.getName() + " ...! JsonAPIary only supports ONE class per type value.";
                            throw new IllegalArgumentException(issue);
                        }
                    }

                    // Add to the Map //
                    jsonApiTypeMap.put(jsonApiTypeName, jsonApiType);
                } else {
                    String issue = "Type " + jsonApiType.getName() + " is not JsonAPI annotated. All types are expected" +
                            " tp have *at least* the @JsonApiType annoatation!";
                    throw new IllegalArgumentException(issue);
                }
            }
        }

        // Register Envelope Serializers/Deserializers //
        this.addSerializer(new JsonApiEnvelopeSerializer());
        this.addDeserializer(envelopeType, new JsonApiEnvelopeDeserializer(envelopeType, jsonApiTypeMap));
        this.addSerializer(new JsonApiErrorSerializer());
    }

}
