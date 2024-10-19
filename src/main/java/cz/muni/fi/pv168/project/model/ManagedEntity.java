package cz.muni.fi.pv168.project.model;


// Enum used during combo-box selection in the 'Manager' tab
// could be useful in the future to keep knowledge of which entity is being managed by the program
public enum ManagedEntity {
    CATEGORIES("Categories"),
    TEMPLATES("Templates"),
    INTERVALS("Intervals");

    private final String displayName;

    ManagedEntity(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
