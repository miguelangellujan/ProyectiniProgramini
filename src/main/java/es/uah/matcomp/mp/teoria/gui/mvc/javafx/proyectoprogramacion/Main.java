package es.uah.matcomp.mp.teoria.gui.mvc.javafx.proyectoprogramacion;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Main {
    public static void main(String[] args) {
        Refugio refugio = new Refugio();
        ZonaRiesgo zonaRiesgo = new ZonaRiesgo(4);
        List<Tunel> tuneles = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            tuneles.add(new Tunel(i));
        }

        // Paciente cero: Creando el primer zombi
        new Zombi("Z0000", zonaRiesgo).start();

        // Crear humanos periódicamente
        Thread humanoCreator = new Thread(() -> {
            while (true) {
                try {
                    if (ThreadLocalRandom.current().nextInt(500, 2001) > 1500) {
                        String id = String.format("H%04d", ThreadLocalRandom.current().nextInt(10000));
                        new Humano(id, refugio, zonaRiesgo, tuneles).start();
                    }
                    Thread.sleep(1000); // Periodo de espera para la creación de humanos
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        humanoCreator.start();
    }
}
