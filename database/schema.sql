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
-- Table `mydb`.`Clases`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`Clases` (
  `idClases` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(45) NOT NULL,
  `tipo` ENUM('GRUPAL', 'PERSONAL') NULL,
  `horario` DATETIME NOT NULL,
  `duracion_minutos` INT NULL,
  `capacidad_max` INT NULL,
  `activo` TINYINT NULL DEFAULT 1,
  PRIMARY KEY (`idClases`)
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
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;--