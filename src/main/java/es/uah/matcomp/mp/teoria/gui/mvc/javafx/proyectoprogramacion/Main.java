package es.uah.matcomp.mp.teoria.gui.mvc.javafx.proyectoprogramacion;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) {
        Refugio refugio = new Refugio();
        ZonaRiesgo zonaRiesgo = new ZonaRiesgo(4);
        List<Tunel> tuneles = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            tuneles.add(new Tunel(i));
        }

        ExecutorService executorHumanos = Executors.newCachedThreadPool();
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        ExecutorService executorZombis = Executors.newCachedThreadPool();

        // Paciente cero
        executorZombis.execute(new Zombi("Z0000", zonaRiesgo, executorZombis));

        // Crear humanos periódicamente
        scheduler.scheduleAtFixedRate(() -> {
            if (!executorHumanos.isShutdown()) {
                String id = "H" + String.format("%04d", ThreadLocalRandom.current().nextInt(10000));
                executorHumanos.execute(new Humano(id, refugio, zonaRiesgo, tuneles));
            }
        }, 0, ThreadLocalRandom.current().nextInt(500, 2001), TimeUnit.MILLISECONDS);

        scheduler.schedule(() -> {
            scheduler.shutdown();

            // 1. Detener nuevos humanos
            executorHumanos.shutdown();
            try {
                if (!executorHumanos.awaitTermination(30, TimeUnit.SECONDS)) {
                    executorHumanos.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorHumanos.shutdownNow();
                Thread.currentThread().interrupt();
            }

            // 2. Ahora sí: detener exploración porque ya no hay humanos usando el pool
            zonaRiesgo.shutdown();

            // 3. Detener zombis
            executorZombis.shutdown();
            try {
                if (!executorZombis.awaitTermination(30, TimeUnit.SECONDS)) {
                    executorZombis.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorZombis.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }, 10000, TimeUnit.MILLISECONDS);
    }
}