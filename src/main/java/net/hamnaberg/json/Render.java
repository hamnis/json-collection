package net.hamnaberg.json;

public final class Render {
    public static final Render Link = new Render("link");
    public static final Render Image = new Render("image");

    private final String name;

    private Render(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


    public static Render valueOf(String name) {
        if (Link.getName().equalsIgnoreCase(name)) {
            return Link;
        }
        else if (Image.getName().equalsIgnoreCase(name)) {
            return Image;
        }
        else {
            return new Render(name);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Render render = (Render) o;

        if (name != null ? !name.equals(render.name) : render.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
