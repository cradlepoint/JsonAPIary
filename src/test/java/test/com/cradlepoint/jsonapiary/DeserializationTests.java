package test.com.cradlepoint.jsonapiary;

import com.cradlepoint.jsonapiary.JsonApiModule;
import com.cradlepoint.jsonapiary.envelopes.SimpleJsonApiEnvelope;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.Assert;
import org.junit.Test;
import test.com.cradlepoint.jsonapiary.pojos.*;

import java.util.Arrays;

public class DeserializationTests {

    ////////////////
    // Attributes //
    ////////////////

    private ObjectMapper objectMapper;

    /////////////////
    // Constructor //
    /////////////////

    public DeserializationTests() {
        JsonApiModule jsonApiModule = new JsonApiModule(Arrays.asList(
                SimpleObject.class,
                SimpleSubObject.class,
                SimpleNestedSubObject.class,
                SingleLinkNode.class,
                ABaseClass.class,
                AChildClass.class,
                TypeWithABoolean.class));

        objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.registerModule(jsonApiModule);
    }

    ///////////
    // Tests //
    ///////////

    @Test
    public void deserializeObjectWithoutIdTest() throws Exception {
        // Init Test Objects //
        String json = "{\n" +
                "  \"data\" : {\n" +
                "    \"type\" : \"TypeWithABoolean\",\n" +
                "    \"attributes\" : {\n" +
                "      \"bool\" : false\n" +
                "    }\n" +
                "  }\n" +
                "}";

        // Deserialize and Verify //
        SimpleJsonApiEnvelope<TypeWithABoolean> jsonApiEnvelope = objectMapper.readValue(json, SimpleJsonApiEnvelope.class);
        Assert.assertNotNull(jsonApiEnvelope);
        Assert.assertNotNull(jsonApiEnvelope.getData());
        Assert.assertEquals(false, jsonApiEnvelope.getData().isBool());
    }

}
