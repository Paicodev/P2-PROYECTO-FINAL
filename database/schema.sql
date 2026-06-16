-- MySQL Workbench Forward Engineering (Optimizado para Java JDBC)

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `mydb` DEFAULT CHARACTER SET utf8 ;
USE `mydb` ;

-- -----------------------------------------------------
-- Table `mydb`.`Persona`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`Persona` (
  `idPersona` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(45) NOT NULL,
  `apellido` VARCHAR(45) NOT NULL,
  `dni` VARCHAR(20) NOT NULL,
  `email` VARCHAR(150) NULL,
  `telefono` VARCHAR(20) NULL,
  `tipo_persona` ENUM('MIEMBRO', 'INSTRUCTOR', 'ADMIN') NULL,
  `fecha_registro` DATE NOT NULL,
  `activo` TINYINT NULL DEFAULT 1,
  PRIMARY KEY (`idPersona`),
  UNIQUE INDEX `dni_UNIQUE` (`dni` ASC),
  UNIQUE INDEX `email_UNIQUE` (`email` ASC)
) ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `mydb`.`Planes`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`Planes` (
  `id_planes` INT NOT NULL AUTO_INCREMENT,
  `nombre_plan` VARCHAR(100) NOT NULL,
  `duracion_meses` INT NOT NULL,
  `descripcion` TEXT NULL,
  `precio_mensual` DECIMAL(10,2) NULL,
  PRIMARY KEY (`id_planes`)
) ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `mydb`.`Miembros`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`Miembros` (
  `idMiembros` INT NOT NULL AUTO_INCREMENT,
  `fecha_inscripcion` DATE NOT NULL,
  `fecha_vencimiento` DATE NOT NULL,
  `estado` ENUM('ACTIVO', 'INACTIVO', 'SUSPENDIDO') NULL,
  `Planes_id_planes` INT NOT NULL,
  `Persona_idPersona` INT NOT NULL,
  PRIMARY KEY (`idMiembros`),
  INDEX `fk_Miembros_Planes_idx` (`Planes_id_planes` ASC),
  INDEX `fk_Miembros_Persona1_idx` (`Persona_idPersona` ASC),
  CONSTRAINT `fk_Miembros_Planes`
    FOREIGN KEY (`Planes_id_planes`)
    REFERENCES `mydb`.`Planes` (`id_planes`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT `fk_Miembros_Persona1`
    FOREIGN KEY (`Persona_idPersona`)
    REFERENCES `mydb`.`Persona` (`idPersona`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `mydb`.`Instructores`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`Instructores` (
  `idInstructores` INT NOT NULL AUTO_INCREMENT,
  `especialidad` VARCHAR(95) NULL,
  `sueldo` DECIMAL(10,2) NULL,
  `Persona_idPersona` INT NOT NULL,
  PRIMARY KEY (`idInstructores`),
  INDEX `fk_Instructores_Persona1_idx` (`Persona_idPersona` ASC),
  CONSTRAINT `fk_Instructores_Persona1`
    FOREIGN KEY (`Persona_idPersona`)
    REFERENCES `mydb`.`Persona` (`idPersona`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `mydb`.`Clases` (MODIFICADA CON CLAVE FORÁNEA)
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`Clases` (
  `idClases` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(45) NOT NULL,
  `tipo` ENUM('GRUPAL', 'PERSONAL') NULL,
  `horario` DATETIME NOT NULL,
  `duracion_minutos` INT NULL,
  `capacidad_max` INT NULL,
  `activo` TINYINT NULL DEFAULT 1,
  `Instructores_idInstructores` INT NOT NULL, -- Nueva columna FK
  PRIMARY KEY (`idClases`),
  INDEX `fk_Clases_Instructores1_idx` (`Instructores_idInstructores` ASC),
  CONSTRAINT `fk_Clases_Instructores1`
    FOREIGN KEY (`Instructores_idInstructores`)
    REFERENCES `mydb`.`Instructores` (`idInstructores`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE
) ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `mydb`.`Inscripciones`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`Inscripciones` (
  `idInscripciones` INT NOT NULL AUTO_INCREMENT,
  `fecha_inscripcion` DATE NOT NULL,
  `asistio` TINYINT NULL DEFAULT 0,
  `Clases_idClases` INT NOT NULL,
  `Miembros_idMiembros` INT NOT NULL,
  PRIMARY KEY (`idInscripciones`),
  INDEX `fk_Inscripciones_Clases1_idx` (`Clases_idClases` ASC),
  INDEX `fk_Inscripciones_Miembros1_idx` (`Miembros_idMiembros` ASC),
  CONSTRAINT `fk_Inscripciones_Clases1`
    FOREIGN KEY (`Clases_idClases`)
    REFERENCES `mydb`.`Clases` (`idClases`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT `fk_Inscripciones_Miembros1`
    FOREIGN KEY (`Miembros_idMiembros`)
    REFERENCES `mydb`.`Miembros` (`idMiembros`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `mydb`.`Pagos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`Pagos` (
  `idPagos` INT NOT NULL AUTO_INCREMENT,
  `monto` DECIMAL(10,2) NOT NULL,
  `fecha_pago` DATE NOT NULL,
  `tipo` ENUM('MENSUALIDAD', 'CLASE', 'INSCRIPCION') NULL,
  `estado` ENUM('PAGADO', 'PENDIENTE', 'VENCIDO') NULL,
  `descripcion` VARCHAR(155) NULL,
  `Miembros_idMiembros` INT NOT NULL,
  PRIMARY KEY (`idPagos`),
  INDEX `fk_Pagos_Miembros1_idx` (`Miembros_idMiembros` ASC),
  CONSTRAINT `fk_Pagos_Miembros1`
    FOREIGN KEY (`Miembros_idMiembros`)
    REFERENCES `mydb`.`Miembros` (`idMiembros`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `mydb`.`UsuarioSistema`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`UsuarioSistema` (
  `idUsuarioSistema` INT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL,
  `password_hash` VARCHAR(255) NOT NULL,
  `rol` ENUM('ADMIN', 'RECEPCIONISTA') NULL,
  `ultimo_acceso` DATETIME NULL,
  `Persona_idPersona` INT NOT NULL,
  PRIMARY KEY (`idUsuarioSistema`),
  UNIQUE INDEX `username_UNIQUE` (`username` ASC),
  INDEX `fk_UsuarioSistema_Persona1_idx` (`Persona_idPersona` ASC),
  CONSTRAINT `fk_UsuarioSistema_Persona1`
    FOREIGN KEY (`Persona_idPersona`)
    REFERENCES `mydb`.`Persona` (`idPersona`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE = InnoDB;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

-- 1. PLANES
INSERT INTO Planes (nombre_plan, duracion_meses, descripcion, precio_mensual) VALUES 
('Plan Básico', 1, 'Acceso a sala de musculación', 15000.00),
('Plan Full', 1, 'Musculación + Clases Grupales', 20000.00),
('Plan Trimestral VIP', 3, 'Pase libre total por 3 meses', 50000.00);

-- 2. PERSONAS (1 Admin, 1 Recep, 2 Instructores, 5 Miembros)
INSERT INTO Persona (nombre, apellido, dni, email, telefono, tipo_persona, fecha_registro) VALUES 
('Carlos', 'Gerente', '11111111', 'admin@fitbase.com', '2657111111', 'ADMIN', '2024-01-01'),
('Ana', 'Recepcionista', '22222222', 'recepcion@fitbase.com', '2657222222', 'RECEPCIONISTA', '2024-01-02'),
('Marcos', 'Trainer', '33333333', 'marcos@fitbase.com', '2657333333', 'INSTRUCTOR', '2024-01-10'),
('Lucía', 'Fitness', '44444444', 'lucia@fitbase.com', '2657444444', 'INSTRUCTOR', '2024-01-15'),
('Juan', 'Pérez', '55555555', 'juan@gmail.com', '2657555555', 'MIEMBRO', '2024-05-01'),
('María', 'Gómez', '66666666', 'maria@gmail.com', '2657666666', 'MIEMBRO', '2024-05-15'),
('Pedro', 'Díaz', '77777777', 'pedro@gmail.com', '2657777777', 'MIEMBRO', '2024-06-01'),
('Sofía', 'Ruiz', '88888888', 'sofia@gmail.com', '2657888888', 'MIEMBRO', '2024-06-10'),
('Luis', 'Sosa', '99999999', 'luis@gmail.com', '2657999999', 'MIEMBRO', '2024-06-12');

-- 3. USUARIOS DEL SISTEMA (Contraseñas en texto plano '1234' para las pruebas)
INSERT INTO UsuarioSistema (username, password_hash, rol, Persona_idPersona) VALUES 
('admin', '1234', 'ADMIN', 1),
('recepcion', '1234', 'RECEPCIONISTA', 2);

-- 4. INSTRUCTORES (Asociados a las personas 3 y 4)
INSERT INTO Instructores (especialidad, sueldo, Persona_idPersona) VALUES 
('Musculación y CrossFit', 350000.00, 3),
('Yoga y Pilates', 300000.00, 4);

-- 5. MIEMBROS (Asociados a las personas 5 a 9)
INSERT INTO Miembros (fecha_inscripcion, fecha_vencimiento, estado, Planes_id_planes, Persona_idPersona) VALUES 
('2024-05-01', '2024-06-01', 'INACTIVO', 1, 5),
('2024-05-15', '2024-08-15', 'ACTIVO', 3, 6),
('2024-06-01', '2024-07-01', 'ACTIVO', 2, 7),
('2024-06-10', '2024-07-10', 'ACTIVO', 2, 8),
('2024-06-12', '2024-07-12', 'ACTIVO', 1, 9);

-- 6. CLASES (Asociadas a los instructores 1 y 2)
INSERT INTO Clases (nombre, tipo, horario, duracion_minutos, capacidad_max, Instructores_idInstructores) VALUES 
('CrossFit Extremo', 'GRUPAL', '2024-06-20 18:00:00', 60, 15, 1),
('Yoga Relax', 'GRUPAL', '2024-06-21 09:00:00', 45, 20, 2),
('Entrenamiento Personalizado', 'PERSONAL', '2024-06-22 10:00:00', 60, NULL, 1);

-- 7. INSCRIPCIONES (Miembros activos anotados a clases)
INSERT INTO Inscripciones (fecha_inscripcion, asistio, Clases_idClases, Miembros_idMiembros) VALUES 
('2024-06-13', 0, 1, 2), -- María a CrossFit
('2024-06-14', 1, 2, 3), -- Pedro a Yoga (Ya asistió)
('2024-06-14', 0, 1, 4); -- Sofía a CrossFit

-- 8. PAGOS (Historial de ingresos)
INSERT INTO Pagos (monto, fecha_pago, tipo, estado, descripcion, Miembros_idMiembros) VALUES 
(15000.00, '2024-05-01', 'MENSUALIDAD', 'PAGADO', 'Pago mes Mayo - Efectivo', 1),
(50000.00, '2024-05-15', 'MENSUALIDAD', 'PAGADO', 'Pago Trimestre - Tarjeta', 2),
(20000.00, '2024-06-01', 'MENSUALIDAD', 'PAGADO', 'Pago mes Junio - Efectivo', 3),
(20000.00, '2024-06-10', 'MENSUALIDAD', 'PAGADO', 'Pago mes Junio - Transferencia', 4),
(15000.00, '2024-06-12', 'MENSUALIDAD', 'PAGADO', 'Pago mes Junio - Efectivo', 5);