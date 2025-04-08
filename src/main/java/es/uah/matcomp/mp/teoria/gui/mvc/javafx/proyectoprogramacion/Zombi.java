package es.uah.matcomp.mp.teoria.gui.mvc.javafx.proyectoprogramacion;

import java.util.Random;

public class Zombi extends Thread {
    private final String id;
    private final ZonaRiesgo zonaRiesgo;
    private int muertes = 0;
    private Random random = new Random();

    public Zombi(String id, ZonaRiesgo zonaRiesgo) {
        this.id = id;
        this.zonaRiesgo = zonaRiesgo;
    }

    @Override
    public void run() {
        while (true) {
            try {
                int areaActual = random.nextInt(zonaRiesgo.getNumAreas());
                zonaRiesgo.moverAZona(this, areaActual);

                Humano humanoAtacado = zonaRiesgo.buscarHumano(areaActual);
                if (humanoAtacado != null) {
                    atacar(humanoAtacado);
                } else {
                    Thread.sleep(random.nextInt(1000) + 2000); // Espera si no hay humanos
                }
            } catch (InterruptedException e) {
                Logger.log("Zombi " + id + " interrumpido");
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void atacar(Humano humano) throws InterruptedException {
        Logger.log("El zombi " + id + " ataca al humano " + humano.getIdHumano());

        int duracionAtaque = random.nextInt(1000) + 500;
        Thread.sleep(duracionAtaque);

        if (!humano.intentarDefenderse()) {
            humano.morir();
            muertes++;
            Logger.log("El humano " + humano.getIdHumano() + " ha muerto. Renace como zombi Z" +
                    humano.getIdHumano().substring(1));
            // Convertir humano en zombi
            new Zombi("Z" + humano.getIdHumano().substring(1), zonaRiesgo).start();
        } else {
            humano.setMarcado(true);
            Logger.log("El humano " + humano.getIdHumano() + " se ha defendido pero queda marcado");
        }
    }

    public int getMuertes() { return muertes; }
}
