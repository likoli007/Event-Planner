package cz.muni.fi.pv168.project.model;

import java.awt.Color;
import java.util.Objects;

public class Category extends Entity {
    private String name;

    private Color color;

    public Category(String name, Color color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Category getCategory(){
        return this;
    }

    @Override
    public boolean isDuplicate(Entity e) {
        if (e == null || getClass() != e.getClass()) return false;
        Category category = (Category) e;
        return Objects.equals(name, category.name);
    }

    @Override
    public String toString() {
        return name;
    }
}