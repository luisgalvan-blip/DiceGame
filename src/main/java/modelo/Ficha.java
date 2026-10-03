package modelo;

public class Ficha {
    private final int id;
    private int turnosEnSistema; // cuantos turnos lleva dentro del sistema sin salir

    public Ficha(int id) {
        this.id = id;
        this.turnosEnSistema = 0;
    }

    // Se llama una vez por turno mientras la ficha siga dentro del sistema
    public void sumarTurno() {
        turnosEnSistema++;
    }

    public int getId() {
        return id;
    }

    public int getTurnosEnSistema() {
        return turnosEnSistema;
    }
}