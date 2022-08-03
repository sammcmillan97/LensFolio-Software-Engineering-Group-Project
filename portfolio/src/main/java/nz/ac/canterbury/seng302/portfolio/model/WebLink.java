package nz.ac.canterbury.seng302.portfolio.model;

import javax.persistence.Embeddable;

@Embeddable
public class WebLink {
    String name;
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
