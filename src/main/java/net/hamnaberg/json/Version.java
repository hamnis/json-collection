package net.hamnaberg.json;

public enum Version {
    ONE("1.0");

    private String identifier;

    Version(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    public static Version getVersion(String id) {
        for (Version version : values()) {
            if (version.getIdentifier().equals(id)) {
                return version;
            }
        }
        return ONE;
    }
}
