CREATE TABLE todo (
    `id` int NOT NULL AUTO_INCREMENT,
    `title` varchar(256) NOT NULL,
    `done` tinyint NOT NULL DEFAULT 0,

    PRIMARY KEY (id)
) DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
