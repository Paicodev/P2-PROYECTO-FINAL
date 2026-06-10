# 🗄️ Base de Datos - FitBase (Gym Manager)

Esta carpeta contiene la estructura de la base de datos MySQL necesaria para correr el sistema localmente.

## Instrucciones para el Setup Inicial:

1. Abre **MySQL Workbench** o tu cliente SQL preferido (DBeaver, phpMyAdmin).
2. Conéctate a tu servidor local (`localhost:3306`) con tu usuario `root`.
3. Abre el archivo `schema.sql` que se encuentra en esta carpeta.
4. Ejecuta todo el script (botón del rayito). Esto creará el esquema `mydb` y todas sus tablas automáticamente.
5. ¡Listo! Ya puedes ejecutar el proyecto Java y probar los DAOs.

## Instrucciones para modificar el modelo:

Si necesitas agregar una nueva tabla o columna:
1. **NO** edites el `.sql` a mano.
2. Abre el archivo `modelo_relacional.mwb` en MySQL Workbench.
3. Haz tus cambios visuales en el diagrama.
4. Ve a `File -> Export -> Forward Engineer SQL CREATE Script...` y sobreescribe el archivo `script_inicial.sql`.
5. Haz un commit en GitHub avisando al equipo que deben volver a correr el script.