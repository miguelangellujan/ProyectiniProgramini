package es.uah.matcomp.mp.teoria.gui.mvc.javafx.proyectoprogramacion;

import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.*;

public class ZonaRiesgo {
    private final int numAreas;
    private final ConcurrentHashMap<Integer, BlockingQueue<Humano>> humanosPorArea;
    private final ConcurrentHashMap<Integer, BlockingQueue<Zombi>> zombisPorArea;
    private final ConcurrentHashMap<String, AtomicInteger> zombisLetales;
    private final Object[] locksAreas;

    public ZonaRiesgo(int numAreas) {
        this.numAreas = numAreas;
        this.humanosPorArea = new ConcurrentHashMap<>();
        this.zombisPorArea = new ConcurrentHashMap<>();
        this.zombisLetales = new ConcurrentHashMap<>();
        this.locksAreas = new Object[numAreas];

        for (int i = 0; i < numAreas; i++) {
            humanosPorArea.put(i, new LinkedBlockingQueue<>());
            zombisPorArea.put(i, new LinkedBlockingQueue<>());
            locksAreas[i] = new Object(); // Usando objetos como monitores para las áreas
        }
    }

    public int explorar(Humano humano) {
        int area = ThreadLocalRandom.current().nextInt(numAreas);

        synchronized (locksAreas[area]) {
            try {
                humanosPorArea.get(area).put(humano);
                Logger.log(STR."\{humano.getIdHumano()} explorando en área \{area}");
                Thread.sleep(ThreadLocalRandom.current().nextInt(3000, 5001));

                if (humano.estaVivo()) {
                    Logger.log(STR."\{humano.getIdHumano()} encontró comida (2 unidades)");
                    return 2; // Comida encontrada
                }
                return 0;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return 0;
            } finally {
                humanosPorArea.get(area).remove(humano);
            }
        }
    }

    public Humano buscarHumano(int area) {
        BlockingQueue<Humano> humanos = humanosPorArea.get(area);
        return humanos.isEmpty() ? null : humanos.peek(); // Obtener humano sin quitarlo
    }

    public void moverAZona(Zombi zombi, int area) {
        zombisPorArea.get(area).add(zombi);
        Logger.log(STR."\{zombi.getIdZombi()} se mueve a la zona \{area}");
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
}
