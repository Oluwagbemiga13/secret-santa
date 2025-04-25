package cz.oluwagbemiga.santa.be.entity;

public enum ListStatus {
    CREATED,
    PEOPLE_SELECTING_GIFTS,
    SENT;

    public String getValue() {
        return this.name();
    }
}
