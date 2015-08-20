package org.zendesk.client.v2.model.events;

/**
 * @author stephenc
 * @since 05/04/2013 11:53
 */
abstract class PublicPrivateEvent extends Event {

    private static final long serialVersionUID = 1L;

    private Boolean publicComment;

    public Boolean getPublic() {
        return publicComment;
    }

    public void setPublic(Boolean publicComment) {
        this.publicComment = publicComment;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("PublicPrivateEvent");
        sb.append("{publicComment=").append(publicComment);
        sb.append('}');
        return sb.toString();
    }
}
