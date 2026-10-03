package modelo;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Controla el avance de la simulacion turno a turno y calcula las metricas
// que se muestran en el panel lateral (Actividad, Rendimiento, etc).
public class MotorJuego {
    private final List<Recurso> recursos;
    private final int maxTurnos;
    private int contadorFichas; // para generar ids unicos de fichas

    // Propiedades de JavaFX: la Vista se puede "enlazar" (bind) a estas y se
    // actualiza sola cada vez que cambian, sin que la Vista tenga que pedir nada.
    private final SimpleIntegerProperty turnoActual = new SimpleIntegerProperty(0);
    private final SimpleIntegerProperty throughput = new SimpleIntegerProperty(0);
    private final SimpleIntegerProperty fichasEnSistema = new SimpleIntegerProperty(0);
    private final SimpleIntegerProperty actividad = new SimpleIntegerProperty(0);
    private final SimpleDoubleProperty tiempoPromedio = new SimpleDoubleProperty(0);

    // Acumuladores internos para calcular el promedio de "tiempo en el sistema"
    private int sumaTiemposCompletados = 0;
    private int totalFichasCompletadas = 0;

    public MotorJuego(List<Recurso> recursos, int maxTurnos) {
        this.recursos = recursos;
        this.maxTurnos = maxTurnos;
    }

    // Carga fichas iniciales en la primera estacion (materia prima disponible al arrancar)
    public void cargarFichasIniciales(int cantidad) {
        Recurso primero = recursos.get(0);
        for (int i = 0; i < cantidad; i++) {
            primero.getColaEntrada().add(new Ficha(contadorFichas++));
        }
        actualizarFichasEnSistema();
    }

    // Avanza un turno completo siguiendo las reglas del juego
    public void siguienteTurno() {
        if (turnoActual.get() >= maxTurnos) {
            return; // ya se jugaron todos los turnos
        }

        // Foto del tamaño de cada cola antes de procesar este turno.
        // Esto evita que una ficha avance mas de una estacion en el mismo turno.
        Map<Recurso, Integer> tamanosIniciales = new HashMap<>();
        for (Recurso r : recursos) {
            tamanosIniciales.put(r, r.getColaEntrada().size());
        }

        int actividadDelTurno = 0;

        // Cada recurso tira sus dados y mueve fichas segun su capacidad
        for (Recurso r : recursos) {
            int capacidad = r.tirarDados();
            int disponibles = tamanosIniciales.get(r);

            // Regla de cuello de botella: nunca mover mas fichas de las que habia al inicio
            int aMover = Math.min(capacidad, disponibles);

            for (int i = 0; i < aMover; i++) {
                Ficha f = r.getColaEntrada().poll(); // poll() saca el primero de la cola
                f.sumarTurno();

                if (r.getSiguiente() != null) {
                    r.getSiguiente().getColaEntrada().add(f);
                } else {
                    // Salio por la ultima estacion: cuenta como throughput
                    throughput.set(throughput.get() + 1);
                    sumaTiemposCompletados += f.getTurnosEnSistema();
                    totalFichasCompletadas++;
                }
            }
            actividadDelTurno += aMover;
        }

        // Las fichas que se quedaron esperando tambien envejecen un turno
        for (Recurso r : recursos) {
            for (Ficha f : r.getColaEntrada()) {
                f.sumarTurno();
            }
        }

        // 4. Actualizar las metricas del panel lateral
        actividad.set(actividadDelTurno);
        actualizarFichasEnSistema();
        if (totalFichasCompletadas > 0) {
            tiempoPromedio.set((double) sumaTiemposCompletados / totalFichasCompletadas);
        }

        turnoActual.set(turnoActual.get() + 1);
    }

    private void actualizarFichasEnSistema() {
        int total = 0;
        for (Recurso r : recursos) {
            total += r.getColaEntrada().size();
        }
        fichasEnSistema.set(total);
    }

    // Deja todo como al principio y vuelve a cargar las fichas iniciales
    public void reiniciar(int fichasIniciales) {
        for (Recurso r : recursos) {
            r.getColaEntrada().clear();
        }
        turnoActual.set(0);
        throughput.set(0);
        actividad.set(0);
        tiempoPromedio.set(0);
        sumaTiemposCompletados = 0;
        totalFichasCompletadas = 0;
        contadorFichas = 0;
        cargarFichasIniciales(fichasIniciales);
    }

    // Getters de las propiedades, para enlazarlas (bind) desde la Vista
    public SimpleIntegerProperty turnoActualProperty() { return turnoActual; }
    public SimpleIntegerProperty throughputProperty() { return throughput; }
    public SimpleIntegerProperty fichasEnSistemaProperty() { return fichasEnSistema; }
    public SimpleIntegerProperty actividadProperty() { return actividad; }
    public SimpleDoubleProperty tiempoPromedioProperty() { return tiempoPromedio; }

    public int getMaxTurnos() { return maxTurnos; }
    public List<Recurso> getRecursos() { return recursos; }
}
