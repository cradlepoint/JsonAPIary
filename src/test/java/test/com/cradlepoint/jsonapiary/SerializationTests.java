package test.com.cradlepoint.jsonapiary;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.cradlepoint.jsonapiary.JsonApiModule;
import com.cradlepoint.jsonapiary.envelopes.JsonApiEnvelope;
import org.junit.Assert;
import org.junit.Test;
import test.com.cradlepoint.jsonapiary.pojos.SimpleNestedSubObject;
import test.com.cradlepoint.jsonapiary.pojos.SimpleObject;
import test.com.cradlepoint.jsonapiary.pojos.SimpleSubObject;
import test.com.cradlepoint.jsonapiary.pojos.SingleLinkNode;

public class SerializationTests {

    ////////////////
    // Attributes //
    ////////////////

    private ObjectMapper objectMapper;

    /////////////////
    // Constructor //
    /////////////////

    public SerializationTests() {
        JsonApiModule jsonApiModule = new JsonApiModule(
                SimpleObject.class,
                SimpleSubObject.class,
                SimpleNestedSubObject.class,
                SingleLinkNode.class);

        objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.registerModule(jsonApiModule);
    }

    ///////////
    // Tests //
    ///////////

    @Test
    public void circularReferenceTest() throws Exception {
        // Init Test Objects //
        SingleLinkNode singleLinkNode1 = new SingleLinkNode();
        SingleLinkNode singleLinkNode2 = new SingleLinkNode();
        SingleLinkNode singleLinkNode3 = new SingleLinkNode();

        singleLinkNode1.setId(1.0);
        singleLinkNode2.setId(2.0);
        singleLinkNode3.setId(3.0);

        singleLinkNode1.setLinkNode(singleLinkNode2);
        singleLinkNode2.setLinkNode(singleLinkNode3);
        singleLinkNode3.setLinkNode(singleLinkNode1);

        // Serialize //
        String json = objectMapper.writeValueAsString(new JsonApiEnvelope<SingleLinkNode>(singleLinkNode1));

        Assert.assertTrue(json.equals("{\n" +
                "  \"data\" : {\n" +
                "    \"id\" : 1.0,\n" +
                "    \"type\" : \"SingleLinkNode\",\n" +
                "    \"attributes\" : {\n" +
                "      \"value\" : \"Logan was here 1.0 times!\"\n" +
                "    },\n" +
                "    \"relationships\" : {\n" +
                "      \"link\" : {\n" +
                "        \"data\" : {\n" +
                "          \"id\" : 2.0,\n" +
                "          \"type\" : \"SingleLinkNode\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  },\n" +
                "  \"included\" : [ {\n" +
                "    \"id\" : 2.0,\n" +
                "    \"type\" : \"SingleLinkNode\",\n" +
                "    \"attributes\" : {\n" +
                "      \"value\" : \"Logan was here 2.0 times!\"\n" +
                "    },\n" +
                "    \"relationships\" : {\n" +
                "      \"link\" : {\n" +
                "        \"data\" : {\n" +
                "          \"id\" : 3.0,\n" +
                "          \"type\" : \"SingleLinkNode\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }, {\n" +
                "    \"id\" : 3.0,\n" +
                "    \"type\" : \"SingleLinkNode\",\n" +
                "    \"attributes\" : {\n" +
                "      \"value\" : \"Logan was here 3.0 times!\"\n" +
                "    },\n" +
                "    \"relationships\" : {\n" +
                "      \"link\" : {\n" +
                "        \"data\" : {\n" +
                "          \"id\" : 1.0,\n" +
                "          \"type\" : \"SingleLinkNode\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  } ]\n" +
                "}"));
    }

    @Test
    public void complexRelationshipSerializationTest() throws Exception {
        SimpleNestedSubObject simpleNestedSubObject = new SimpleNestedSubObject();
        simpleNestedSubObject.setIid(54321l);
        simpleNestedSubObject.setNestedThing("qwerty");

        SimpleSubObject simpleSubObject = new SimpleSubObject(8);
        simpleSubObject.setId("AnID");
        simpleSubObject.setNestedThing(simpleNestedSubObject);
        long counter = 1;
        for(SimpleNestedSubObject nestedSubObject : simpleSubObject.getNestedThings()) {
            nestedSubObject.setIid(counter++);
            nestedSubObject.setNestedThing("Loooooooogan #" + counter);
        }

        SimpleObject simpleObject = new SimpleObject();
        simpleObject.setId(9876l);
        simpleObject.setAttribute("AtTrIbUtE!");
        simpleObject.setThing2(simpleSubObject);

        SimpleObject simpleObject2 = new SimpleObject();
        simpleObject2.setId(4567l);
        simpleObject2.setAttribute("simpleObject2");
        simpleNestedSubObject.setMetaThing(simpleObject2);

        // First Serialize //
        String json = objectMapper.writeValueAsString(new JsonApiEnvelope<SimpleObject>(simpleObject));

        Assert.assertTrue(json.equals("{\n" +
                "  \"data\" : {\n" +
                "    \"id\" : 9876,\n" +
                "    \"type\" : \"SimpleObject\",\n" +
                "    \"attributes\" : {\n" +
                "      \"objectAttribute\" : \"AtTrIbUtE!\"\n" +
                "    },\n" +
                "    \"meta\" : {\n" +
                "      \"catchAllThing\" : \"this should ahve been caught\",\n" +
                "      \"objectBlah\" : \"blah!\"\n" +
                "    },\n" +
                "    \"relationships\" : {\n" +
                "      \"someRelationship\" : null,\n" +
                "      \"someOtherRelationship\" : {\n" +
                "        \"data\" : {\n" +
                "          \"id\" : \"AnID\",\n" +
                "          \"type\" : \"TypeOverride\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  },\n" +
                "  \"included\" : [ {\n" +
                "    \"id\" : \"AnID\",\n" +
                "    \"type\" : \"TypeOverride\",\n" +
                "    \"attributes\" : {\n" +
                "      \"BAZ\" : \"...BAZ...! 8\"\n" +
                "    },\n" +
                "    \"relationships\" : {\n" +
                "      \"NESTEDnestedNESTED\" : {\n" +
                "        \"data\" : [ {\n" +
                "          \"id\" : 1,\n" +
                "          \"type\" : \"SimpleNestedSubObject\"\n" +
                "        }, {\n" +
                "          \"id\" : 2,\n" +
                "          \"type\" : \"SimpleNestedSubObject\"\n" +
                "        }, {\n" +
                "          \"id\" : 3,\n" +
                "          \"type\" : \"SimpleNestedSubObject\"\n" +
                "        }, {\n" +
                "          \"id\" : 4,\n" +
                "          \"type\" : \"SimpleNestedSubObject\"\n" +
                "        }, {\n" +
                "          \"id\" : 5,\n" +
                "          \"type\" : \"SimpleNestedSubObject\"\n" +
                "        }, {\n" +
                "          \"id\" : 6,\n" +
                "          \"type\" : \"SimpleNestedSubObject\"\n" +
                "        }, {\n" +
                "          \"id\" : 7,\n" +
                "          \"type\" : \"SimpleNestedSubObject\"\n" +
                "        }, {\n" +
                "          \"id\" : 8,\n" +
                "          \"type\" : \"SimpleNestedSubObject\"\n" +
                "        } ]\n" +
                "      },\n" +
                "      \"SINGLEnestedTHING\" : {\n" +
                "        \"data\" : {\n" +
                "          \"id\" : 54321,\n" +
                "          \"type\" : \"SimpleNestedSubObject\"\n" +
                "        },\n" +
                "        \"meta\" : {\n" +
                "          \"SOMEmetaSIMPLEobjectttttt\" : {\n" +
                "            \"objectId\" : 4567,\n" +
                "            \"objectAttribute\" : \"simpleObject2\",\n" +
                "            \"ignoredThing\" : \"THIS SHOULDN'T BE HERE\",\n" +
                "            \"objectBlah\" : \"blah!\",\n" +
                "            \"catchAllThing\" : \"this should ahve been caught\"\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }, {\n" +
                "    \"id\" : 54321,\n" +
                "    \"type\" : \"SimpleNestedSubObject\",\n" +
                "    \"attributes\" : {\n" +
                "      \"aNestedTHING\" : \"qwerty\"\n" +
                "    },\n" +
                "    \"meta\" : {\n" +
                "      \"SOMEmetaSIMPLEobjectttttt\" : {\n" +
                "        \"objectId\" : 4567,\n" +
                "        \"objectAttribute\" : \"simpleObject2\",\n" +
                "        \"ignoredThing\" : \"THIS SHOULDN'T BE HERE\",\n" +
                "        \"objectBlah\" : \"blah!\",\n" +
                "        \"catchAllThing\" : \"this should ahve been caught\"\n" +
                "      }\n" +
                "    }\n" +
                "  }, {\n" +
                "    \"id\" : 1,\n" +
                "    \"type\" : \"SimpleNestedSubObject\",\n" +
                "    \"attributes\" : {\n" +
                "      \"aNestedTHING\" : \"Loooooooogan #2\"\n" +
                "    },\n" +
                "    \"meta\" : {\n" +
                "      \"SOMEmetaSIMPLEobjectttttt\" : null\n" +
                "    }\n" +
                "  }, {\n" +
                "    \"id\" : 2,\n" +
                "    \"type\" : \"SimpleNestedSubObject\",\n" +
                "    \"attributes\" : {\n" +
                "      \"aNestedTHING\" : \"Loooooooogan #3\"\n" +
                "    },\n" +
                "    \"meta\" : {\n" +
                "      \"SOMEmetaSIMPLEobjectttttt\" : null\n" +
                "    }\n" +
                "  }, {\n" +
                "    \"id\" : 3,\n" +
                "    \"type\" : \"SimpleNestedSubObject\",\n" +
                "    \"attributes\" : {\n" +
                "      \"aNestedTHING\" : \"Loooooooogan #4\"\n" +
                "    },\n" +
                "    \"meta\" : {\n" +
                "      \"SOMEmetaSIMPLEobjectttttt\" : null\n" +
                "    }\n" +
                "  }, {\n" +
                "    \"id\" : 4,\n" +
                "    \"type\" : \"SimpleNestedSubObject\",\n" +
                "    \"attributes\" : {\n" +
                "      \"aNestedTHING\" : \"Loooooooogan #5\"\n" +
                "    },\n" +
                "    \"meta\" : {\n" +
                "      \"SOMEmetaSIMPLEobjectttttt\" : null\n" +
                "    }\n" +
                "  }, {\n" +
                "    \"id\" : 5,\n" +
                "    \"type\" : \"SimpleNestedSubObject\",\n" +
                "    \"attributes\" : {\n" +
                "      \"aNestedTHING\" : \"Loooooooogan #6\"\n" +
                "    },\n" +
                "    \"meta\" : {\n" +
                "      \"SOMEmetaSIMPLEobjectttttt\" : null\n" +
                "    }\n" +
                "  }, {\n" +
                "    \"id\" : 6,\n" +
                "    \"type\" : \"SimpleNestedSubObject\",\n" +
                "    \"attributes\" : {\n" +
                "      \"aNestedTHING\" : \"Loooooooogan #7\"\n" +
                "    },\n" +
                "    \"meta\" : {\n" +
                "      \"SOMEmetaSIMPLEobjectttttt\" : null\n" +
                "    }\n" +
                "  }, {\n" +
                "    \"id\" : 7,\n" +
                "    \"type\" : \"SimpleNestedSubObject\",\n" +
                "    \"attributes\" : {\n" +
                "      \"aNestedTHING\" : \"Loooooooogan #8\"\n" +
                "    },\n" +
                "    \"meta\" : {\n" +
                "      \"SOMEmetaSIMPLEobjectttttt\" : null\n" +
                "    }\n" +
                "  }, {\n" +
                "    \"id\" : 8,\n" +
                "    \"type\" : \"SimpleNestedSubObject\",\n" +
                "    \"attributes\" : {\n" +
                "      \"aNestedTHING\" : \"Loooooooogan #9\"\n" +
                "    },\n" +
                "    \"meta\" : {\n" +
                "      \"SOMEmetaSIMPLEobjectttttt\" : null\n" +
                "    }\n" +
                "  } ]\n" +
                "}"));
    }

}
