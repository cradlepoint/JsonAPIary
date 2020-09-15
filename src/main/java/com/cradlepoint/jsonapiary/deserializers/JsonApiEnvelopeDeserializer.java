package com.cradlepoint.jsonapiary.deserializers;

import com.cradlepoint.jsonapiary.constants.JsonApiKeyConstants;
import com.cradlepoint.jsonapiary.deserializers.helpers.LinksDeserializer;
import com.cradlepoint.jsonapiary.envelopes.JsonApiEnvelope;
import com.cradlepoint.jsonapiary.types.ResourceLinkage;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class JsonApiEnvelopeDeserializer<T extends JsonApiEnvelope> extends StdDeserializer<T> {

    ////////////////
    // Attributes //
    ////////////////

    private Map<String, Class> jsonApiTypeMap;

    /////////////////
    // Constructor //
    /////////////////

    /**
     * Default constructor taking in JsonAPI type map
     * @param jsonApiTypeMap
     */
    public JsonApiEnvelopeDeserializer(
            Class<T> type,
            Map<String, Class> jsonApiTypeMap) {
        super(type);
        this.jsonApiTypeMap = jsonApiTypeMap;
    }

    /////////////////////////////
    // StdDeserializer Methods //
    /////////////////////////////

    /**
     * Deserializes json into enveloped, and properly annotated, new object
     * @param jsonParser
     * @param deserializationContext
     * @return
     * @throws IOException
     * @throws JsonProcessingException
     */
    public T deserialize(
            JsonParser jsonParser,
            DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        // Convert to tree //
        JsonNode rootNode = jsonParser.getCodec().readTree(jsonParser);

        // Fetch the Data, Included, and Meta nodes... //
        JsonNode dataNode = rootNode.get(JsonApiKeyConstants.DATA_KEY);
        JsonNode includedNode = rootNode.get(JsonApiKeyConstants.INCLUDED_KEY);
        JsonNode linksNode = rootNode.get(JsonApiKeyConstants.LINKS_KEY);
        JsonNode metaNode = rootNode.get(JsonApiKeyConstants.META_DATA_KEY);

        // Bootstrap a new JsonApiObjectManager //
        JsonApiObjectManager jsonApiObjectManager = new JsonApiObjectManager(
                dataNode,
                includedNode,
                jsonApiTypeMap);

        // Loop through processing and collecting all the primary object(s) //
        Object dataObject = null;
        List<ResourceLinkage> dataObjectResourceLinkages = jsonApiObjectManager.getPrimaryObjects();
        if(dataNode.isArray()) {
            List<Object> dataObjects = new ArrayList<Object>();
            for(ResourceLinkage dataObjectResourceLinkage : dataObjectResourceLinkages) {
                dataObjects.add(
                        jsonApiObjectManager.lazyFetchObject(dataObjectResourceLinkage, deserializationContext));
            }
            dataObject = dataObjects;
        } else {
            dataObject = jsonApiObjectManager.lazyFetchObject(dataObjectResourceLinkages.get(0), deserializationContext);
        }

        // Construct the Envelop from deserialized data //
        T jsonApiEnvelope = createT();
        jsonApiEnvelope.setData(dataObject);

        // Deserialize the top-level links //
        if(linksNode != null && !linksNode.isNull()) {
            LinksDeserializer.deserializeLinksInto(
                    jsonApiEnvelope,
                    linksNode);
        }

        // Deserialize the top-level meta //
        if(metaNode != null && !metaNode.isNull()) {
            Iterator<String> metaFields = metaNode.fieldNames();
            while(metaFields.hasNext()) {
                String field = metaFields.next();
                String value = metaNode.get(field).textValue();
                if(field != null && !field.isEmpty() && value != null) {
                    jsonApiEnvelope.addMeta(field, value);
                }
            }
        }

        return jsonApiEnvelope;
    }

    ///////////////////////
    // Protected Methods //
    ///////////////////////

    /**
     * Reflectively create a new Instance of the JsonApiEnvelope implementation. Feel free to override
     * for reasons (such as performance) if desired.
     * @return
     */
    protected T createT() {
        try {
            Constructor construtor = _valueClass.getConstructor();
            return (T) construtor.newInstance();
        } catch (NoSuchMethodException e) {
            String issue = "Class " + _valueClass.getSimpleName() + " has no void constructor.";
            throw new IllegalArgumentException(issue, e);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            String issue = "Class " + _valueClass.getSimpleName() + " must have a public void constructor";
            throw new IllegalArgumentException(issue, e);
        }
    }

}
