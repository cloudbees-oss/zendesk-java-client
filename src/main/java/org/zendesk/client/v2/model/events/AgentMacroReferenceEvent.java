package org.zendesk.client.v2.model.events;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.zendesk.client.v2.model.Via;

public class AgentMacroReferenceEvent extends Event {

    private static final long serialVersionUID = 1L;

    private Long macroId;
    private String macroTitle;
    private Via via;

    @JsonProperty("macro_id")
    public Long getMacroId() {
        return macroId;
    }

    public void setMacroId(Long macroId) {
        this.macroId = macroId;
    }

    @JsonProperty("macro_title")
    public String getMacroTitle() {
        return macroTitle;
    }

    public void setMacroTitle(String macroTitle) {
        this.macroTitle = macroTitle;
    }

    public Via getVia() {
        return via;
    }

    public void setVia(Via via) {
        this.via = via;
    }

    @Override
    public String toString() {
        return "AgentMacroReferenceEvent{" + "macroId=" + macroId +
                ", macroTitle='" + macroTitle + '\'' +
                ", via=" + via +
                '}';
    }
}
