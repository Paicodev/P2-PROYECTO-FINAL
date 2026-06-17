# P2-PROYECTO-FINAL

# Informe de Proyecto Final: FitBase
**Universidad Nacional de Villa Mercedes** **Materia:** Programación II  
**Carrera:** Programador Universitario en Sistemas  

---

## Información del Proyecto
* **Materia:** Programación II
* **Profesores:** Isray Nahir, Lucero Walter
* **Equipo de Desarrollo:** * Agüero Gerónimo
  * Aguilar Jennifer
  * Aguilar Pablo
  * Brambilla Zoe
  * Bulacio Maia
  * Garavaglia Angelina

---

## 1. Introducción y Objetivo del Sistema
El presente documento detalla el desarrollo de **FitBase**, un Sistema de Punto de Venta Interno y gestión administrativa diseñado específicamente para el control de un gimnasio. El software está orientado a ser utilizado de manera exclusiva por el personal del establecimiento, limitando el acceso y las funciones según los privilegios del usuario (**Dueño/Administrador** o **Recepcionista/Operario**).

---

## 2. Alcance y Requerimientos
El sistema fue construido cumpliendo con estrictos requerimientos funcionales y no funcionales que garantizan su correcto desempeño operativo y técnico.

### Requerimientos Funcionales (RF)
| ID | Requerimiento | Descripción |
| :--- | :--- | :--- |
| **RF01** | Autenticación de Usuarios | El sistema deberá permitir el inicio y cierre de sesión mediante credenciales válidas. |
| **RF02** | Gestión de Miembros | El sistema deberá permitir registrar, consultar, modificar y dar de baja miembros del gimnasio. |
| **RF03** | Gestión de Usuarios del Sistema | El sistema deberá permitir registrar, consultar, modificar y desactivar usuarios del sistema. |
| **RF04** | Gestión de Clases | El sistema deberá permitir registrar, consultar, modificar y eliminar clases del gimnasio. |
| **RF05** | Gestión de Instructores | El sistema deberá permitir registrar, consultar, modificar y eliminar instructores. |
| **RF06** | Inscripción a Clases | El sistema deberá permitir inscribir miembros en las clases disponibles. |
| **RF07** | Gestión de Pagos | El sistema deberá registrar y consultar los pagos realizados por los miembros. |
| **RF08** | Generación de Reportes | El sistema deberá generar reportes de ingresos, pagos y asistencia de miembros. |
| **RF09** | Exportación de Reportes | El sistema deberá permitir exportar reportes en formato PDF. |

### Requerimientos No Funcionales (RNF)
| ID | Requerimiento | Descripción |
| :--- | :--- | :--- |
| **RNF01** | Tecnología de Interfaz | La interfaz gráfica deberá desarrollarse utilizando Java Swing. |
| **RNF02** | Usabilidad | La interfaz deberá ser intuitiva, consistente y permitir realizar las tareas habituales con una cantidad mínima de acciones. |
| **RNF03** | Persistencia de Datos | La información deberá almacenarse en una base de datos MySQL. |
| **RNF04** | Arquitectura DAO | El acceso a los datos deberá implementarse mediante el patrón DAO para desacoplar la lógica de negocio de la persistencia. |
| **RNF05** | Conectividad | La comunicación con la base de datos deberá realizarse mediante JDBC. |
| **RNF06** | Seguridad de Acceso a Datos | Las consultas a la base de datos deberán implementarse mediante `PreparedStatement` para prevenir ataques de inyección SQL. |
| **RNF07** | Control de Acceso | El sistema deberá garantizar que cada usuario visualice únicamente las funcionalidades habilitadas para su rol. |

---

## 3. Arquitectura del Sistema
Para garantizar la escalabilidad y el mantenimiento del código, se implementó una arquitectura en capas fuertemente tipada:

* **Capa de Presentación (VIEW):** Desarrollada con componentes Swing puros (`GridBagLayout`, `BorderLayout`) para evitar dependencias de archivos autogenerados `.form`. Incluye paneles dinámicos inyectados de acuerdo al rol del usuario.
* **Capa de Negocio (SERVICE):** Encargada de centralizar las validaciones lógicas previas a la base de datos y la orquestación transaccional (ej. `PagoService`, `UsuarioService`).
* **Capa de Acceso a Datos (DAO / UTILS):** Abstracción JDBC que previene la inyección SQL mediante el uso de sentencias preparadas (`PreparedStatement`).

### 3.1 Manejo de Transacciones JDBC
**Transacciones con setAutoCommit(false).**
Ciertas operaciones del sistema involucran escrituras en múltiples tablas de forma simultánea. Para garantizar la integridad de los datos en estos casos, se implementó el manejo explícito de transacciones JDBC. El mecanismo consiste en:

* Desactivar el auto-commit con `connection.setAutoCommit(false)` impidiendo que cada sentencia SQL se confirme de forma independiente.
* Ejecutar todas las operaciones SQL necesarias dentro del mismo bloque.
* Confirmar los cambios con `connection.commit()` únicamente si todas las operaciones se completaron sin errores.
* Revertir todos los cambios con `connection.rollback()` en el bloque catch si cualquiera de las operaciones falla, dejando la base de datos en su estado original.

Este enfoque se aplicó en módulos críticos del sistema:
* **Módulo de Miembros (Maia):** El alta de un miembro requiere insertar registros en las tablas `Persona` y `Miembros` de forma atómica. Si la inserción en `Miembros` falla (por ejemplo, por un plan inexistente), el rollback deshace automáticamente la inserción previa en `Persona`, evitando registros huérfanos en la base de datos.
* **Módulo de Pagos (Gerónimo):** El registro de un pago implica insertar en la tabla `Pagos` y actualizar el estado del miembro en `Miembros`. Ambas operaciones se encapsulan en una única transacción. Si la actualización del estado falla, el pago no queda registrado, garantizando la consistencia entre el historial de pagos y el estado vigente del socio.
* **Módulo de Usuarios:** Al crear un empleado con acceso al sistema, se crea el registro físico en el staff y sus credenciales de autenticación en una sola transacción.

### 3.2 Seguridad y Módulos Destacados
El sistema cuenta con un modelo de Control de Acceso Basado en Roles (RBAC). Dependiendo de si quien inicia sesión es ADMIN o RECEPCIONISTA, la interfaz adapta sus componentes:

* **Gestión de Usuarios y Personal (Pablo y Jennifer):** Panel exclusivo para Administradores. Permite dar de alta personal (Instructores, Recepcionistas) y asignarles credenciales de acceso. Utiliza componentes bloqueables (`JComboBox` en modo Read-Only) para evitar vulnerabilidades como la asignación de IDs foráneos incorrectos.
* **Gestión de Instructores y Clases (Jennifer):** El sistema cuenta con validaciones de cupos máximos en tiempo real mediante subconsultas SQL, impidiendo la sobreinscripción a clases grupales.
* **Módulo de Reportes Financieros (Angelina):** Exclusivo para gerencia. Utiliza la librería iTextPDF para generar documentos dinámicos iterando sobre las fechas. A través de consultas complejas (`INNER JOIN` entre 4 tablas), agrupa los ingresos por mes, resta los sueldos fijos de los instructores y calcula el balance neto automáticamente.

## 4. Diseño Orientado a Objetos y Base de Datos
El dominio del problema fue modelado aplicando los pilares de la Programación Orientada a Objetos. Se destaca la utilización de herencia a partir de una clase abstracta madre `Persona`, de la cual derivan las entidades `Miembro` e `Instructor`, reutilizando atributos comunes como DNI, nombre y contacto.

A nivel de persistencia, se diseñó un esquema relacional en MySQL compuesto por 8 tablas principales (`persona`, `miembro`, `usuario_sistema`, `instructor`, `clase`, `inscripcion`, `pago` y `plan`) altamente normalizadas, asegurando la integridad referencial mediante claves foráneas.

## 5. Metodología de Trabajo y División de Módulos
El proyecto fue dividido en 5 Sprints incrementales, comenzando por el diseño de la base de datos, avanzando hacia las capas de persistencia y finalizando con la integración gráfica y generación de reportes.

El equipo de 6 integrantes se dividió las responsabilidades para asegurar un desarrollo paralelo eficiente:

* **Pablo:** Líder Técnico. Encargado del esquema arquitectónico, seguridad, Login y ABM de Usuarios.
* **Zoe:** Colíder y Core Frontend. Desarrolló la estructura contenedora (`VentanaPrincipal` con `CardLayout`), el control de vistas por rol y la conexión JDBC global (Singleton).
* **Maia:** Desarrolló el complejo módulo transaccional para el ABM de Miembros.
* **Gerónimo:** Implementó la capa de negocio financiera y el módulo de Pagos.
* **Jennifer:** Encargada de la interfaz y la lógica de la Gestión de Clases, Instructores e Inscripciones.
* **Angelina:** Llevó a cabo el cierre analítico programando el motor de Reportes visuales y su exportación a PDF.

Para la gestión y seguimiento de las tareas del proyecto, el equipo utilizó Trello como herramienta de administración de trabajo. Se creó un tablero compartido entre los 6 integrantes con 5 columnas que representaban el estado de cada tarea: *Backlog* (tareas identificadas pero no iniciadas), *To-Do* (tareas listas para comenzar), *In Progress* (en desarrollo), *Testing / Review* (en revisión por el equipo) y *Done* (completadas). Cada módulo del sistema contaba con su propia tarjeta asignada al integrante responsable, con un checklist de subtareas específicas que permitió verificar el avance de forma granular y coordinar la integración entre módulos sin bloqueos.

**Tablero Trello:** [https://trello.com/b/WhNm648r](https://trello.com/b/WhNm648r)

## 6. Despliegue y Repositorio

### Modalidad de despliegue
FitBase es una aplicación de escritorio local. No requiere conexión a Internet ni servidor web: se ejecuta directamente en la máquina del establecimiento donde está instalado el servidor MySQL. Para poner en funcionamiento el sistema en un nuevo equipo se deben cumplir los siguientes requisitos previos:

* Java JDK 17 o superior instalado.
* MySQL Server 8.x instalado y en ejecución (compatible con WAMP, XAMPP o instalación directa).
* Ejecutar el script SQL del proyecto para crear el esquema `mydb` y sus tablas.
* Compilar el proyecto con Maven (`mvn package`) y ejecutar el `.jar` generado.

### Repositorio del proyecto
El código fuente completo del proyecto se encuentra versionado con Git y alojado públicamente en GitHub, permitiendo el seguimiento del historial de cambios, la colaboración entre los integrantes del equipo y la revisión del código por parte de docentes:

**Repositorio:** [https://github.com/Paicodev/P2-PROYECTO-FINAL.git](https://github.com/Paicodev/P2-PROYECTO-FINAL.git)
