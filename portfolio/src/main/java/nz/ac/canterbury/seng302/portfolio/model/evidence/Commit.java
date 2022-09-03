package nz.ac.canterbury.seng302.portfolio.model.evidence;

import javax.persistence.Embeddable;
import java.util.Date;

@Embeddable
public class Commit {
    String Author;
    Date date;
    String link;
    String description;

    public Commit(String author, Date date, String link, String description) {
        this.Author = author;
        this.date = date;
        this.link = link;
        this.description = description;
    }

    public Commit() {
    }

    public String getAuthor() {
        return Author;
    }

    public void setAuthor(String author) {
        Author = author;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
