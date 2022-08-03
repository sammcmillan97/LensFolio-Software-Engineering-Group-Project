package nz.ac.canterbury.seng302.portfolio.model;

import java.util.Date;

/**
 * Stores date restriction information for the project date service method getDateRestrictions.
 */
public record DateRestriction(Date date, String text) {

}
