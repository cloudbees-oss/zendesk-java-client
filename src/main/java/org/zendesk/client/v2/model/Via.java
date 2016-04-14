package org.zendesk.client.v2.model;

import java.io.Serializable;
import java.util.Map;

/**
 * @author stephenc
 * @since 04/04/2013 17:00
 */
public class Via implements Serializable {

    private static final long serialVersionUID = 1L;

    private String channel;
    private Map<String, Object> source;

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public Map<String, Object> getSource() {
        return source;
    }

    public void setSource(Map<String, Object> source) {
        this.source = source;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Via");
        sb.append("{channel='").append(channel).append('\'');
        sb.append(", source=").append(source);
        sb.append('}');
        return sb.toString();
    }
}
