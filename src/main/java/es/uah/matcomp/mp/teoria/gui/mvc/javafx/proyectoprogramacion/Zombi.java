package es.uah.matcomp.mp.teoria.gui.mvc.javafx.proyectoprogramacion;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ExecutorService;

public class Zombi extends Thread {
    private final String id;
    private final ZonaRiesgo zonaRiesgo;
    private final AtomicInteger muertes = new AtomicInteger(0);
    private final ExecutorService executorZombis;

    public Zombi(String id, ZonaRiesgo zonaRiesgo, ExecutorService executorZombis) {
        this.id = id;
        this.zonaRiesgo = zonaRiesgo;
        this.executorZombis = executorZombis;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                int area = ThreadLocalRandom.current().nextInt(zonaRiesgo.getNumAreas());
                zonaRiesgo.moverAZona(this, area);

                Humano humano = zonaRiesgo.buscarHumano(area);
                if (humano != null) {
                    atacar(humano);
                } else {
                    Thread.sleep(ThreadLocalRandom.current().nextInt(2000, 3001));
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void atacar(Humano humano) throws InterruptedException {
        Logger.log(id + " atacando a " + humano.getIdHumano());
        Thread.sleep(ThreadLocalRandom.current().nextInt(500, 1501));

        if (!humano.intentarDefenderse()) {
            humano.morir();
            muertes.incrementAndGet();
            zonaRiesgo.registrarMuerte(id);
            Logger.log(humano.getIdHumano() + " muerto. Renace como zombi");

            String nuevoId = "Z" + humano.getIdHumano().substring(1);
            executorZombis.execute(new Zombi(nuevoId, zonaRiesgo, executorZombis));
        } else {
            humano.setMarcado(true);
            Logger.log(humano.getIdHumano() + " se defendió pero quedó marcado");
        }
    }

    public int getMuertes() {
        return muertes.get();
    }

    public String getIdZombi() {
        return id;
    }
}
