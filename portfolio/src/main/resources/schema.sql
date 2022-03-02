CREATE TABLE IF NOT EXISTS PROJECT (
    project_id INT AUTO_INCREMENT PRIMARY KEY,
    project_name VARCHAR(50) NOT NULL,
    description VARCHAR (250) NOT NULL,
    start_date DATE,
    end_date DATE
);