package es.uah.matcomp.mp.teoria.gui.mvc.javafx.proyectoprogramacion;

import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Humano extends Thread {
    private final String id;
    private final Refugio refugio;
    private final ZonaRiesgo zonaRiesgo;
    private final List<Tunel> tuneles;
    private final AtomicBoolean marcado = new AtomicBoolean(false);// comienzan sin marcar
    private final AtomicBoolean vivo = new AtomicBoolean(true); //comienzan vivos
    private final Random random=new Random();

    public Humano(String id, Refugio refugio, ZonaRiesgo zonaRiesgo, List<Tunel> tuneles) {
        this.id = id;
        this.refugio = refugio;
        this.zonaRiesgo = zonaRiesgo;
        this.tuneles = tuneles;
    }

    @Override
    public void run() {
        refugio.agregarHumano(this);
        while (!Thread.currentThread().isInterrupted() && vivo.get()) {
            try {
                refugio.zonaComun(this);
                Thread.sleep(1000+random.nextInt(1001));
                Tunel tunel = tuneles.get(random.nextInt(tuneles.size()));

                tunel.cruzar(this, true);
                int resultado = zonaRiesgo.explorar(this);
                int comida = resultado;
                if (!vivo.get()) {break;} // <--- Salir antes de seguir si murió
                if (comida > 0) {
                    refugio.depositarComida(comida);
                }

                if (vivo.get()) {
                    tunel.cruzar(this, false);
                    refugio.descansar(this);
                    refugio.comer(this);
                    if (marcado.get()) {
                        refugio.recuperarse(this);
                        marcado.set(false);
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        refugio.removerHumano(this);
    }

    public String getIdHumano() { return id; }
    public boolean estaVivo() { return vivo.get(); }
    public void morir() {
        // Marca al humano como muerto (no se mueve ni interactúa más)
        vivo.set(false);

        // Eliminar al humano del refugio (y otras estructuras si es necesario)
        refugio.removerHumano(this);

        // Interrumpir el hilo del humano para detener cualquier acción pendiente
        Thread.currentThread().interrupt();
    }
    public boolean estaMarcado() { return marcado.get(); }
    public void setMarcado(boolean valor) { marcado.set(valor); }
    public boolean intentarDefenderse() { return ThreadLocalRandom.current().nextInt(3) < 2; }
    public Refugio getRefugio() { return refugio; }
}