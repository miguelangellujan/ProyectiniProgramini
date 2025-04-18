package es.uah.matcomp.mp.teoria.gui.mvc.javafx.proyectoprogramacion;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

public class Tunel {
    private final int id;
    private final CyclicBarrier barrera;
    private final Semaphore pasoIndividual;

    public Tunel(int id) {
        this.id = id;
        this.barrera = new CyclicBarrier(3);           // Se esperan 3 humanos
        this.pasoIndividual = new Semaphore(1);        // Solo 1 humano puede cruzar a la vez
    }

    public void cruzar(Humano humano, boolean saliendo) throws InterruptedException {
        Logger.log(STR."\{humano.getIdHumano()} esperando en túnel \{id} (\{saliendo ? "saliendo" : "entrando"})");

        try {
            // Esperar a que se forme un grupo de 3 humanos
            barrera.await();
        } catch (BrokenBarrierException e) {
            Logger.log(STR."Error en la barrera del túnel \{id}");
            return;
        }
        // Cruzar uno a uno usando un semáforo
        pasoIndividual.acquire();
        try {
            Logger.log(STR."\{humano.getIdHumano()} cruzando túnel \{id}");
            Thread.sleep(1000); // Simula el cruce del túnel
        } finally {
            pasoIndividual.release();
        }
    }
}