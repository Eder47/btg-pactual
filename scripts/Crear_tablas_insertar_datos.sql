---creamos las tablas
CREATE TABLE Cliente (
    id NUMBER PRIMARY KEY,
    nombre VARCHAR2(100) NOT NULL,
    apellidos VARCHAR2(100) NOT NULL,
    ciudad VARCHAR2(100)
);

CREATE TABLE Sucursal (
    id NUMBER PRIMARY KEY,
    nombre VARCHAR2(100) NOT NULL,
    ciudad VARCHAR2(100)
);

CREATE TABLE Producto (
    id NUMBER PRIMARY KEY,
    nombre VARCHAR2(100) NOT NULL,
    tipoProducto VARCHAR2(50)
);

CREATE TABLE Inscripcion (
    idProducto NUMBER,
    idCliente NUMBER,
    PRIMARY KEY (idProducto, idCliente),
    FOREIGN KEY (idProducto) REFERENCES Producto(id),
    FOREIGN KEY (idCliente) REFERENCES Cliente(id)
);

CREATE TABLE Disponibilidad (
    idSucursal NUMBER,
    idProducto NUMBER,
    PRIMARY KEY (idSucursal, idProducto),
    FOREIGN KEY (idSucursal) REFERENCES Sucursal(id),
    FOREIGN KEY (idProducto) REFERENCES Producto(id)
);

CREATE TABLE Visitan (
    idSucursal NUMBER,
    idCliente NUMBER,
    fechaVisita DATE,
    PRIMARY KEY (idSucursal, idCliente, fechaVisita),
    FOREIGN KEY (idSucursal) REFERENCES Sucursal(id),
    FOREIGN KEY (idCliente) REFERENCES Cliente(id)
);

INSERT INTO Cliente VALUES (1, 'Juan', 'Pérez', 'Bogotá');
INSERT INTO Cliente VALUES (2, 'María', 'Gómez', 'Medellín');
INSERT INTO Cliente VALUES (3, 'Carlos', 'Rodríguez', 'Cali');

INSERT INTO Sucursal VALUES (1, 'Sucursal Norte', 'Bogotá');
INSERT INTO Sucursal VALUES (2, 'Sucursal Sur', 'Bogotá');
INSERT INTO Sucursal VALUES (3, 'Sucursal Centro', 'Medellín');

INSERT INTO Producto VALUES (1, 'Cuenta Ahorros', 'Bancario');
INSERT INTO Producto VALUES (2, 'Tarjeta Crédito', 'Tarjeta');
INSERT INTO Producto VALUES (3, 'CDT', 'Inversión');


INSERT INTO Inscripcion VALUES (1, 1);


INSERT INTO Disponibilidad VALUES (1, 1);
INSERT INTO Disponibilidad VALUES (2, 1);


INSERT INTO Visitan VALUES (1, 1, SYSDATE);
INSERT INTO Visitan VALUES (2, 1, SYSDATE);