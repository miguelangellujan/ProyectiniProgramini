package es.uah.matcomp.mp.teoria.gui.mvc.javafx.proyectoprogramacion;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class Tunel {
    private final int id;
    private final CyclicBarrier barrera;
    private final Semaphore pasoIndividual;

    // Contadores para controlar prioridad
    private final AtomicInteger esperandoEntrar = new AtomicInteger(0);
    private final AtomicInteger esperandoSalir = new AtomicInteger(0);

    public Tunel(int id) {
        this.id = id;
        this.barrera = new CyclicBarrier(3);
        this.pasoIndividual = new Semaphore(1);
    }

    public void cruzar(Humano humano, boolean saliendo) throws InterruptedException {
        if (saliendo) {
            esperandoSalir.incrementAndGet();
            // Espera activa si hay humanos queriendo entrar
            while (esperandoEntrar.get() > 0) {
                Thread.sleep(100); // pequeña espera para no saturar la CPU
            }
        } else {
            esperandoEntrar.incrementAndGet();
        }

        Logger.log(STR."\{humano.getIdHumano()} esperando en túnel \{id} (\{saliendo ? "saliendo" : "entrando"})");

        try {
            barrera.await(); // espera a formar grupo de 3
        } catch (BrokenBarrierException e) {
            Logger.log(STR."Error en la barrera del túnel \{id}, recreando...");
            return;
        }

        pasoIndividual.acquire();
        try {
            Logger.log(STR."\{humano.getIdHumano()} cruzando túnel \{id}");
            Thread.sleep(1000);
        } finally {
            pasoIndividual.release();
            if (saliendo) {
                esperandoSalir.decrementAndGet();
            } else {
                esperandoEntrar.decrementAndGet();
            }
        }
    }
}