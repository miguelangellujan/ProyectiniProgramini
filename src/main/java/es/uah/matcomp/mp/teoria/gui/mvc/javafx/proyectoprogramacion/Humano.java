package es.uah.matcomp.mp.teoria.gui.mvc.javafx.proyectoprogramacion;

import java.util.List;
import java.util.Random;

public class Humano extends Thread {
    private final String id;
    private final Refugio refugio;
    private final ZonaRiesgo zonaRiesgo;
    private final List<Tunel> tuneles;
    private boolean marcadoPorZombi = false;
    private boolean vivo = true;  // Nuevo campo para estado
    private Random random = new Random();

    public Humano(String id, Refugio refugio, ZonaRiesgo zonaRiesgo, List<Tunel> tuneles) {
        this.id = id;
        this.refugio = refugio;
        this.zonaRiesgo = zonaRiesgo;
        this.tuneles = tuneles;
    }

    // Método que faltaba
    public boolean estaVivo() {
        return vivo;
    }

    // Método para cuando el humano muere
    public void morir() {
        this.vivo = false;
    }

    // ... resto de los métodos existentes
    public String getIdHumano() {
        return id;
    }

    public boolean estaMarcado() {
        return marcadoPorZombi;
    }

    public void setMarcado(boolean marcado) {
        this.marcadoPorZombi = marcado;
    }

    public boolean intentarDefenderse() {
        return random.nextInt(3) < 2; // 2/3 de probabilidad
    }

    public Refugio getRefugio() {
        return refugio;
    }

    @Override
    public void run() {
        Logger.log("Humano " + id + " INICIA su ciclo de vida");
        while (vivo) {
            try {
                Logger.log("Humano " + id + " en Zona Común");
                refugio.zonaComun(this);

                Logger.log("Humano " + id + " preparándose para salir");
                refugio.prepararParaSalida(this);

                // ... resto del código
            } catch (InterruptedException e) {
                Logger.log("Humano " + id + " INTERRUMPIDO");
                Thread.currentThread().interrupt();
                break;
            }
        }
        Logger.log("Humano " + id + " TERMINA su ciclo");
    }
}