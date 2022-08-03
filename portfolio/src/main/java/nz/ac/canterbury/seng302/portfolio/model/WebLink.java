package nz.ac.canterbury.seng302.portfolio.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Embeddable class to allow weblinks to be stored in evidence without creating a weblinks entity.
 * Easiest way to store name and weblink together.
 */
@Embeddable
public class WebLink {
    String name;
    @Column(columnDefinition = "LONGTEXT")
    String link;

    public WebLink(String webLink, String name) {
        this.name = name;
        this.link = webLink;
    }

    public WebLink(String webLink) {
        this.link = webLink;
    }

    public WebLink() {
    }

    public String getName() {
        return name;
    }

    public String getLink() {
        return link;
    }
}
