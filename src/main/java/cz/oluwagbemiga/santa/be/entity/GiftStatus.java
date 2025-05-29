package cz.oluwagbemiga.santa.be.entity;

import lombok.Getter;

@Getter
public enum GiftStatus {
    CREATED("Gift has been created and is waiting to be selected."),
    SELECTED("Gift has been selected by a participant."),
    LINKED("Gift has been linked to a affiliate link."),
    SENT("Gift has been sent to the participant.");

    private final String message;

    GiftStatus(String message) {
        this.message = message;
    }
    
    public String getValue() {
        return this.name().replace('_', ' ');
    }

}




