package test.com.cradlepoint.jsonapiary.envelopes;

import com.cradlepoint.jsonapiary.envelopes.JsonApiEnvelope;
import com.cradlepoint.jsonapiary.envelopes.JsonApiOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomTestEnvelope<T> implements JsonApiEnvelope<T> {

    ////////////////
    // Attributes //
    ////////////////

    public T data;
    public Map<String, String> links;
    public Map<String, String> meta;
    List<JsonApiOptions> options;

    /////////////////
    // Constructor //
    /////////////////

    public CustomTestEnvelope() {
        links = new HashMap<String, String>();
        meta = new HashMap<String, String>();
        options = new ArrayList<JsonApiOptions>();
    }

    public CustomTestEnvelope(T data) {
        this.data = data;
        links = new HashMap<String, String>();
        meta = new HashMap<String, String>();
        options = new ArrayList<JsonApiOptions>();
    }


    ///////////////
    // Overrides //
    ///////////////

    @Override
    public T getData() {
        return this.data;
    }

    @Override
    public void setData(T data) {
        this.data = data;
    }

    @Override
    public Map<String, String> getLinks() {
        return this.links;
    }

    @Override
    public void addLink(String key, String url) {
        this.links.put(key, url);
    }

    @Override
    public Map<String, String> getMeta() {
        return this.meta;
    }

    @Override
    public void addMeta(String key, String value) {
        this.meta.put(key, value);
    }

    @Override
    public boolean containsOption(JsonApiOptions option) {
        return options.contains(option);
    }

    @Override
    public List<JsonApiOptions> getOptions() {
        return options;
    }

}
