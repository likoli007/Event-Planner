CREATE TABLE IF NOT EXISTS Category (
    id UUID PRIMARY KEY,
    name VARCHAR(25) NOT NULL,
    color VARCHAR(7) NOT NULL
);

CREATE TABLE IF NOT EXISTS TimeUnit (
    id UUID PRIMARY KEY,
    name VARCHAR(25) NOT NULL,
    shortcut VARCHAR(4) NOT NULL,
    minutes         INT                   NOT NULL CHECK (minutes >= 0),
    isSystemDefined BOOLEAN DEFAULT FALSE NOT NULL
);

CREATE TABLE IF NOT EXISTS Template (
    id UUID PRIMARY KEY,
    name VARCHAR(25) NOT NULL,
    details VARCHAR(36),
    startTime TIME NOT NULL,
    timeUnit UUID NOT NULL,
    timeUnitAmount INT NOT NULL CHECK (timeUnitAmount >= 0),
    FOREIGN KEY (timeUnit) REFERENCES TimeUnit(id)
);

CREATE TABLE IF NOT EXISTS Template_Category (
    template_id UUID NOT NULL,
    category_id UUID NOT NULL,
    PRIMARY KEY (template_id, category_id),
    FOREIGN KEY (template_id) REFERENCES Template(id),
    FOREIGN KEY (category_id) REFERENCES Category(id)
);

CREATE TABLE IF NOT EXISTS TodoEvent (
    id UUID PRIMARY KEY,
    name VARCHAR(25) NOT NULL,
    details VARCHAR(36),
    start TIMESTAMP NOT NULL,
    timeUnit       UUID NOT NULL,
    timeUnitAmount INT  NOT NULL CHECK (timeUnitAmount >= 0),
    done BOOLEAN DEFAULT FALSE NOT NULL,
    FOREIGN KEY (timeUnit) REFERENCES TimeUnit(id)
);

CREATE TABLE IF NOT EXISTS TodoEvent_Category (
                                                  todoEvent_id UUID NOT NULL,
    category_id UUID NOT NULL,
                                                  PRIMARY KEY (todoEvent_id, category_id),
                                                  FOREIGN KEY (todoEvent_id) REFERENCES TodoEvent (id),
    FOREIGN KEY (category_id) REFERENCES Category(id)
);