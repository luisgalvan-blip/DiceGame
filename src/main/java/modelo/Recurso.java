package modelo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

// Representa una estacion de trabajo dentro de la linea de produccion.
// Usa una Cola (Queue) para guardar las fichas que esperan ser procesadas.
public class Recurso {
    private final String nombre;
    private final Queue<Ficha> colaEntrada; // FIFO: las fichas salen en el orden que llegaron
    private final List<Dado> dadosAsignados; // dados que el jugador coloco en este recurso
    private Recurso siguiente; // siguiente estacion de la cadena (null si es la ultima)

    public Recurso(String nombre) {
        this.nombre = nombre;
        this.colaEntrada = new LinkedList<>(); // LinkedList implementa Queue
        this.dadosAsignados = new ArrayList<>();
    }

    public String getNombre() {
        return nombre;
    }

    public Queue<Ficha> getColaEntrada() {
        return colaEntrada;
    }

    public List<Dado> getDadosAsignados() {
        return dadosAsignados;
    }

    public Recurso getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(Recurso siguiente) {
        this.siguiente = siguiente;
    }

    // Tira todos los dados asignados y devuelve la capacidad total del turno
    public int tirarDados() {
        int capacidad = 0;
        for (Dado d : dadosAsignados) {
            capacidad += d.tirar();
        }
        return capacidad;
    }
}