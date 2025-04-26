package cz.oluwagbemiga.santa.be.entity;

public enum ListStatus {
    CREATED("List needs to be sent to participants"),
    PEOPLE_SELECTING_GIFTS("People are selecting their gifts."),
    SENT_TO_SANTA("Everyone recieved instructions what gift they are giving."),;

    private final String message;

    ListStatus(String message) {
        this.message = message;
    }

    public String getValue() {
        return this.name().replace('_', ' ');
    }

    public String getMessage() {
        return this.message;
    }
}