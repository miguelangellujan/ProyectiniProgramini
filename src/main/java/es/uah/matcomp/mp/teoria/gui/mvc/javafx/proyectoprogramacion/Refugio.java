package es.uah.matcomp.mp.teoria.gui.mvc.javafx.proyectoprogramacion;

import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public class Refugio {
    private final Semaphore semaforoComedor = new Semaphore(10, true);
    private final Semaphore semaforoDescanso = new Semaphore(15, true);
    private final Lock lockComida = new ReentrantLock();
    private final Condition hayComida = lockComida.newCondition();
    private int comidaDisponible = 0;
    private final ConcurrentLinkedQueue<Humano> humanosEnRefugio = new ConcurrentLinkedQueue<>();

    public void zonaComun(Humano humano) throws InterruptedException {
        Logger.log(STR."\{humano.getIdHumano()} en zona común");
        Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 2001));
    }

    public void descansar(Humano humano) throws InterruptedException {
        semaforoDescanso.acquire();
        try {
            Logger.log(STR."\{humano.getIdHumano()} descansando");
            Thread.sleep(ThreadLocalRandom.current().nextInt(2000, 4001));
        } finally {
            semaforoDescanso.release();}
    }
    public void comer(Humano humano) throws InterruptedException {
        semaforoComedor.acquire();
        try {
            lockComida.lock();
            try {
                while (comidaDisponible < 1) {
                    hayComida.await();
                }
                comidaDisponible--;
                Logger.log(STR."\{humano.getIdHumano()} comiendo, Total comida: \{comidaDisponible}");
            } finally {
                lockComida.unlock();
            }
            Logger.log(STR."\{humano.getIdHumano()} comiendo");
            Thread.sleep(ThreadLocalRandom.current().nextInt(3000, 5001));
        } finally {
            semaforoComedor.release();
        }
    }
    public void depositarComida(int cantidad) {
        lockComida.lock();
        try {
            comidaDisponible += cantidad;
            hayComida.signalAll();
            Logger.log(STR."Depositadas \{cantidad} unidades de comida. Total: \{comidaDisponible}");
        } finally {
            lockComida.unlock();
        }
    }
    public void agregarHumano(Humano humano) {
        humanosEnRefugio.add(humano);
        Logger.log(STR."\{humano.getIdHumano()} ha entrado al refugio");
    }
    public void removerHumano(Humano humano) {
        humanosEnRefugio.remove(humano);
    }
    public void recuperarse(Humano humano) throws InterruptedException {
        Logger.log(STR."\{humano.getIdHumano()} recuperándose en enfermería");
        Thread.sleep(2000);
    }
}