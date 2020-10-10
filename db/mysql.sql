
CREATE DATABASE IF NOT EXISTS test;
USE test;

CREATE TABLE employee (
    id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(16) NOT NULL,
    salary FLOAT(8, 2) NOT NULL,
    UNIQUE KEY uk_name (name),
    PRIMARY KEY (id)
) ENGINE=InnoDB;

INSERT INTO employee (name, salary) VALUES ('Mike', 8000.0);
