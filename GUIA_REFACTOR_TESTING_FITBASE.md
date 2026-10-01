# 🏋️ FitBase - Documento Maestro de Contexto, Arquitectura y Testing
**Materia:** Programación II — **Universidad Nacional de Villa Mercedes (UNViMe)**  
**Cátedra:** Profesores Isray Nahir, Lucero Walter  
**Repositorio:** [https://github.com/Paicodev/P2-PROYECTO-FINAL.git](https://github.com/Paicodev/P2-PROYECTO-FINAL.git)  
**Equipo de Desarrollo:** Agüero Gerónimo, Aguilar Jennifer, Aguilar Pablo (Líder Técnico), Brambilla Zoe, Bulacio Maia, Garavaglia Angelina.

---

## 1. Visión General del Sistema y Alcance
**FitBase** es un Sistema de Gestión Administrativa y Punto de Venta Interno (MIS / POS) para gimnasios, desarrollado como aplicación de escritorio en **Java 17+**. Su objetivo es digitalizar la operativa diaria, restringiendo accesos y vistas mediante un control de acceso basado en roles (**RBAC**):
* **Dueño / Administrador:** Acceso irrestricto a métricas financieras, reportes analíticos en PDF, alta/baja de staff con credenciales y gestión de instructores.
* **Recepcionista / Operario:** Operaciones cotidianas de mostrador: registro de socios, cobro de mensualidades, inscripción a clases grupales/personales y control de asistencia.

### Requerimientos Clave (RF / RNF):
* **RNF01 (Interfaz):** Java Swing puro (sin archivos autogenerados `.form`), usando `BorderLayout`, `GridBagLayout` y navegación Single Page Application (SPA) con `CardLayout`.
* **RNF03 y RNF05 (Persistencia):** Base de datos relacional MySQL 8.x con conectividad **JDBC pura** (sin frameworks ORM como Hibernate o JPA).
* **RNF04 (Patrón DAO):** Desacoplamiento total entre el dominio y el acceso a datos mediante objetos de acceso a datos (`DAO`).
* **RNF06 (Seguridad SQL):** Uso estricto de `PreparedStatement` en todas las consultas para parametrizar datos y prevenir inyecciones SQL.
* **Transacciones ACID Manuales:** Control transaccional explícito (`conn.setAutoCommit(false)`, `conn.commit()`, `conn.rollback()`) en operaciones que involucran múltiples tablas (ej. Persona + Miembro, Persona + Staff, o Pago + Estado de Miembro).
* **Concurrencia:** Ejecución de consultas pesadas fuera del hilo de la interfaz gráfica (**Event Dispatch Thread - EDT**) mediante `SwingWorker`.
* **Reportes:** Generación de balances dinámicos e informes de inscriptos en PDF utilizando **iTextPDF**.
* **Testing:** Pruebas unitarias de lógica de negocio y dominio con **JUnit 5** y aislamiento de base de datos con **Mockito**.

---

## 2. Arquitectura del Sistema (Patrón en Capas)

```
┌─────────────────────────────────────────────────────────────┐
│ 1. CAPA DE PRESENTACIÓN (VIEW / SWING)                      │
│ VentanaPrincipal (CardLayout), LoginFrame, Paneles y Diálogos│
└──────────────────────────────┬──────────────────────────────┘
                               │ Inyecta / Llama
┌──────────────────────────────▼──────────────────────────────┐
│ 2. CAPA DE NEGOCIO (SERVICE)                                │
│ UsuarioService, MiembroService, PagoService, ClaseService... │
│ -> Contiene reglas de negocio, validaciones y orquestación. │
└──────────────────────────────┬──────────────────────────────┘
                               │ Inyecta / Llama
┌──────────────────────────────▼──────────────────────────────┐
│ 3. CAPA DE PERSISTENCIA (DAO)                               │
│ UsuarioDAO, MiembroDAO, PagoDAO, ClaseDAO, InscripcionesDAO  │
│ -> Sentencias SQL con PreparedStatement e interface DAO<T>. │
└──────────────────────────────┬──────────────────────────────┘
                               │ Obtiene java.sql.Connection
┌──────────────────────────────▼──────────────────────────────┐
│ 4. INFRAESTRUCTURA Y CONEXIÓN                               │
│ DatabaseManager (Patrón Singleton JDBC)                     │
└─────────────────────────────────────────────────────────────┘
                               ▲
┌──────────────────────────────┴──────────────────────────────┐
│ DOMINIO TRANSVERSAL (POJOs / MODELOS)                       │
│ Persona (Abstracta) <- Miembro, Instructor                  │
│ ClaseGimnasio (Abstracta) <- ClaseGrupal, ClasePersonal      │
│ UsuarioSistema (Composición con Persona), Pago, Plan...     │
└─────────────────────────────────────────────────────────────┘
```

### Decisiones de Diseño Orientado a Objetos (POO):
1. **Herencia y Polimorfismo:**
   * `Persona` es una clase abstracta que centraliza atributos (`nombre`, `apellido`, `dni`, `email`, `telefono`) y validaciones de formato. De ella heredan `Miembro` e `Instructor`.
   * `ClaseGimnasio` es abstracta y define el método polimórfico `getTipoClase()`. `ClaseGrupal` añade el atributo `capacidadMax`, mientras que `ClasePersonal` modela entrenamientos personalizados sin límite de cupo estándar.
2. **Composición sobre Herencia:**
   * `UsuarioSistema` **NO** hereda de `Persona`. Utiliza composición (contiene un `personaIdPersona` como clave foránea lógica), ya que no todas las personas del gimnasio poseen usuario y contraseña en el sistema.
3. **Patrón Singleton:**
   * `DatabaseManager` tiene constructor privado y método sincronizado `getInstance()` para asegurar una única conexión JDBC compartida y evitar agotar el pool de conexiones de MySQL.

---

## 3. Matriz de Responsabilidades, Diagnóstico y Plan por Integrante

### 👤 1. Pablo Aguilar (Líder Técnico)
* **Módulo:** Autenticación, Seguridad y Gestión de Usuarios (Staff).
* **Archivos Clave:** `UsuarioSistema.java`, `UsuarioDAO.java`, `UsuarioService.java`, `LoginFrame.java`, `PanelUsuarios.java`.
* **Diagnóstico / Refactor a Realizar:**
  1. *Bypass de arquitectura:* `LoginFrame` llama directamente a `new UsuarioDAO()`. Refactorizar creando en `UsuarioService`:
     ```java
     public Optional<UsuarioSistema> autenticar(String username, String password)
     ```
  2. *Inyección de dependencias:* Agregar en `UsuarioService` el constructor:
     ```java
     public UsuarioService(UsuarioDAO usuarioDAO) { this.usuarioDAO = usuarioDAO; }
     ```
  3. *Eliminar test frágil:* Borrar `UsuarioDAOTest.java` (dependía de datos manuales en MySQL).
* **Batería de Tests con JUnit 5 y Mockito (`UsuarioServiceTest`):**
  * `testAutenticar_CredencialesCorrectas_RetornaUsuario()`
  * `testAutenticar_PasswordIncorrecto_RetornaOptionalVacio()`
  * `testEliminarUsuario_AdminPrincipalId1_LanzaExcepcion()` (Regla de negocio: no se puede borrar al superadmin).
  * `testGuardarUsuario_CamposVacios_LanzaDatosInvalidosException()`.
* **Preguntas de Examen:** ¿Por qué `UsuarioSistema` usa composición y no herencia? ¿Cómo previene `PreparedStatement` la inyección SQL? ¿Qué hace `@Mock` y `@InjectMocks`?

---

### 👤 2. Zoe Brambilla (Colíder y Core Frontend)
* **Módulo:** Navegación Principal (CardLayout), Control de Roles y Conexión Global.
* **Archivos Clave:** `VentanaPrincipal.java`, `DatabaseManager.java`, `DatabaseManagerTest.java`, `pom.xml`.
* **Diagnóstico / Refactor a Realizar:**
  1. *Bug de conexión cerrada en Singleton:* En `DatabaseManager.java`, si se invoca `cerrarConexion()`, la variable estática `instance` debe resetearse a `null` (`instance = null; connection = null;`). Si no se hace, futuras llamadas devuelven una conexión inutilizable.
  2. *Actualización de dependencias en `pom.xml`:* Incorporar `mockito-core` y `mockito-junit-jupiter` (versión 5.11.0).
  3. *Control estricto de vistas:* En `VentanaPrincipal`, evitar instanciar paneles administrativos (`PanelUsuarios`, `PanelReportes`) si el rol del usuario autenticado es `RECEPCIONISTA`.
* **Batería de Tests con JUnit 5 (`DatabaseManagerTest`):**
  * Probar que `getInstance()` devuelve siempre la misma referencia de memoria (`assertSame`).
  * Probar que tras `cerrarConexion()`, una nueva llamada a `getInstance()` genera una instancia válida y abierta.
* **Preguntas de Examen:** ¿Qué ventajas y desventajas tiene el patrón Singleton frente a un Pool de Conexiones? ¿Cómo funciona el ciclo de vida de los paneles en un `CardLayout`?

---

### 👤 3. Maia Bulacio (Desarrollo Backend Transaccional)
* **Módulo:** Alta, Baja y Modificación de Miembros (Socios) y Transacciones ACID.
* **Archivos Clave:** `Miembro.java`, `MiembroDAO.java`, `MiembroService.java`, `PanelMiembros.java`.
* **Diagnóstico / Refactor a Realizar:**
  1. *Bug de excepciones silenciadas:* En `MiembroDAO.java`, los métodos `actualizar()` y `eliminar()` capturan `SQLException` pero solo imprimen por consola sin relanzar `ConexionBDException`. La vista cree que la operación fue un éxito aunque la base de datos falle. **Debe relanzar la excepción.**
  2. *Inyección de dependencias:* Agregar en `MiembroService`:
     ```java
     public MiembroService(MiembroDAO miembroDAO) { this.miembroDAO = miembroDAO; }
     ```
  3. *Side-effect en consultas:* El método `actualizarEstadosVencidos()` ejecuta un `UPDATE` dentro de `obtenerTodos()`. Asegurar que no interfiera en transacciones activas.
* **Batería de Tests con JUnit 5 y Mockito:**
  * **Unitario Puro (`MiembroTest`):** Probar `diasParaVencer()` con fechas futuras, pasadas y de hoy; probar `estaActivo()`.
  * **Con Mockito (`MiembroServiceTest`):**
    * `testGuardarMiembro_DniDuplicado_LanzaExcepcionYNoGuarda()`: Simular `miembroDAO.buscarPorDNI(...)` devolviendo un socio existente y verificar con `verify(miembroDAO, never()).guardar(any())`.
    * `testGuardarMiembro_VencimientoAnteriorAInscripcion_LanzaExcepcion()`.
    * `testGuardarMiembro_DatosValidos_InvocaGuardarEnDAO()`.
* **Preguntas de Examen:** ¿Qué significa que una transacción sea Atómica? ¿Por qué se hace rollback en el catch? ¿Por qué se restaura `conn.setAutoCommit(true)` en el bloque `finally`?

---

### 👤 4. Gerónimo Agüero (Capa Financiera y Negocio)
* **Módulo:** Registro de Pagos, Cálculo de Vencimientos y Mora.
* **Archivos Clave:** `Pago.java`, `PagoDAO.java`, `PagoService.java`, `PanelPagos.java`.
* **Diagnóstico / Refactor a Realizar:**
  1. *Violación de arquitectura en capas:* En `PagoService.java`, hay sentencias SQL directas (`UPDATE Miembros...` y `SELECT COUNT(*) FROM Pagos...`). Esas consultas deben trasladarse a `MiembroDAO` o `PagoDAO`. El Service solo orquesta entidades y reglas de negocio.
  2. *Inyección de dependencias:* Agregar en `PagoService`:
     ```java
     public PagoService(PagoDAO pagoDAO) { this.pagoDAO = pagoDAO; }
     ```
  3. *Inconsistencia de negocio al eliminar:* Si se elimina un pago en `PanelPagos`, documentar la regla o revertir el vencimiento del socio.
* **Batería de Tests con JUnit 5 y Mockito:**
  * **Unitario Puro (`PagoTest`):** Probar `pago.calcularMora(0.10)` cuando el estado es `VENCIDO` (retorna el 10%) vs cuando es `PAGADO` (retorna 0.0); probar formato de `generarRecibo()`.
  * **Con Mockito (`PagoServiceTest`):**
    * `testRegistrarPago_MontoMenorOIgualACero_LanzaDatosInvalidosException()`.
    * `testRegistrarPago_MiembroNulo_LanzaDatosInvalidosException()`.
    * `testRegistrarPago_MensualidadDuplicadaEnElMismoMes_LanzaExcepcion()`.
* **Preguntas de Examen:** ¿Por qué un test unitario de `PagoService` no debe conectarse a MySQL? ¿Cómo se simula el comportamiento de `pagoDAO.guardar(any())` en Mockito?

---

### 👤 5. Jennifer Aguilar (Gestión de Clases y Operatoria)
* **Módulo:** Instructores, Clases (Grupales/Personales) e Inscripciones con Control de Cupos.
* **Archivos Clave:** `ClaseGimnasio.java`, `ClaseGrupal.java`, `ClasePersonal.java`, `ClaseService.java`, `InscripcionesDAO.java`, `PanelClases.java`, `PanelInscripciones.java`.
* **Diagnóstico / Refactor a Realizar:**
  1. *Falta de Capa Service en Inscripciones:* `InscripcionesDAO.java` contiene validaciones lógicas (`validar estado del socio`, `validar cupo de clase grupal`, `validar clase personal asignada`). Esto debe extraerse a una nueva clase de negocio `InscripcionesService.java` para respetar la separación de responsabilidades.
  2. *Inyección de dependencias:* Permitir constructor inyectable en `ClaseService(ClaseDAO dao)` e `InscripcionesService(InscripcionesDAO dao)`.
  3. *Eliminar test frágil:* Reemplazar `InscripcionesDAOTest.java` por pruebas con Mockito.
* **Batería de Tests con JUnit 5 y Mockito:**
  * **Unitario Puro (`ClaseGimnasioTest`):** Verificar polimorfismo entre `ClaseGrupal.getTipoClase()` ("GRUPAL") y `ClasePersonal.getTipoClase()` ("PERSONAL"); validar capacidad máxima.
  * **Con Mockito (`InscripcionesServiceTest`):**
    * `testInscribir_SocioNoActivo_LanzaExcepcion()`.
    * `testInscribir_ClaseGrupalLlena_LanzaExcepcionPorCupo()`.
    * `testInscribir_ClasePersonalYaOcupada_LanzaExcepcion()`.
* **Preguntas de Examen:** ¿Qué es una clase abstracta y por qué no se puede instanciar directamente? ¿Cómo aplica el polimorfismo en el cálculo o determinación de cupos?

---

### 👤 6. Angelina Garavaglia (Analítica y Reportería)
* **Módulo:** Motor de Reportes, Cálculo de Balances Financieros y Exportación a PDF (iText).
* **Archivos Clave:** `PanelReportes.java`, `itextpdf` (dependencia Maven).
* **Diagnóstico / Refactor a Realizar:**
  1. *Separación de responsabilidades:* `PanelReportes.java` tiene consultas SQL (`SELECT SUM(sueldo)...`) mezcladas con llamadas a la librería `Document` de iTextPDF dentro de los ActionListeners de Swing.
  2. *Creación de `ReporteService.java`:* Mover el cálculo contable (Ingresos de Pagos menos Gastos Fijos de Sueldos de Instructores = Balance Neto) a un servicio independiente.
* **Batería de Tests con JUnit 5 (`ReporteServiceTest`):**
  * Probar que el algoritmo de cálculo de balance arroje superávit o déficit correctamente dados una lista de pagos y una suma de sueldos fijos.
  * Probar que si no hay pagos registrados en un mes, el balance refleje el saldo negativo correspondiente a los costos fijos.
* **Preguntas de Examen:** ¿Cómo interactúa el motor de iTextPDF con las tablas (`PdfPTable`) y párrafos (`Paragraph`)? ¿Por qué la lógica de balance debe estar aislada de la ventana de Swing?

---

## 4. Guía Fundamental de Testing para la Mesa de Examen

### ¿Qué es JUnit 5 y por qué lo usamos?
Es el framework estándar de pruebas unitarias en Java. Nos provee anotaciones (`@Test`, `@BeforeEach`, `@DisplayName`) y aserciones (`assertEquals`, `assertTrue`, `assertFalse`, `assertThrows`, `assertNotNull`) para verificar automáticamente el comportamiento esperado del código.

### ¿Qué es Mockito y por qué es obligatorio en la Capa Service?
Un test unitario **no debe depender de recursos externos** (MySQL, red, archivos de disco).
Si probamos `MiembroService.guardarMiembro(...)`:
* No queremos que cree un registro real en la tabla `Persona` de MySQL.
* Queremos aislar a `MiembroService`.
* Para lograrlo, le pasamos un **Mock** de `MiembroDAO`. Un Mock es un objeto falso simulado en memoria que programamos para responder lo que necesitemos:
  ```java
  // Simulamos que al buscar el DNI "12345678", la BD dice que ya existe
  when(miembroDAOMock.buscarPorDNI("12345678")).thenReturn(Optional.of(miembroExistente));
  ```
* Luego verificamos que el servicio tome la decisión correcta (lanzar excepción y **no** guardar):
  ```java
  assertThrows(DatosInvalidosException.class, () -> miembroService.guardarMiembro(nuevoMiembro));
  verify(miembroDAOMock, never()).guardar(any());
  ```

---

## 5. Dependencias Maven para el Testing (`pom.xml`)

Para que el proyecto soporte Mockito con JUnit 5, se deben asegurar las siguientes dependencias en la sección `<dependencies>` de `pom.xml`:

```xml
<!-- JUnit 5 Jupiter API & Engine -->
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-api</artifactId>
    <version>5.10.0</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-engine</artifactId>
    <version>5.10.0</version>
    <scope>test</scope>
</dependency>

<!-- Mockito Core & JUnit 5 Extension -->
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <version>5.11.0</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-junit-jupiter</artifactId>
    <version>5.11.0</version>
    <scope>test</scope>
</dependency>
```
