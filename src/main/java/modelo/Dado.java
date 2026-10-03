package modelo;

import java.util.Random;

// Representa un dado de 6 caras que se puede asignar a un Recurso (estacion).
public class Dado {
    private static final Random GENERADOR = new Random();
    private int valorActual; // ultimo resultado obtenido al tirar

    // Tira el dado y devuelve un numero entre 1 y 6
    public int tirar() {
        valorActual = GENERADOR.nextInt(6) + 1;
        return valorActual;
    }

    public int getValorActual() {
        return valorActual;
    }
}