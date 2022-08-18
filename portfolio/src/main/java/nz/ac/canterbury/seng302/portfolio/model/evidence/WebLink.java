package nz.ac.canterbury.seng302.portfolio.model.evidence;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Embeddable class to allow weblinks to be stored in evidence without creating a weblinks entity.
 * Easiest way to store name and weblink together.
 */
@Embeddable
public class WebLink {
    String name;
    boolean safe;
    @Column(columnDefinition = "LONGTEXT")
    String link;

    public WebLink(String webLink, String name) {
        this.name = name;
        if (webLink.matches("http://.*")) {
            this.link = webLink.replaceFirst("http://", "");
            this.safe = false;
        } else if (webLink.matches("https://.*")) {
            this.link = webLink.replaceFirst("https://", "");
            this.safe = true;
        }

    }

    public WebLink(String webLink) {
        if (webLink.matches("http://.*")) {
            this.link = webLink.replaceFirst("http://", "");
            this.safe = false;
        } else if (webLink.matches("https://.*")) {
            this.link = webLink.replaceFirst("https://", "");
            this.safe = true;
        }
    }

    public WebLink() {
    }

    public String getName() {
        return name;
    }

    public String getLink() {
        return link;
    }

    public boolean isSafe() {
        return this.safe;
    }
}
