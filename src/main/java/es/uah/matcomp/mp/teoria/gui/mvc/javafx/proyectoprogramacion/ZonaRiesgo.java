package es.uah.matcomp.mp.teoria.gui.mvc.javafx.proyectoprogramacion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ZonaRiesgo {
    private final int numAreas;
    private final Map<Integer, List<Humano>> humanosPorArea;
    private final Map<Integer, List<Zombi>> zombisPorArea;
    private final Map<String, Integer> zombisLetales;
    private final List<Lock> locksAreas;
    private final Random random;

    public ZonaRiesgo(int numAreas) {
        this.numAreas = numAreas;
        this.humanosPorArea = new ConcurrentHashMap<>();
        this.zombisPorArea = new ConcurrentHashMap<>();
        this.zombisLetales = new HashMap<>();
        this.locksAreas = new ArrayList<>();
        this.random = new Random();

        for (int i = 0; i < numAreas; i++) {
            humanosPorArea.put(i, new ArrayList<>());
            zombisPorArea.put(i, new ArrayList<>());
            locksAreas.add(new ReentrantLock());
        }
    }

    public void explorar(Humano humano) throws InterruptedException {
        int areaSeleccionada = random.nextInt(numAreas);
        Lock lockArea = locksAreas.get(areaSeleccionada);

        try {
            lockArea.lock();

            humanosPorArea.get(areaSeleccionada).add(humano);
            Logger.log("Humano " + humano.getIdHumano() + " está explorando en área " + areaSeleccionada);

            Thread.sleep(random.nextInt(2000) + 3000);

            if (humano.estaVivo()) {  // Ahora funciona correctamente
                Logger.log("Humano " + humano.getIdHumano() + " recolectó 2 piezas de comida");
                humano.getRefugio().depositarComida(2);
            }

        } finally {
            humanosPorArea.get(areaSeleccionada).remove(humano);
            lockArea.unlock();
        }
    }

    public Humano buscarHumano(int area) {
        List<Humano> humanosEnArea = humanosPorArea.get(area);
        if (!humanosEnArea.isEmpty()) {
            return humanosEnArea.get(random.nextInt(humanosEnArea.size()));
        }
        return null;
    }

    public void moverAZona(Zombi zombi, int areaDestino) {
        // Remover de área actual (si está en alguna)
        for (List<Zombi> zombis : zombisPorArea.values()) {
            zombis.remove(zombi);
        }

        // Agregar a nueva área
        zombisPorArea.get(areaDestino).add(zombi);
        Logger.log("Zombi " + zombi.getId() + " se movió al área " + areaDestino);
    }

    public void registrarMuerte(String idZombi) {
        zombisLetales.merge(idZombi, 1, Integer::sum);
    }

    public Map<String, Integer> getZombisLetales() {
        return zombisLetales;
    }

    public int getNumAreas() {
        return numAreas;
    }
}