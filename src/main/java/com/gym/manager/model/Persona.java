package com.gym.manager.model;
import com.gym.manager.exceptions.DatosInvalidosException; //importo la excepcion que cree para validar los datos de las personas

public abstract class Persona {
    private int id;
    private String nombre;
    private String apellido;
    private String dni;
    private String email;
    private String telefono;
    
    public Persona(int id, String nombre, String apellido, String dni, String email, String telefono){
        this.id = id;
        setNombre(nombre);
        setApellido(apellido);
        setDni(dni);
        setEmail(email);
        setTelefono(telefono);
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new DatosInvalidosException("El nombre no puede estar vacio");
        }
        this.nombre = nombre;
    }
    public String getApellido() {
        return apellido;
    }
    public void setApellido(String apellido) {
        if (apellido == null || apellido.trim().isEmpty()) {
            throw new DatosInvalidosException("El apellido no puede estar vacio");
        }
        this.apellido = apellido;
    }
    public String getDni() {
        return dni;
    }
    public void setDni(String dni) {
        if (dni == null || dni.trim().isEmpty()) {
            throw new DatosInvalidosException("El DNI no puede estar vacio");
        }
        this.dni = dni;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new DatosInvalidosException("El email no puede estar vacio");
        } else if (!email.contains("@") || !email.contains(".")) {
            throw new DatosInvalidosException("El email no es valido");
        }
        this.email = email;
    }
    public String getTelefono() {
        return telefono;
    }
    public void setTelefono(String telefono) {
        if (telefono == null || telefono.trim().isEmpty()) {
            throw new DatosInvalidosException("El telefono no puede estar vacio");
        }
        this.telefono = telefono;
    }

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }
    public boolean validarDatos() {
        return nombre != null && !nombre.trim().isEmpty() &&
                apellido != null && !apellido.trim().isEmpty() &&
                dni != null && !dni.trim().isEmpty() &&
                email != null && !email.trim().isEmpty() && email.contains("@") && email.contains(".") &&
                telefono != null && !telefono.trim().isEmpty();
    }
    @Override
    public String toString() {
        return "ID: " + id + ", Nombre: " + getNombreCompleto() + ", DNI: " + dni + ", Email: " + email + ", Telefono: " + telefono;
    }
}
