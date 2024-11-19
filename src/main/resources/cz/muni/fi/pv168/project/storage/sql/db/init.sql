CREATE TABLE IF NOT EXISTS Category (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    color VARCHAR(7) NOT NULL
);

CREATE TABLE IF NOT EXISTS TimeUnit (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    shortcut VARCHAR(50) NOT NULL,
    minutes INT NOT NULL
);

CREATE TABLE IF NOT EXISTS Template (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    details TEXT,
    startTime TIME NOT NULL,
    timeUnit UUID NOT NULL,
    timeUnitAmount INT NOT NULL,
    FOREIGN KEY (timeUnit) REFERENCES TimeUnit(id)
);

CREATE TABLE IF NOT EXISTS Template_Category (
    template_id UUID NOT NULL,
    category_id UUID NOT NULL,
    PRIMARY KEY (template_id, category_id),
    FOREIGN KEY (template_id) REFERENCES Template(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES Category(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS TodoEvent (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    details TEXT,
    start TIMESTAMP NOT NULL,
    timeUnit UUID NOT NULL,
    done BOOLEAN DEFAULT FALSE NOT NULL,
    FOREIGN KEY (timeUnit) REFERENCES TimeUnit(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS TodoEvent_Category (
    todo_event_id UUID NOT NULL,
    category_id UUID NOT NULL,
    PRIMARY KEY (todo_event_id, category_id),
    FOREIGN KEY (todo_event_id) REFERENCES TodoEvent(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES Category(id) ON DELETE CASCADE
);