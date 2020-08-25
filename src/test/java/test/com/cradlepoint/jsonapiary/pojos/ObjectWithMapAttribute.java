package test.com.cradlepoint.jsonapiary.pojos;

import com.cradlepoint.jsonapiary.annotations.JsonApiAttribute;
import com.cradlepoint.jsonapiary.annotations.JsonApiId;
import com.cradlepoint.jsonapiary.annotations.JsonApiMeta;
import com.cradlepoint.jsonapiary.annotations.JsonApiType;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Hashtable;
import java.util.Map;

@JsonApiType(value = "mapAttributeObject")
public class ObjectWithMapAttribute {

    ////////////////
    // Attributes //
    ////////////////

    @JsonApiId
    private String id;

    @JsonProperty(value = "someMap")
    @JsonApiMeta
    private Map<String, String> someMap;

    /////////////////
    // Constructor //
    /////////////////

    public ObjectWithMapAttribute() { }

    ////////////////////
    // Public Methods //
    ////////////////////

    public void addToMap(String key, String value) {
        if(someMap == null) {
            someMap = new Hashtable<String, String>();
        }
        someMap.put(key, value);
    }

    /////////////////////////
    // Getters and Setters //
    /////////////////////////

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, String> getSomeMap() {
        return this.someMap;
    }

    public void setSomeMap(Map<String, String> someMap) {
        this.someMap = someMap;
    }

}
