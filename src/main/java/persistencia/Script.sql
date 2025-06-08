-- Crear base de datos
CREATE DATABASE AgenciaViajesDB;
GO
USE AgenciaViajesDB;
GO

-- Tabla: Usuarios (con campo Rol directamente)
CREATE TABLE Usuarios (
    id INT PRIMARY KEY IDENTITY(1,1),
    name VARCHAR(100) NOT NULL,
    passwordHash VARCHAR(64) NOT NULL,
    email VARCHAR(200) NOT NULL UNIQUE,
    status TINYINT NOT NULL, -- 1 = Activo, 0 = Inactivo
    rol NVARCHAR(20)   (rol IN ('Administrador', 'Agente', 'Cliente')) NOT NULL
);
GO

-- Tabla: Clientes
CREATE TABLE Clientes (
    clienteId INT PRIMARY KEY IDENTITY(1,1),
    userId INT UNIQUE,
    telefono VARCHAR(20),
    direccion VARCHAR(200),
    FOREIGN KEY (userId) REFERENCES Usuarios(id)
);
GO

-- Tabla: Destinos (con campo imagen)
CREATE TABLE Destinos (
    destinoId INT PRIMARY KEY IDENTITY(1,1),
    nombre VARCHAR(100),
    pais VARCHAR(100),
    descripcion TEXT,
    imagen VARBINARY(MAX)
);
GO

-- Tabla: Paquetes
CREATE TABLE Paquetes (
    paqueteId INT PRIMARY KEY IDENTITY(1,1),
    nombre VARCHAR(150),
    descripcion TEXT,
    precio DECIMAL(10,2),
    duracionDias INT,
    fechaInicio DATE,
    fechaFin DATE,
    destinoId INT,
    FOREIGN KEY (destinoId) REFERENCES Destinos(destinoId)
);
GO

-- Tabla: Reservas
CREATE TABLE Reservas (
    reservaId INT PRIMARY KEY IDENTITY(1,1),
    clienteId INT,
    paqueteId INT,
    fechaReserva DATE DEFAULT GETDATE(),
    estado VARCHAR(20) CHECK (estado IN ('Pendiente', 'Confirmada', 'Cancelada')) DEFAULT 'Pendiente',
    FOREIGN KEY (clienteId) REFERENCES Clientes(clienteId),
    FOREIGN KEY (paqueteId) REFERENCES Paquetes(paqueteId)
);
GO

-- Tabla: MetodoPago
CREATE TABLE MetodoPago (
    metodoPagoId INT PRIMARY KEY IDENTITY(1,1),
    nombreMetodo VARCHAR(50) UNIQUE NOT NULL
);
GO

-- Tabla: Pagos
CREATE TABLE Pagos (
    pagoId INT PRIMARY KEY IDENTITY(1,1),
    reservaId INT,
    monto DECIMAL(10,2),
    metodoPagoId INT,
    fechaPago DATE DEFAULT GETDATE(),
    FOREIGN KEY (reservaId) REFERENCES Reservas(reservaId),
    FOREIGN KEY (metodoPagoId) REFERENCES MetodoPago(metodoPagoId)
);
GO

-- Insertar métodos de pago base
INSERT INTO MetodoPago (nombreMetodo) VALUES
('Efectivo'),
('Tarjeta'),
('Transferencia');
GO

