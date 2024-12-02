package cz.muni.fi.pv168.project.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cz.muni.fi.pv168.project.business.service.export.serialize.ColorSerializer;

import java.awt.Color;
import java.util.Objects;

public class Category extends Entity {
    private String name;

    @JsonSerialize (using = ColorSerializer.class)
    private Color color;

    public Category(String name, Color color) {
        this.name = name;
        this.color = color;
    }

    public Category(Category category) {
        this.id = category.id;
        this.name = category.name;
        this.color = category.color;
    }

    @Override
    public void update(Entity e) {
        if (!(e instanceof Category category)) {
            throw new IllegalArgumentException("Cannot update object of different class");
        }

        this.id = category.id;
        this.name = category.name;
        this.color = category.color;
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

    @JsonIgnore
    public Category getCategory(){
        return this;
    }

    @Override
    public boolean isDuplicate(Entity e) {
        if (e == null || getClass() != e.getClass()) return false;
        Category category = (Category) e;
        return Objects.equals(name, category.name);
    }

    //if both the color RGB and name matches, the entity is not meaningfully different
    @Override
    public boolean isMeaningfullyDifferent(Entity e) {
        if (e == null || getClass() != e.getClass()) return true;
        Category category = (Category) e;
        if (Objects.equals(name, category.name) && color.getRGB() == ((Category) e).getColor().getRGB()) return false;
        return true;
    }

    @Override
    public String toString() {
        return name;
    }
}