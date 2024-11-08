package cz.muni.fi.pv168.project.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cz.muni.fi.pv168.project.service.export.serialize.ColorSerializer;

import java.awt.Color;

public class Category extends Entity {
    private String name;

    @JsonSerialize (using = ColorSerializer.class)
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

    @JsonIgnore
    public Category getCategory(){
        return this;
    }

    @Override
    public String toString() {
        return name;
    }
}