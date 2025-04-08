package es.uah.matcomp.mp.teoria.gui.mvc.javafx.proyectoprogramacion;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class Refugio {
    private final List<Humano> humanosEnRefugio;
    private final Semaphore semaforoComedor;
    private final Semaphore semaforoDescanso;
    private final Random random;
    private int comidaDisponible;

    public Refugio() {
        this.humanosEnRefugio = new ArrayList<>();
        this.semaforoComedor = new Semaphore(10, true); // Capacidad para 10 humanos comiendo
        this.semaforoDescanso = new Semaphore(15, true); // Capacidad para 15 humanos descansando
        this.random = new Random();
        this.comidaDisponible = 0;
    }

    public synchronized void agregarHumano(Humano humano) {
        humanosEnRefugio.add(humano);
        Logger.log("Humano " + humano.getIdHumano() + " ha entrado al refugio");
    }

    public synchronized void removerHumano(Humano humano) {
        humanosEnRefugio.remove(humano);
        Logger.log("Humano " + humano.getIdHumano() + " ha salido del refugio");
    }

    public void zonaComun(Humano humano) throws InterruptedException {
        Logger.log("Humano " + humano.getIdHumano() + " está en la zona común");
        Thread.sleep(random.nextInt(1000) + 1000); // 1-2 segundos
    }

    public void prepararParaSalida(Humano humano) {
        Logger.log("Humano " + humano.getIdHumano() + " se prepara para salir");
    }

    public void descansar(Humano humano) throws InterruptedException {
        semaforoDescanso.acquire();
        try {
            Logger.log("Humano " + humano.getIdHumano() + " está descansando");
            Thread.sleep(random.nextInt(2000) + 2000); // 2-4 segundos
        } finally {
            semaforoDescanso.release();
        }
    }

    public void comer(Humano humano) throws InterruptedException {
        semaforoComedor.acquire();
        try {
            synchronized (this) {
                while (comidaDisponible < 1) {
                    Logger.log("Humano " + humano.getIdHumano() + " espera comida");
                    wait();
                }
                comidaDisponible--;
            }
            Logger.log("Humano " + humano.getIdHumano() + " está comiendo");
            Thread.sleep(random.nextInt(2000) + 3000); // 3-5 segundos
        } finally {
            semaforoComedor.release();
        }
    }

    public synchronized void depositarComida(int cantidad) {
        comidaDisponible += cantidad;
        notifyAll();
        Logger.log("Se han depositado " + cantidad + " piezas de comida. Total: " + comidaDisponible);
    }

    public synchronized int getHumanosEnRefugio() {
        return humanosEnRefugio.size();
    }

    public void recuperarse(Humano humano) throws InterruptedException {
        Logger.log("Humano " + humano.getIdHumano() + " se recupera de sus heridas");
        Thread.sleep(random.nextInt(2000) + 3000); // 3-5 segundos
        humano.setMarcado(false);
    }
}
