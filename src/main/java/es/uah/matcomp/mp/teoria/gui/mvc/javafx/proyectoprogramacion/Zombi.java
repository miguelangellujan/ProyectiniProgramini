package es.uah.matcomp.mp.teoria.gui.mvc.javafx.proyectoprogramacion;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class Zombi extends Thread {
    private final String id;
    private final ZonaRiesgo zonaRiesgo;
    private final AtomicInteger muertes = new AtomicInteger(0);

    public Zombi(String id, ZonaRiesgo zonaRiesgo) {
        this.id = id;
        this.zonaRiesgo = zonaRiesgo;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                int area = ThreadLocalRandom.current().nextInt(zonaRiesgo.getNumAreas());
                zonaRiesgo.moverAZona(this, area);

                Humano humano = zonaRiesgo.buscarHumano(area);
                if (humano != null&& humano.estaVivo()) {
                    atacar(humano);
                } else {
                    // Verifica la interrupción antes de dormir
                    if (!Thread.currentThread().isInterrupted()) {
                        Thread.sleep(ThreadLocalRandom.current().nextInt(2000, 3001));
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private synchronized void atacar(Humano humano) throws InterruptedException {
        Logger.log(String.format("%s atacando a %s", id, humano.getIdHumano()));
        Thread.sleep(ThreadLocalRandom.current().nextInt(500, 1501));

            // Verifica si el humano ya ha muerto
            if (!humano.estaVivo()) {
                return; // El humano ya fue atacado anteriormente y está muerto
            }

            // El humano intenta defenderse
            if (!humano.intentarDefenderse()) {
                humano.morir(); // El humano muere
                muertes.incrementAndGet();
                zonaRiesgo.registrarMuerte(id);
                Logger.log(String.format("%s muerto. Renace como zombi", humano.getIdHumano()));

                // Crear un nuevo zombi a partir del humano muerto
                String nuevoId = STR."Z\{humano.getIdHumano()}"; // Evita usar la misma ID
                Zombi zombi = new Zombi(nuevoId, zonaRiesgo);
                zombi.start();
            } else {
                // Si el humano se defendió, marcarlo
                humano.setMarcado(true);
                Logger.log(String.format("%s se defendió pero quedó marcado", humano.getIdHumano()));
            }
    }
    public int getMuertes() {
        return muertes.get();
    }

    public String getIdZombi() {
        return id;
    }
}
