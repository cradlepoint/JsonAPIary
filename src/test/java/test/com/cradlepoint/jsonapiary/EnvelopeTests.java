package test.com.cradlepoint.jsonapiary;

import com.cradlepoint.jsonapiary.envelopes.SimpleJsonApiEnvelope;
import org.junit.Test;
import test.com.cradlepoint.jsonapiary.pojos.SimpleObject;

import java.util.ArrayList;
import java.util.List;

public class EnvelopeTests {

    /////////////////
    // Constructor //
    /////////////////

    public EnvelopeTests() { }

    ///////////
    // Tests //
    ///////////

    @Test
    public void SingleObjectHappyPathTest() throws Exception {
        new SimpleJsonApiEnvelope<SimpleObject>(new SimpleObject());
    }

    @Test(expected = IllegalArgumentException.class)
    public void SingleObjectNonAnnotatedTypeTest() throws Exception {
        new SimpleJsonApiEnvelope<InstantiationException>(new InstantiationException());
    }

    @Test
    public void ListObjectHappyPathTest() throws Exception {
        List<SimpleObject> simpleObjectList = new ArrayList<SimpleObject>();
        for(int i1 = 0; i1 < 16; i1++) {
            simpleObjectList.add(new SimpleObject());
        }
        new SimpleJsonApiEnvelope<List<SimpleObject>>(simpleObjectList);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ListObjectPartiallyAnnotatedTypeTest() throws Exception {
        List<Object> simpleObjectList = new ArrayList<Object>();
        for(int i1 = 0; i1 < 8; i1++) {
            simpleObjectList.add(new SimpleObject());
        }
        simpleObjectList.add(new InstantiationException());
        new SimpleJsonApiEnvelope<InstantiationException>(new InstantiationException());
    }

}
