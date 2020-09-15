package test.com.cradlepoint.jsonapiary;

import com.cradlepoint.jsonapiary.JsonApiModule;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.Assert;
import org.junit.Test;
import test.com.cradlepoint.jsonapiary.envelopes.CustomTestEnvelope;
import test.com.cradlepoint.jsonapiary.pojos.*;

import java.util.Arrays;

public class CustomEnvelopeTests {

    ////////////////
    // Attributes //
    ////////////////

    private ObjectMapper objectMapper;

    /////////////////
    // Constructor //
    /////////////////

    public CustomEnvelopeTests() {
        JsonApiModule jsonApiModule = new JsonApiModule(CustomTestEnvelope.class, Arrays.asList(
                SimpleObject.class,
                SimpleSubObject.class,
                SimpleNestedSubObject.class,
                SingleLinkNode.class,
                ABaseClass.class,
                AChildClass.class,
                SimpleObjectWithListRelationship.class,
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
    public void serializationDeserializationTest() throws Exception {
        // Init Test Objects //
        TypeWithABoolean testObject = new TypeWithABoolean();
        testObject.setId(83701);
        testObject.setBool(true);

        // Serialize! //
        String json = objectMapper.writeValueAsString(new CustomTestEnvelope<TypeWithABoolean>(testObject));
        Assert.assertNotNull(json);
        Assert.assertTrue(json.equals("{\n" +
                "  \"data\" : {\n" +
                "    \"id\" : \"83701\",\n" +
                "    \"type\" : \"TypeWithABoolean\",\n" +
                "    \"attributes\" : {\n" +
                "      \"bool\" : true\n" +
                "    }\n" +
                "  }\n" +
                "}"));

        // Deserialize! //
        CustomTestEnvelope<TypeWithABoolean> deserializedObject = objectMapper.readValue(json, CustomTestEnvelope.class);
        Assert.assertNotNull(deserializedObject);
        Assert.assertEquals(testObject.getId(), deserializedObject.getData().getId());
        Assert.assertEquals(testObject.isBool(), deserializedObject.getData().isBool());
    }

}
