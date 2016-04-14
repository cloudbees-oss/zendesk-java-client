package org.zendesk.client.v2.model;

import java.util.List;

/**
 * @author stephenc
 * @since 05/04/2013 15:38
 */
public class Attachment extends Photo {

    private static final long serialVersionUID = 1L;

    private List<Photo> thumbnails;

    public List<Photo> getThumbnails() {
        return thumbnails;
    }

    public void setThumbnails(List<Photo> thumbnails) {
        this.thumbnails = thumbnails;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Attachment");
        sb.append("{id=").append(getId());
        sb.append(", fileName='").append(getFileName()).append('\'');
        sb.append(", contentType='").append(getContentType()).append('\'');
        sb.append(", contentUrl='").append(getContentUrl()).append('\'');
        sb.append(", size=").append(getSize());
        sb.append(", thumbnails=").append(thumbnails);
        sb.append('}');
        return sb.toString();
    }

    public static class Upload {
        private String token;
        private List<Attachment> attachments;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public List<Attachment> getAttachments() {
            return attachments;
        }

        public void setAttachments(List<Attachment> attachments) {
            this.attachments = attachments;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("Upload");
            sb.append("{token='").append(token).append('\'');
            sb.append(", attachments=").append(attachments);
            sb.append('}');
            return sb.toString();
        }
    }
}
