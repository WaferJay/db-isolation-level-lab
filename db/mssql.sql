
CREATE DATABASE test;
go

USE test;

CREATE TABLE employee (
    id INT NOT NULL IDENTITY(1, 1),
    name VARCHAR(16) NOT NULL,
    salary DECIMAL(8, 2) NOT NULL,
    CONSTRAINT uk_name UNIQUE (name)
);

INSERT INTO employee (name, salary) VALUES ('Mike', 8000.0);
