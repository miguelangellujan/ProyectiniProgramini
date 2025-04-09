package es.uah.matcomp.mp.teoria.gui.mvc.javafx.proyectoprogramacion;

import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.*;
import java.util.*;

public class ZonaRiesgo {
    private final int numAreas;
    private final ConcurrentHashMap<Integer, CopyOnWriteArrayList<Humano>> humanosPorArea;
    private final ConcurrentHashMap<Integer, CopyOnWriteArrayList<Zombi>> zombisPorArea;
    private final ConcurrentHashMap<String, AtomicInteger> zombisLetales;
    private final Lock[] locksAreas;
    private final ExecutorService executorExploracion = Executors.newFixedThreadPool(10);

    public ZonaRiesgo(int numAreas) {
        this.numAreas = numAreas;
        this.humanosPorArea = new ConcurrentHashMap<>();
        this.zombisPorArea = new ConcurrentHashMap<>();
        this.zombisLetales = new ConcurrentHashMap<>();
        this.locksAreas = new ReentrantLock[numAreas];

        for (int i = 0; i < numAreas; i++) {
            humanosPorArea.put(i, new CopyOnWriteArrayList<>());
            zombisPorArea.put(i, new CopyOnWriteArrayList<>());
            locksAreas[i] = new ReentrantLock();
        }
    }

    public Future<Integer> explorarAsCallable(Humano humano) {
        return executorExploracion.submit(() -> {
            int area = ThreadLocalRandom.current().nextInt(numAreas);
            Lock lockArea = locksAreas[area];

            lockArea.lock();
            try {
                humanosPorArea.get(area).add(humano);
                Logger.log(humano.getIdHumano() + " explorando en área " + area);
                Thread.sleep(ThreadLocalRandom.current().nextInt(3000, 5001));

                if (humano.estaVivo()) {
                    Logger.log(humano.getIdHumano() + " encontró comida (2 unidades)");
                    return 2; // Comida encontrada
                }
                return 0;
            } finally {
                humanosPorArea.get(area).remove(humano);
                lockArea.unlock();
            }
        });
    }

    public Humano buscarHumano(int area) {
        CopyOnWriteArrayList<Humano> humanos = humanosPorArea.get(area);
        return humanos.isEmpty() ? null : humanos.get(ThreadLocalRandom.current().nextInt(humanos.size()));
    }

    public void moverAZona(Zombi zombi, int area) {
        zombisPorArea.get(area).add(zombi);
        Logger.log(zombi.getIdZombi() + " se mueve a la zona " + area);
    }

    public void registrarMuerte(String idZombi) {
        zombisLetales.computeIfAbsent(idZombi, k -> new AtomicInteger()).incrementAndGet();
    }

    public ConcurrentHashMap<String, AtomicInteger> getZombisLetales() {
        return zombisLetales;
    }

    public int getNumAreas() {
        return numAreas;
    }

    public void shutdown() {
        executorExploracion.shutdown();
        try {
            if (!executorExploracion.awaitTermination(10, TimeUnit.SECONDS)) {
                executorExploracion.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorExploracion.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
