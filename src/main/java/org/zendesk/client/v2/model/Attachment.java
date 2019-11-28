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
        return "Attachment" +
                "{id=" + getId() +
                ", fileName='" + getFileName() + '\'' +
                ", contentType='" + getContentType() + '\'' +
                ", contentUrl='" + getContentUrl() + '\'' +
                ", size=" + getSize() +
                ", thumbnails=" + thumbnails +
                '}';
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
            return "Upload" +
                    "{token='" + token + '\'' +
                    ", attachments=" + attachments +
                    '}';
        }
    }
}
