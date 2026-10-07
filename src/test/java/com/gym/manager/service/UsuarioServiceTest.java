package com.gym.manager.service;

import com.gym.manager.dao.UsuarioDAO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.gym.manager.model.UsuarioSistema;
import com.gym.manager.model.enums.RolUsuario;
import com.gym.manager.exceptions.DatosInvalidosException;


@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    static {
        System.setProperty("net.bytebuddy.experimental", "true");
    }
    
    @Mock
    //Objeto falso
    private UsuarioDAO usuarioDAO;

    @InjectMocks
    //Clase real que vamos a testear inyectando el mock
    private UsuarioService usuarioService;

   @Test
   @DisplayName("Login exitoso con creedenciales correctas")
   public void testAutenticar_CredencialesCorrectas_RetornaTrue(){
   //Arange
   when(usuarioDAO.validarLogin("admin", "1234")).thenReturn(true);
   //Act
   boolean resultado = usuarioService.autenticar("admin", "1234");
   //Assert
   assertTrue(resultado, "El login debería ser exitoso con creedenciales válidas");
   }

    @Test
    @DisplayName("Debe retornar false cuando la contraseña es incorrecta")
    public void testAutenticar_PasswordIncorrecto_RetornaFalse() {
        // Arrange: el DAO dice que no coincide
        when(usuarioDAO.validarLogin("admin", "claveErronea")).thenReturn(false);

        // Act
        boolean resultado = usuarioService.autenticar("admin", "claveErronea");

        // Assert
        assertFalse(resultado, "El login debería fallar con contraseña incorrecta.");
    }

    @Test
    @DisplayName("Debe retornar false sin consultar a la BD si el usuario está vacío")
    public void testAutenticar_UsuarioVacio_RetornaFalse() {
        // Act
        boolean resultado = usuarioService.autenticar("", "1234");

        // Assert
        assertFalse(resultado);
        // Bonus de Mockito: verificamos que NUNCA se haya llamado al DAO
        verify(usuarioDAO, never()).validarLogin(anyString(), anyString());
    }

    @Test
    @DisplayName("Debe lanzar excepción al intentar eliminar al Administrador principal (ID 1)")
    public void testEliminarUsuario_AdminPrincipalId1_LanzaExcepcion() {
        // Assert + Act: comprobamos que arroje la excepción esperada
        assertThrows(com.gym.manager.exceptions.DatosInvalidosException.class, () -> {
            usuarioService.eliminarUsuario(1);
        });

        // Verificamos que NUNCA haya llamado al DAO para borrarlo
        verify(usuarioDAO, never()).eliminar(1);
    }

    @Test
    @DisplayName("Debe guardar el usuario correctamente cuando los datos son válidos")
    public void testGuardarUsuario_DatosValidos_InvocaGuardarEnDAO() {
        // Arrange: creamos un usuario con todos los campos completos
        UsuarioSistema usuarioValido = new UsuarioSistema(0, "paguilar", "clave123", RolUsuario.ADMIN, null, 1);
        usuarioValido.setNombre("Pablo");
        usuarioValido.setApellido("Aguilar");
        usuarioValido.setDni("40123456");

        // Act
        usuarioService.guardarUsuario(usuarioValido);

        // Assert: comprobamos que el servicio haya llamado a usuarioDAO.guardar(usuarioValido) exactamente 1 vez
        verify(usuarioDAO, times(1)).guardar(usuarioValido);
    }

    @Test
    @DisplayName("Debe lanzar excepción si se intenta guardar un usuario con nombre vacío")
    public void testGuardarUsuario_NombreVacio_LanzaExcepcionYNoGuarda() {
        // Arrange: usuario con nombre vacío
        UsuarioSistema usuarioInvalido = new UsuarioSistema(0, "paguilar", "clave123", RolUsuario.ADMIN, null, 1);
        usuarioInvalido.setNombre("   "); // Nombre en blanco
        usuarioInvalido.setApellido("Aguilar");
        usuarioInvalido.setDni("40123456");

        // Act & Assert
        assertThrows(DatosInvalidosException.class, () -> {
            usuarioService.guardarUsuario(usuarioInvalido);
        });

        // Verificamos que al fallar la validación, el DAO NUNCA fue llamado
        verify(usuarioDAO, never()).guardar(any());
    }

    @Test
    @DisplayName("Debe permitir eliminar a un usuario común (ID distinto de 1)")
    public void testEliminarUsuario_UsuarioComun_LlamaEliminarEnDAO() {
        // Act: borramos a un empleado con ID 5
        usuarioService.eliminarUsuario(5);

        // Assert: verificamos que el DAO sí fue llamado con ese ID
        verify(usuarioDAO, times(1)).eliminar(5);
    }

    @Test
    @DisplayName("Debe actualizar el usuario correctamente cuando los datos son válidos")
    public void testActualizarUsuario_DatosValidos_InvocaActualizarEnDAO() {
        // Arrange
        UsuarioSistema usuarioExistente = new UsuarioSistema(3, "jdoe", "nuevaClave", RolUsuario.RECEPCIONISTA, null, 2);
        usuarioExistente.setNombre("John");
        usuarioExistente.setApellido("Doe");
        usuarioExistente.setDni("30999888");

        // Act
        usuarioService.actualizarUsuario(usuarioExistente);

        // Assert
        verify(usuarioDAO, times(1)).actualizar(usuarioExistente);
    }

    @Test
    @DisplayName("Debe retornar el rol correspondiente al consultar un usuario existente")
    public void testObtenerRol_UsuarioExistente_RetornaRol() {
        // Arrange: el DAO dice que "admin" es ADMIN
        when(usuarioDAO.obtenerRol("admin")).thenReturn("ADMIN");

        // Act
        String rol = usuarioService.obtenerRol("admin");

        // Assert
        assertEquals("ADMIN", rol);
        verify(usuarioDAO, times(1)).obtenerRol("admin");
    }

}