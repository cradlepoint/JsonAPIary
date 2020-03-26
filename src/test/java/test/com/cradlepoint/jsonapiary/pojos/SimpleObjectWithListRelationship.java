package test.com.cradlepoint.jsonapiary.pojos;

import com.cradlepoint.jsonapiary.annotations.JsonApiAttribute;
import com.cradlepoint.jsonapiary.annotations.JsonApiId;
import com.cradlepoint.jsonapiary.annotations.JsonApiRelationship;
import com.cradlepoint.jsonapiary.annotations.JsonApiType;

import java.util.List;

@JsonApiType("RelationshipThing")
public class SimpleObjectWithListRelationship {

    ////////////////
    // Attributes //
    ////////////////

    @JsonApiId
    private String id;

    @JsonApiAttribute("text")
    private String text;

    @JsonApiRelationship("rels")
    private List<SimpleObjectWithListRelationship> relations;

    /////////////////
    // Constructor //
    /////////////////

    public SimpleObjectWithListRelationship() { }

    /////////////////////////
    // Getters and Setters //
    /////////////////////////

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<SimpleObjectWithListRelationship> getRelations() {
        return this.relations;
    }

    public void setRelations(List<SimpleObjectWithListRelationship> relations) {
        this.relations = relations;
    }

}
