package test.com.cradlepoint.jsonapiary;

import com.cradlepoint.jsonapiary.JsonApiModule;
import com.cradlepoint.jsonapiary.envelopes.SimpleJsonApiEnvelope;
import com.cradlepoint.jsonapiary.envelopes.JsonApiOptions;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.Assert;
import org.junit.Test;
import test.com.cradlepoint.jsonapiary.pojos.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ComboTests {

    ////////////////
    // Attributes //
    ////////////////

    private ObjectMapper objectMapper;

    /////////////////
    // Constructor //
    /////////////////

    public ComboTests() {
        JsonApiModule jsonApiModule = new JsonApiModule(Arrays.asList(
                SimpleObject.class,
                SimpleSubObject.class,
                SimpleNestedSubObject.class,
                SingleLinkNode.class,
                ABaseClass.class,
                AChildClass.class,
                SimpleObjectWithListRelationship.class));

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
    public void simpleRelationshipTest() throws Exception {
        SimpleObject simpleObject = new SimpleObject();
        SimpleSubObject simpleSubObject = new SimpleSubObject();
        simpleObject.setThing2(simpleSubObject);

        // First Serialize //
        String serialization = objectMapper.writeValueAsString(new SimpleJsonApiEnvelope<SimpleObject>(simpleObject));

        // Then try to Deserialize the output back! //
        SimpleJsonApiEnvelope<SimpleObject> deserializedObject = objectMapper.readValue(serialization, SimpleJsonApiEnvelope.class);

        Assert.assertTrue(simpleObject.equals(deserializedObject.getData()));
    }

    @Test
    public void emptyRelationshipListTest() throws Exception {
        SimpleSubObject simpleSubObject = new SimpleSubObject();
        SimpleNestedSubObject simpleNestedSubObject = new SimpleNestedSubObject();
        simpleNestedSubObject.setMetaThing(new SimpleObject());
        simpleSubObject.setNestedThing(simpleNestedSubObject);

        // First Serialize //
        String serialization = objectMapper.writeValueAsString(new SimpleJsonApiEnvelope<SimpleSubObject>(simpleSubObject));

        // Then try to Deserialize the output back! //
        SimpleJsonApiEnvelope<SimpleSubObject> deserializedObject = objectMapper.readValue(serialization, SimpleJsonApiEnvelope.class);

        Assert.assertTrue(simpleSubObject.equals(deserializedObject.getData()));
    }

    @Test
    public void complexRelationshipTest() throws Exception {
        SimpleNestedSubObject simpleNestedSubObject = new SimpleNestedSubObject();

        SimpleSubObject simpleSubObject = new SimpleSubObject(8);
        simpleSubObject.setNestedThing(simpleNestedSubObject);

        SimpleObject simpleObject = new SimpleObject();
        simpleObject.setThing2(simpleSubObject);

        simpleNestedSubObject.setMetaThing(new SimpleObject());

        // First Serialize //
        String serialization = objectMapper.writeValueAsString(new SimpleJsonApiEnvelope<SimpleObject>(simpleObject));

        // Then try to Deserialize the output back! //
        SimpleJsonApiEnvelope<SimpleObject> deserializedObject = objectMapper.readValue(serialization, SimpleJsonApiEnvelope.class);

        Assert.assertTrue(simpleObject.equals(deserializedObject.getData()));
    }

    @Test
    public void topLevelLinksAndMetaTest() throws Exception {
        // Init test objects //
        SimpleObject simpleObject1 = new SimpleObject();
        simpleObject1.setId(1l);
        simpleObject1.setAttribute("number: O.N.E.");
        SimpleJsonApiEnvelope<SimpleObject> jsonApiEnvelope = new SimpleJsonApiEnvelope<SimpleObject>(simpleObject1);

        jsonApiEnvelope.addMeta("top-LEVEL-meta-THING", "this is a fancy thing!");
        jsonApiEnvelope.addMeta("helllllo", "good! buy!");

        jsonApiEnvelope.addLink("google", new URL("http://www.google.com"));
        jsonApiEnvelope.addLink("jsonapi", new URL("http://jsonapi.org/"));

        // First, serialize //
        String json = objectMapper.writeValueAsString(jsonApiEnvelope);
        Assert.assertNotNull(json);

        // Then, try to deserialize back into an equal Object //
        SimpleJsonApiEnvelope<SimpleObject> deserializedObject = objectMapper.readValue(json, SimpleJsonApiEnvelope.class);
        Assert.assertNotNull(deserializedObject);
        Assert.assertTrue(jsonApiEnvelope.equals(deserializedObject));
    }

    @Test
    public void inheritanceTest() throws Exception {
        // Init Test Objects //
        SimpleObject simpleObject = new SimpleObject();
        simpleObject.setId(314l);
        simpleObject.setAttribute("pi");

        AChildClass aChildClass = new AChildClass();
        aChildClass.setWhoAmI("my(ComboTests)Id");
        aChildClass.setWhatDoIHave(simpleObject);
        aChildClass.setWhaz("this is (ComboTests) whaz!");
        aChildClass.setMetaInt(210);
        SimpleJsonApiEnvelope<AChildClass> jsonApiEnvelope = new SimpleJsonApiEnvelope<AChildClass>(aChildClass);

        // First, serialize //
        String json = objectMapper.writeValueAsString(jsonApiEnvelope);
        Assert.assertNotNull(json);

        // Then, attempt to deserialize and verify //
        SimpleJsonApiEnvelope<AChildClass> deserializedObject = objectMapper.readValue(json, SimpleJsonApiEnvelope.class);
        Assert.assertNotNull(deserializedObject);
        Assert.assertTrue(jsonApiEnvelope.equals(deserializedObject));
    }

    @Test
    public void listOfObjectsTest() throws Exception {
        // Init Test Objects //
        List<SimpleObject> simpleObjectList = new ArrayList<SimpleObject>();
        for (int i1 = 0; i1 < 4; i1++) {
            SimpleSubObject simpleSubObject = new SimpleSubObject();
            simpleSubObject.setId("Object Number " + i1);
            simpleSubObject.setBaz("Baz " + (i1 * i1));

            SimpleObject simpleObject = new SimpleObject();
            simpleObject.setId((i1 * 10l));
            simpleObject.setAttribute("an attribute!");
            simpleObject.setThing2(simpleSubObject);

            simpleObjectList.add(simpleObject);
        }
        SimpleJsonApiEnvelope<List<SimpleObject>> jsonApiEnvelope = new SimpleJsonApiEnvelope<List<SimpleObject>>(simpleObjectList);

        // Serialize! //
        String json = objectMapper.writeValueAsString(jsonApiEnvelope);
        Assert.assertNotNull(json);

        // Verify!! //
        SimpleJsonApiEnvelope<List<SimpleObject>> deserializedObject = objectMapper.readValue(json, SimpleJsonApiEnvelope.class);
        Assert.assertNotNull(deserializedObject);
        Assert.assertTrue(jsonApiEnvelope.equals(deserializedObject));
    }

    @Test
    public void serializeAndDeserializeWithoutIncludedTest() throws Exception {
        // Init Test Objects //
        SingleLinkNode tailNode = new SingleLinkNode();
        tailNode.setId(222222l);
        tailNode.setValue("the last node...");

        SingleLinkNode headNode = new SingleLinkNode();
        headNode.setId(111111l);
        headNode.setValue("the first node!");
        headNode.setLinkNode(tailNode);

        // Serialize! Without includeds! //
        String json = objectMapper.writeValueAsString(new SimpleJsonApiEnvelope<SingleLinkNode>(headNode, JsonApiOptions.OMIT_INCLUDED_BLOCK));
        Assert.assertNotNull(json);
        Assert.assertTrue(json.contains(headNode.getValue()));
        Assert.assertFalse(json.contains("included"));
        Assert.assertFalse(json.contains(tailNode.getValue()));

        // Verify //
        SimpleJsonApiEnvelope<SingleLinkNode> result = objectMapper.readValue(json, SimpleJsonApiEnvelope.class);
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getData());

        SingleLinkNode resultNode = result.getData();
        Assert.assertEquals(headNode.getId(), resultNode.getId());
        Assert.assertEquals(headNode.getValue(), resultNode.getValue());

        Assert.assertNotNull(headNode.getLinkNode());
        SingleLinkNode resultTailNode = resultNode.getLinkNode();
        Assert.assertEquals(tailNode.getId(), resultTailNode.getId());
        Assert.assertNull(resultTailNode.getValue());
        Assert.assertNull(resultTailNode.getLinkNode());
    }

    @Test
    public void serializeAndDeserializeRelationshipListWithoutIncludedTest() throws Exception {
        // Init Test Objects //
        SimpleObjectWithListRelationship testSub1 = new SimpleObjectWithListRelationship();
        testSub1.setId("two");
        testSub1.setText("secondary");

        SimpleObjectWithListRelationship testSub2 = new SimpleObjectWithListRelationship();
        testSub2.setId("thr33");
        testSub2.setText("final");

        List<SimpleObjectWithListRelationship> subs = new ArrayList<>();
        subs.add(testSub1);
        subs.add(testSub2);

        SimpleObjectWithListRelationship testMain = new SimpleObjectWithListRelationship();
        testMain.setId("1");
        testMain.setText("main");
        testMain.setRelations(subs);

        // Serialize! Without includeds! //
        String json = objectMapper.writeValueAsString(new SimpleJsonApiEnvelope<SimpleObjectWithListRelationship>(testMain, JsonApiOptions.OMIT_INCLUDED_BLOCK));
        Assert.assertNotNull(json);
        Assert.assertTrue(json.contains(testMain.getText()));
        Assert.assertFalse(json.contains("included"));
        Assert.assertTrue(json.contains(testSub1.getId()));
        Assert.assertFalse(json.contains(testSub1.getText()));
        Assert.assertTrue(json.contains(testSub2.getId()));
        Assert.assertFalse(json.contains(testSub2.getText()));

        // Verify //
        SimpleJsonApiEnvelope<SimpleObjectWithListRelationship> result = objectMapper.readValue(json, SimpleJsonApiEnvelope.class);
        Assert.assertNotNull(result);
        SimpleObjectWithListRelationship resultMainObject = result.getData();
        Assert.assertNotNull(resultMainObject);
        Assert.assertEquals(testMain.getId(), resultMainObject.getId());
        Assert.assertEquals(testMain.getText(), resultMainObject.getText());

        Assert.assertEquals(2, resultMainObject.getRelations().size());
        SimpleObjectWithListRelationship subObject1 = resultMainObject.getRelations().get(0);
        Assert.assertEquals(testSub1.getId(), subObject1.getId());
        Assert.assertNull(subObject1.getText());

        SimpleObjectWithListRelationship subObject2 = resultMainObject.getRelations().get(1);
        Assert.assertEquals(testSub2.getId(), subObject2.getId());
        Assert.assertNull(subObject2.getText());
    }

}
