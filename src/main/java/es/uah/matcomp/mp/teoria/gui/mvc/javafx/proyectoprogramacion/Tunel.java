package es.uah.matcomp.mp.teoria.gui.mvc.javafx.proyectoprogramacion;

import java.util.concurrent.locks.*;

public class Tunel {
    private final int id;
    private final Lock lock = new ReentrantLock(true);
    private final Condition grupoFormado = lock.newCondition();
    private int humanosEsperando = 0;

    public Tunel(int id) {
        this.id = id;
    }

    public void cruzar(Humano humano, boolean saliendo) throws InterruptedException {
        lock.lock();
        try {
            humanosEsperando++;
            Logger.log(humano.getIdHumano() + " esperando en túnel " + id + " (" + (saliendo ? "saliendo" : "entrando") + ")");
            while (humanosEsperando < 3) {
                grupoFormado.await();
            }
            grupoFormado.signalAll();
            Logger.log(humano.getIdHumano() + " cruzando túnel " + id);
            Thread.sleep(1000);
            humanosEsperando--;
            if (humanosEsperando == 0) {
                grupoFormado.signalAll();
            }
        } finally {
            lock.unlock();
        }
    }
}
