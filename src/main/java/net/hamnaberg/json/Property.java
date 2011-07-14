package net.hamnaberg.json;

public class Property implements WithPrompt, Nameable {
    private final String name;
    private final Value value;
    private final String prompt;

    public Property(String name, Value value, String prompt) {
        this.name = name;
        this.value = value;
        this.prompt = prompt;
    }

    public String getName() {
        return name;
    }

    public Value getValue() {
        return value;
    }

    public String getPrompt() {
        return prompt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Property property = (Property) o;

        if (name != null ? !name.equals(property.name) : property.name != null) return false;
        if (prompt != null ? !prompt.equals(property.prompt) : property.prompt != null) return false;
        if (value != null ? !value.equals(property.value) : property.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (prompt != null ? prompt.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return String.format("Property with name %s, value %s, prompt %s", name, value, prompt);
    }
}
