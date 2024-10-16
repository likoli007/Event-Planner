package cz.muni.fi.pv168.project.model;

public class Category {
    private String name;
    private Color color;

    public Category(String name, Color color) {
        this.name = name;
        this.color = color;
    }

    @Override
    public String toString() {
        return name;
    }
}