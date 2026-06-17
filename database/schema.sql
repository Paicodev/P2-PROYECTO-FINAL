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
  `estado` ENUM('ACTIVO', 'INACTIVO', 'VENCIDO') NULL,
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

-- =====================================================
-- INSERCIÓN DE DATOS DE PRUEBA
-- =====================================================

-- 1. PLANES
INSERT INTO Planes (nombre_plan, duracion_meses, descripcion, precio_mensual) VALUES 
('Plan Básico', 1, 'Acceso a sala de musculación', 25000.00),
('Plan Full', 1, 'Musculación + Clases Grupales', 35000.00),
('Plan Trimestral VIP', 3, 'Pase libre total por 3 meses', 90000.00);

-- 2. PERSONAS (1 Admin, 1 Recep, 4 Instructores, 55 Miembros con DNI aleatorios)
INSERT INTO Persona (nombre, apellido, dni, email, telefono, tipo_persona, fecha_registro) VALUES 
-- Staff (idPersona: 1 y 2)
('Carlos', 'Gerente', '29481023', 'admin@fitbase.com', '2657411111', 'ADMIN', '2026-01-01'),
('Ana', 'Recepcionista', '34910294', 'recepcion@fitbase.com', '2657222222', 'RECEPCIONISTA', '2026-01-02'),
-- Instructores (idPersona: 3 al 6)
('Marcos', 'Trainer', '31524819', 'marcos@fitbase.com', '2657333333', 'INSTRUCTOR', '2026-01-10'),
('Lucía', 'Fitness', '38401928', 'lucia@fitbase.com', '2657444444', 'INSTRUCTOR', '2026-01-15'),
('Esteban', 'Barras', '35719203', 'esteban@fitbase.com', '2657555555', 'INSTRUCTOR', '2026-02-01'),
('Valeria', 'Fuerza', '39148201', 'valeria@fitbase.com', '2657666666', 'INSTRUCTOR', '2026-02-10'),
-- Miembros (idPersona: 7 al 61)
('Juan', 'Pérez', '41284930', 'juan1@gmail.com', '265790101', 'MIEMBRO', '2026-04-28'),
('María', 'Gómez', '43810293', 'maria2@gmail.com', '265790102', 'MIEMBRO', '2026-04-29'),
('Pedro', 'Díaz', '40572913', 'pedro3@gmail.com', '265790103', 'MIEMBRO', '2026-04-30'),
('Sofía', 'Ruiz', '42918304', 'sofia4@gmail.com', '265790104', 'MIEMBRO', '2026-05-01'),
('Luis', 'Sosa', '44192039', 'luis5@gmail.com', '265790105', 'MIEMBRO', '2026-05-01'),
('Ana', 'López', '41502938', 'ana6@gmail.com', '265790106', 'MIEMBRO', '2026-05-02'),
('Carlos', 'Martínez', '43019284', 'carlos7@gmail.com', '265790107', 'MIEMBRO', '2026-05-02'),
('Laura', 'García', '45281930', 'laura8@gmail.com', '265790108', 'MIEMBRO', '2026-05-02'),
('Diego', 'Rodríguez', '39810293', 'diego9@gmail.com', '265790109', 'MIEMBRO', '2026-05-03'),
('Elena', 'Fernández', '42193840', 'elena10@gmail.com', '265790110', 'MIEMBRO', '2026-05-03'),
('Javier', 'Ponce', '40419284', 'javier11@gmail.com', '265790111', 'MIEMBRO', '2026-05-03'),
('Paula', 'Molina', '44719284', 'paula12@gmail.com', '265790112', 'MIEMBRO', '2026-05-04'),
('Andrés', 'Castro', '41392019', 'andres13@gmail.com', '265790113', 'MIEMBRO', '2026-05-04'),
('Clara', 'Ortiz', '43619204', 'clara14@gmail.com', '265790114', 'MIEMBRO', '2026-05-04'),
('Martín', 'Silva', '45910293', 'martin15@gmail.com', '265790115', 'MIEMBRO', '2026-05-05'),
('Luciana', 'Ríos', '42194820', 'luciana16@gmail.com', '265790116', 'MIEMBRO', '2026-05-05'),
('Gabriel', 'Romero', '40810293', 'gabriel17@gmail.com', '265790117', 'MIEMBRO', '2026-05-05'),
('Micaela', 'Navarro', '44291039', 'micaela18@gmail.com', '265790118', 'MIEMBRO', '2026-05-06'),
('Tomás', 'Torres', '41948203', 'tomas19@gmail.com', '265790119', 'MIEMBRO', '2026-05-06'),
('Florencia', 'Acosta', '43291024', 'flor20@gmail.com', '265790120', 'MIEMBRO', '2026-05-06'),
('Emiliano', 'Suárez', '40192840', 'emi21@gmail.com', '265790121', 'MIEMBRO', '2026-05-07'),
('Valentina', 'Rojas', '45391029', 'valen22@gmail.com', '265790122', 'MIEMBRO', '2026-05-07'),
('Nicolás', 'Giménez', '42810293', 'nico23@gmail.com', '265790123', 'MIEMBRO', '2026-05-07'),
('Camila', 'Domínguez', '41492039', 'cami24@gmail.com', '265790124', 'MIEMBRO', '2026-05-08'),
('Rodrigo', 'Blanco', '43519203', 'rodri25@gmail.com', '265790125', 'MIEMBRO', '2026-05-08'),
('Julieta', 'Luna', '40619284', 'juli26@gmail.com', '265790126', 'MIEMBRO', '2026-05-08'),
('Agustín', 'Paz', '44294810', 'agus27@gmail.com', '265790127', 'MIEMBRO', '2026-05-09'),
('Milagros', 'Cruz', '42319284', 'mili28@gmail.com', '265790128', 'MIEMBRO', '2026-05-09'),
('Facundo', 'Vega', '41294019', 'facu29@gmail.com', '265790129', 'MIEMBRO', '2026-05-09'),
('Bárbara', 'Ibarra', '45910239', 'barbi30@gmail.com', '265790130', 'MIEMBRO', '2026-05-10'),
('Lucas', 'Herrera', '43294811', 'lucas31@gmail.com', '265790131', 'MIEMBRO', '2026-05-15'),
('Daniela', 'Medina', '40294822', 'dani32@gmail.com', '265790132', 'MIEMBRO', '2026-05-15'),
('Matías', 'Arias', '44710294', 'mati33@gmail.com', '265790133', 'MIEMBRO', '2026-05-16'),
('Agostina', 'Vidal', '41204928', 'agos34@gmail.com', '265790134', 'MIEMBRO', '2026-05-16'),
('Gonzalo', 'Mendoza', '42319204', 'gonza35@gmail.com', '265790135', 'MIEMBRO', '2026-05-18'),
('Antonella', 'Ramos', '45810293', 'anto36@gmail.com', '265790136', 'MIEMBRO', '2026-05-20'),
('Federico', 'Sarmiento', '40194829', 'fede37@gmail.com', '265790137', 'MIEMBRO', '2026-05-22'),
('Rocío', 'Iglesias', '43610294', 'rocio38@gmail.com', '265790138', 'MIEMBRO', '2026-05-25'),
('Joaquín', 'Pinto', '41294810', 'joaco39@gmail.com', '265790139', 'MIEMBRO', '2026-05-28'),
('Cinthia', 'Bravo', '44810294', 'cinthia40@gmail.com', '265790140', 'MIEMBRO', '2026-05-30'),
('Santiago', 'Benítez', '42104928', 'santi41@gmail.com', '265790141', 'MIEMBRO', '2026-06-01'),
('Mateo', 'Guzmán', '43910294', 'mateo42@gmail.com', '265790142', 'MIEMBRO', '2026-06-01'),
('Ludmila', 'Cáceres', '45194820', 'ludmi43@gmail.com', '265790143', 'MIEMBRO', '2026-06-02'),
('Patricio', 'Cardozo', '40819482', 'patro44@gmail.com', '265790144', 'MIEMBRO', '2026-06-02'),
('Malena', 'Soria', '41940192', 'male45@gmail.com', '265790145', 'MIEMBRO', '2026-06-03'),
('Iair', 'Franco', '44049281', 'iair46@gmail.com', '265790146', 'MIEMBRO', '2026-06-03'),
('Bruno', 'Maldonado', '42849102', 'bruno47@gmail.com', '265790147', 'MIEMBRO', '2026-06-04'),
('Paulina', 'Vargas', '43149204', 'pau48@gmail.com', '265790148', 'MIEMBRO', '2026-06-04'),
('Leonel', 'Mendez', '46049182', 'leo49@gmail.com', '265790149', 'MIEMBRO', '2026-06-05'),
('Abigail', 'Flores', '41940294', 'abi50@gmail.com', '265790150', 'MIEMBRO', '2026-06-05'),
('Tobías', 'Aquino', '40249182', 'tobias51@gmail.com', '265790151', 'MIEMBRO', '2026-06-06'),
('Justina', 'Páez', '44149204', 'justi52@gmail.com', '265790152', 'MIEMBRO', '2026-06-07'),
('Ramiro', 'Leiva', '42940192', 'rami53@gmail.com', '265790153', 'MIEMBRO', '2026-06-08'),
('Abril', 'Sancho', '43049182', 'abril54@gmail.com', '265790154', 'MIEMBRO', '2026-06-09'),
('Ezequiel', 'Bustos', '45940294', 'eze55@gmail.com', '265790155', 'MIEMBRO', '2026-06-10');

-- 3. USUARIOS DEL SISTEMA
INSERT INTO UsuarioSistema (username, password_hash, rol, Persona_idPersona) VALUES 
('admin', '1234', 'ADMIN', 1),
('recepcion', '1234', 'RECEPCIONISTA', 2);

-- 4. INSTRUCTORES (Sueldos mensuales: Suma total de $1.300.000)
INSERT INTO Instructores (especialidad, sueldo, Persona_idPersona) VALUES 
('Musculación y CrossFit', 350000.00, 3),
('Yoga y Pilates', 300000.00, 4),
('Calistenia Avanzada', 320000.00, 5),
('Entrenamiento Funcional', 330000.00, 6);

-- 5. MIEMBROS (Corregido: Se insertan exactamente los 55 registros vinculando las personas de la 7 a la 61)
INSERT INTO Miembros (fecha_inscripcion, fecha_vencimiento, estado, Planes_id_planes, Persona_idPersona) VALUES 
-- Trimestrales VIP en Mayo (idMiembros: 1 al 10 | Persona_idPersona: 7 al 16)
('2026-05-01', '2026-08-01', 'ACTIVO', 3, 7), ('2026-05-01', '2026-08-01', 'ACTIVO', 3, 8),
('2026-05-02', '2026-08-02', 'ACTIVO', 3, 9), ('2026-05-02', '2026-08-02', 'ACTIVO', 3, 10),
('2026-05-03', '2026-08-03', 'ACTIVO', 3, 11), ('2026-05-03', '2026-08-03', 'ACTIVO', 3, 12),
('2026-05-04', '2026-08-04', 'ACTIVO', 3, 13), ('2026-05-05', '2026-08-05', 'ACTIVO', 3, 14),
('2026-05-05', '2026-08-05', 'ACTIVO', 3, 15), ('2026-05-06', '2026-08-06', 'ACTIVO', 3, 16),
-- Planes Full en Mayo (idMiembros: 11 al 34 | Persona_idPersona: 17 al 40)
('2026-05-06', '2026-06-06', 'ACTIVO', 2, 17), ('2026-05-07', '2026-06-07', 'ACTIVO', 2, 18),
('2026-05-07', '2026-06-07', 'ACTIVO', 2, 19), ('2026-05-08', '2026-06-08', 'ACTIVO', 2, 20),
('2026-05-08', '2026-06-08', 'ACTIVO', 2, 21), ('2026-05-09', '2026-06-09', 'ACTIVO', 2, 22),
('2026-05-09', '2026-06-09', 'ACTIVO', 2, 23), ('2026-05-10', '2026-06-10', 'ACTIVO', 2, 24),
('2026-05-10', '2026-06-10', 'ACTIVO', 2, 25), ('2026-05-11', '2026-06-11', 'ACTIVO', 2, 26),
('2026-05-12', '2026-06-12', 'ACTIVO', 2, 27), ('2026-05-12', '2026-06-12', 'ACTIVO', 2, 28),
('2026-05-13', '2026-06-13', 'ACTIVO', 2, 29), ('2026-05-14', '2026-06-14', 'ACTIVO', 2, 30),
('2026-05-15', '2026-06-15', 'ACTIVO', 2, 31), ('2026-05-15', '2026-06-15', 'ACTIVO', 2, 32),
('2026-05-16', '2026-06-16', 'ACTIVO', 2, 33), ('2026-05-16', '2026-06-16', 'ACTIVO', 2, 34),
('2026-05-18', '2026-06-18', 'ACTIVO', 2, 35), ('2026-05-20', '2026-06-20', 'ACTIVO', 2, 36),
('2026-05-22', '2026-06-22', 'ACTIVO', 2, 37), ('2026-05-25', '2026-06-25', 'ACTIVO', 2, 38),
('2026-05-28', '2026-06-28', 'ACTIVO', 2, 39), ('2026-05-30', '2026-06-30', 'ACTIVO', 2, 40),
-- Nuevos e inscripciones de Junio (idMiembros: 35 al 55 | Persona_idPersona: 41 al 61)
('2026-06-01', '2026-07-01', 'ACTIVO', 1, 41), ('2026-06-01', '2026-07-01', 'ACTIVO', 1, 42),
('2026-06-02', '2026-07-02', 'ACTIVO', 2, 43), ('2026-06-02', '2026-07-02', 'ACTIVO', 2, 44),
('2026-06-03', '2026-07-03', 'ACTIVO', 1, 45), ('2026-06-03', '2026-07-03', 'ACTIVO', 1, 46),
('2026-06-04', '2026-07-04', 'ACTIVO', 2, 47), ('2026-06-04', '2026-07-04', 'ACTIVO', 2, 48),
('2026-06-05', '2026-07-05', 'ACTIVO', 1, 49), ('2026-06-05', '2026-07-05', 'ACTIVO', 1, 50),
('2026-06-06', '2026-07-06', 'ACTIVO', 2, 51), ('2026-06-07', '2026-07-07', 'ACTIVO', 1, 52),
('2026-06-08', '2026-07-08', 'ACTIVO', 2, 53), ('2026-06-09', '2026-07-09', 'ACTIVO', 1, 54),
('2026-06-10', '2026-07-10', 'ACTIVO', 2, 55), ('2026-06-11', '2026-07-11', 'ACTIVO', 1, 56),
('2026-06-11', '2026-07-11', 'ACTIVO', 2, 57), ('2026-06-12', '2026-07-12', 'ACTIVO', 1, 58),
('2026-06-12', '2026-07-12', 'ACTIVO', 2, 59), ('2026-06-13', '2026-07-13', 'ACTIVO', 1, 60),
('2026-06-13', '2026-07-13', 'ACTIVO', 2, 61);

-- 6. CLASES (Asociadas correctamente a idInstructores del 1 al 4)
INSERT INTO Clases (nombre, tipo, horario, duracion_minutos, capacidad_max, Instructores_idInstructores) VALUES 
('CrossFit Extremo', 'GRUPAL', '2026-05-15 18:00:00', 60, 15, 1),
('Yoga Relax', 'GRUPAL', '2026-05-16 09:00:00', 45, 20, 2),
('Calistenia Progresiva', 'GRUPAL', '2026-05-17 16:30:00', 60, 12, 3),
('Total HIIT Funcional', 'GRUPAL', '2026-05-18 19:00:00', 50, 25, 4);

-- 7. INSCRIPCIONES (Ahora todos los idMiembros del 1 al 55 existen perfectamente)
INSERT INTO Inscripciones (fecha_inscripcion, asistio, Clases_idClases, Miembros_idMiembros) VALUES 
('2026-05-14', 1, 1, 11), ('2026-05-14', 1, 1, 12), ('2026-05-15', 1, 2, 13), 
('2026-05-15', 0, 2, 14), ('2026-05-16', 1, 3, 15), ('2026-05-16', 1, 3, 16),
('2026-05-17', 1, 4, 1),  ('2026-05-17', 1, 4, 2),  ('2026-05-18', 0, 1, 25),
('2026-06-02', 1, 1, 43), ('2026-06-03', 1, 3, 44), ('2026-06-04', 1, 4, 51);

-- 8. PAGOS (Historial masivo con balance positivo garantizado)
INSERT INTO Pagos (monto, fecha_pago, tipo, estado, descripcion, Miembros_idMiembros) VALUES 
-- MAYO: 10 Trimestrales VIP ($90.000 c/u) = $900.000
(90000.00, '2026-05-01', 'MENSUALIDAD', 'PAGADO', 'Trimestre Completo - Transferencia', 1),
(90000.00, '2026-05-01', 'MENSUALIDAD', 'PAGADO', 'Trimestre Completo - Tarjeta', 2),
(90000.00, '2026-05-02', 'MENSUALIDAD', 'PAGADO', 'Trimestre Completo - Efectivo', 3),
(90000.00, '2026-05-02', 'MENSUALIDAD', 'PAGADO', 'Trimestre Completo - Transferencia', 4),
(90000.00, '2026-05-03', 'MENSUALIDAD', 'PAGADO', 'Trimestre Completo', 5),
(90000.00, '2026-05-03', 'MENSUALIDAD', 'PAGADO', 'Trimestre Completo', 6),
(90000.00, '2026-05-04', 'MENSUALIDAD', 'PAGADO', 'Trimestre Completo', 7),
(90000.00, '2026-05-05', 'MENSUALIDAD', 'PAGADO', 'Trimestre Completo', 8),
(90000.00, '2026-05-05', 'MENSUALIDAD', 'PAGADO', 'Trimestre Completo', 9),
(90000.00, '2026-05-06', 'MENSUALIDAD', 'PAGADO', 'Trimestre Completo', 10),

-- MAYO: 24 Planes Full ($35.000 c/u) = $840.000
(35000.00, '2026-05-06', 'MENSUALIDAD', 'PAGADO', 'Plan Full Mayo', 11),
(35000.00, '2026-05-07', 'MENSUALIDAD', 'PAGADO', 'Plan Full Mayo', 12),
(35000.00, '2026-05-07', 'MENSUALIDAD', 'PAGADO', 'Plan Full Mayo', 13),
(35000.00, '2026-05-08', 'MENSUALIDAD', 'PAGADO', 'Plan Full Mayo', 14),
(35000.00, '2026-05-08', 'MENSUALIDAD', 'PAGADO', 'Plan Full Mayo', 15),
(35000.00, '2026-05-09', 'MENSUALIDAD', 'PAGADO', 'Plan Full Mayo', 16),
(35000.00, '2026-05-09', 'MENSUALIDAD', 'PAGADO', 'Plan Full Mayo', 17),
(35000.00, '2026-05-10', 'MENSUALIDAD', 'PAGADO', 'Plan Full Mayo', 18),
(35000.00, '2026-05-10', 'MENSUALIDAD', 'PAGADO', 'Plan Full Mayo', 19),
(35000.00, '2026-05-11', 'MENSUALIDAD', 'PAGADO', 'Plan Full Mayo', 20),
(35000.00, '2026-05-12', 'MENSUALIDAD', 'PAGADO', 'Plan Full Mayo', 21),
(35000.00, '2026-05-12', 'MENSUALIDAD', 'PAGADO', 'Plan Full Mayo', 22),
(35000.00, '2026-05-13', 'MENSUALIDAD', 'PAGADO', 'Plan Full Mayo', 23),
(35000.00, '2026-05-14', 'MENSUALIDAD', 'PAGADO', 'Plan Full Mayo', 24),
(35000.00, '2026-05-15', 'MENSUALIDAD', 'PAGADO', 'Plan Full Mayo', 25),
(35000.00, '2026-05-15', 'MENSUALIDAD', 'PAGADO', 'Plan Full Mayo', 26),
(35000.00, '2026-05-16', 'MENSUALIDAD', 'PAGADO', 'Plan Full Mayo', 27),
(35000.00, '2026-05-16', 'MENSUALIDAD', 'PAGADO', 'Plan Full Mayo', 28),
(35000.00, '2026-05-18', 'MENSUALIDAD', 'PAGADO', 'Plan Full Mayo', 29),
(35000.00, '2026-05-20', 'MENSUALIDAD', 'PAGADO', 'Plan Full Mayo', 30),
(35000.00, '2026-05-22', 'MENSUALIDAD', 'PAGADO', 'Plan Full Mayo', 31),
(35000.00, '2026-05-25', 'MENSUALIDAD', 'PAGADO', 'Plan Full Mayo', 32),
(35000.00, '2026-05-28', 'MENSUALIDAD', 'PAGADO', 'Plan Full Mayo', 33),
(35000.00, '2026-05-30', 'MENSUALIDAD', 'PAGADO', 'Plan Full Mayo', 34),

-- JUNIO: Renovación de los 24 Full de Mayo = $840.000
(35000.00, '2026-06-06', 'MENSUALIDAD', 'PAGADO', 'Renovación Full Junio', 11),
(35000.00, '2026-06-07', 'MENSUALIDAD', 'PAGADO', 'Renovación Full Junio', 12),
(35000.00, '2026-06-07', 'MENSUALIDAD', 'PAGADO', 'Renovación Full Junio', 13),
(35000.00, '2026-06-08', 'MENSUALIDAD', 'PAGADO', 'Renovación Full Junio', 14),
(35000.00, '2026-06-08', 'MENSUALIDAD', 'PAGADO', 'Renovación Full Junio', 15),
(35000.00, '2026-06-09', 'MENSUALIDAD', 'PAGADO', 'Renovación Full Junio', 16),
(35000.00, '2026-06-09', 'MENSUALIDAD', 'PAGADO', 'Renovación Full Junio', 17),
(35000.00, '2026-06-10', 'MENSUALIDAD', 'PAGADO', 'Renovación Full Junio', 18),
(35000.00, '2026-06-10', 'MENSUALIDAD', 'PAGADO', 'Renovación Full Junio', 19),
(35000.00, '2026-06-11', 'MENSUALIDAD', 'PAGADO', 'Renovación Full Junio', 20),
(35000.00, '2026-06-12', 'MENSUALIDAD', 'PAGADO', 'Renovación Full Junio', 21),
(35000.00, '2026-06-12', 'MENSUALIDAD', 'PAGADO', 'Renovación Full Junio', 22),
(35000.00, '2026-06-13', 'MENSUALIDAD', 'PAGADO', 'Renovación Full Junio', 23),
(35000.00, '2026-06-14', 'MENSUALIDAD', 'PAGADO', 'Renovación Full Junio', 24),
(35000.00, '2026-06-15', 'MENSUALIDAD', 'PAGADO', 'Renovación Full Junio', 25),
(35000.00, '2026-06-15', 'MENSUALIDAD', 'PAGADO', 'Renovación Full Junio', 26),
(35000.00, '2026-06-16', 'MENSUALIDAD', 'PAGADO', 'Renovación Full Junio', 27),
(35000.00, '2026-06-16', 'MENSUALIDAD', 'PAGADO', 'Renovación Full Junio', 28),
(35000.00, '2026-06-18', 'MENSUALIDAD', 'PAGADO', 'Renovación Full Junio', 29),
(35000.00, '2026-06-20', 'MENSUALIDAD', 'PAGADO', 'Renovación Full Junio', 30),
(35000.00, '2026-06-22', 'MENSUALIDAD', 'PAGADO', 'Renovación Full Junio', 31),
(35000.00, '2026-06-25', 'MENSUALIDAD', 'PAGADO', 'Renovación Full Junio', 32),
(35000.00, '2026-06-28', 'MENSUALIDAD', 'PAGADO', 'Renovación Full Junio', 33),
(35000.00, '2026-06-30', 'MENSUALIDAD', 'PAGADO', 'Renovación Full Junio', 34),

-- JUNIO: 7 Nuevos Miembros con Plan Básico ($25.000 c/u) = $175.000
(25000.00, '2026-06-01', 'MENSUALIDAD', 'PAGADO', 'Alta Plan Básico', 41),
(25000.00, '2026-06-01', 'MENSUALIDAD', 'PAGADO', 'Alta Plan Básico', 42),
(25000.00, '2026-06-03', 'MENSUALIDAD', 'PAGADO', 'Alta Plan Básico', 45),
(25000.00, '2026-06-03', 'MENSUALIDAD', 'PAGADO', 'Alta Plan Básico', 46),
(25000.00, '2026-06-05', 'MENSUALIDAD', 'PAGADO', 'Alta Plan Básico', 49),
(25000.00, '2026-06-05', 'MENSUALIDAD', 'PAGADO', 'Alta Plan Básico', 50),
(25000.00, '2026-06-07', 'MENSUALIDAD', 'PAGADO', 'Alta Plan Básico', 52),

-- JUNIO: 8 Nuevos Miembros con Plan Full ($35.000 c/u) = $280.000
(35000.00, '2026-06-02', 'MENSUALIDAD', 'PAGADO', 'Alta Plan Full', 43),
(35000.00, '2026-06-02', 'MENSUALIDAD', 'PAGADO', 'Alta Plan Full', 44),
(35000.00, '2026-06-04', 'MENSUALIDAD', 'PAGADO', 'Alta Plan Full', 47),
(35000.00, '2026-06-04', 'MENSUALIDAD', 'PAGADO', 'Alta Plan Full', 48),
(35000.00, '2026-06-06', 'MENSUALIDAD', 'PAGADO', 'Alta Plan Full', 51),
(35000.00, '2026-06-08', 'MENSUALIDAD', 'PAGADO', 'Alta Plan Full', 53),
(35000.00, '2026-06-09', 'MENSUALIDAD', 'PAGADO', 'Alta Plan Full', 54),
(35000.00, '2026-06-10', 'MENSUALIDAD', 'PAGADO', 'Alta Plan Full', 55);
DROP DATABASE mydb;