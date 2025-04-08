package es.uah.matcomp.mp.teoria.gui.mvc.javafx.proyectoprogramacion;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

    public class Tunel {
        private final int id;
        private final Lock lock = new ReentrantLock(true); // Fair lock para prioridad
        private final Queue<Humano> colaEntrada = new LinkedList<>();
        private final Queue<Humano> colaSalida = new LinkedList<>();

        public Tunel(int id) {
            this.id = id;
        }

        public void salir(Humano humano) throws InterruptedException {
            lock.lock();
            try {
                colaSalida.add(humano);
                formarGrupo(colaSalida);
            } finally {
                lock.unlock();
            }
        }

        public void entrar(Humano humano) throws InterruptedException {
            lock.lock();
            try {
                colaEntrada.add(humano);
                formarGrupo(colaEntrada);
            } finally {
                lock.unlock();
            }
        }

        private void formarGrupo(Queue<Humano> cola) throws InterruptedException {
            synchronized (cola) {
                while (cola.size() < 3 && !cola.isEmpty()) {
                    cola.wait(100); // Espera con timeout para evitar bloqueos eternos
                }

                if (cola.size() >= 3) {
                    for (int i = 0; i < 3; i++) {
                        Humano h = cola.poll();
                        if (h != null && h.estaVivo()) {
                            Logger.log("Humano " + h.getIdHumano() + " atraviesa el túnel " + id);
                            Thread.sleep(1000); // Tiempo de cruce
                        }
                    }
                    cola.notifyAll(); // Notificar a otros humanos esperando
                }
            }
        }
    }