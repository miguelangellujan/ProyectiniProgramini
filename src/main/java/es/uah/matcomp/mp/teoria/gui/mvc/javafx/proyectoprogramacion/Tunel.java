package es.uah.matcomp.mp.teoria.gui.mvc.javafx.proyectoprogramacion;

import java.util.concurrent.locks.*;

public class Tunel {
    private final int id;
    private final Lock lock = new ReentrantLock(true);
    private final Condition grupoFormado = lock.newCondition();
    private int esperando = 0;       // humanos esperando en el túnel
    private int cruzado = 0;       // humanos del grupo actual que ya han cruzado
    private boolean grupoCompleto = false;

    public Tunel(int id) {
        this.id = id;
    }
    public void cruzar(Humano humano, boolean saliendo) throws InterruptedException {
        lock.lock();
        try {
            esperando++;
            Logger.log(STR."\{humano.getIdHumano()} esperando en túnel \{id} (\{saliendo ? "saliendo" : "entrando"})");
            // Esperar si hay grupo en curso
            while (grupoCompleto) {// Comienza siendo falso
                grupoFormado.await();
            }

            // Si hay 3 formamos grupo
            if (esperando == 3) {
                grupoCompleto = true;
                grupoFormado.signalAll(); // despertar a los otros 2
            } else {
                while (!grupoCompleto) { //Equivale a while true
                    grupoFormado.await();
                }
            }

            // Una vez en el grupo, cruzamos de uno en uno
            Logger.log(STR."\{humano.getIdHumano()} cruzando túnel \{id}");
            Thread.sleep(1000);
            cruzado++;
            if (cruzado == 3) {
                esperando = 0;
                cruzado = 0;
                grupoCompleto = false;
                grupoFormado.signalAll(); // permitir que otros formen grupo
            }
        } finally {
            lock.unlock();
        }
    }
}
