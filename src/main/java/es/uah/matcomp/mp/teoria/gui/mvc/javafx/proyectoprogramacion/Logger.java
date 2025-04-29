package es.uah.matcomp.mp.teoria.gui.mvc.javafx.proyectoprogramacion;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

public class Logger {
    private static PrintWriter writer;

    static {
        try {
            writer = new PrintWriter(new FileWriter("apocalipsis.log", true));
        } catch (IOException e) {
            System.out.println("No se pudo abrir el archivo de log.");
        }
        Runtime.getRuntime().addShutdownHook(new CerrarLogger());
    }

    public static void log(String mensaje) {
        String texto = STR."[\{new Date()}] \{mensaje}";
        System.out.println(texto);
        if (writer != null) {
            writer.println(texto);
            writer.flush();
        }
    }
    private static class CerrarLogger extends Thread {
        public void run() {
            if (writer != null) {
                writer.close();
            }
        }
    }
}
