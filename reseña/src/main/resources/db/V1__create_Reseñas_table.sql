CREATE TABLE reseñas (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(255) NOT NULL,
    comentario VARCHAR(255) NOT NULL,
    valoracion DOUBLE NOT NULL,
    PRIMARY KEY (id)
);