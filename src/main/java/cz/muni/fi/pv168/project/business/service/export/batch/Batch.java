package cz.muni.fi.pv168.project.business.service.export.batch;

import cz.muni.fi.pv168.project.model.Category;
import cz.muni.fi.pv168.project.model.Template;
import cz.muni.fi.pv168.project.model.TimeUnit;
import cz.muni.fi.pv168.project.model.TodoEvent;

import java.util.Collection;

public record Batch(Collection<Category> categories, Collection<TimeUnit> timeUnits,
                    Collection<Template> templates, Collection<TodoEvent> events
                    ) {
}
