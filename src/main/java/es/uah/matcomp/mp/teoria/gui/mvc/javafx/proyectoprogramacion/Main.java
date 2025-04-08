package es.uah.matcomp.mp.teoria.gui.mvc.javafx.proyectoprogramacion;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
        public static void main(String[] args) {
            Refugio refugio = new Refugio();
            ZonaRiesgo zonaRiesgo = new ZonaRiesgo(4);
            List<Tunel> tuneles = new ArrayList<>();

            for (int i = 0; i < 4; i++) {
                tuneles.add(new Tunel(i));
            }

            // Crear el paciente cero
            new Zombi("Z0000", zonaRiesgo).start();

            // Crear humanos en un executor con más hilos
            ExecutorService executor = Executors.newFixedThreadPool(100); // Aumenta el pool
            Random random = new Random();

            for (int i = 1; i <= 10000; i++) {
                try {
                    Thread.sleep(random.nextInt(1500) + 500);
                    String id = String.format("H%04d", i);
                    executor.execute(new Humano(id, refugio, zonaRiesgo, tuneles));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }