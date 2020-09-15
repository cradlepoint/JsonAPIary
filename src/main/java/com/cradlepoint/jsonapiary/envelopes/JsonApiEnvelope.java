package com.cradlepoint.jsonapiary.envelopes;

import java.util.List;
import java.util.Map;

public interface JsonApiEnvelope<T> {

    //////////////////////
    // Abstract Methods //
    //////////////////////

    T getData();
    void setData(T data);
    Map<String, String> getLinks();
    void addLink(String key, String url);
    Map<String, String> getMeta();
    void addMeta(String key, String value);
    boolean containsOption(JsonApiOptions option);
    List<JsonApiOptions> getOptions();

}
