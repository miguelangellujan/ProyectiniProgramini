package es.uah.matcomp.mp.teoria.gui.mvc.javafx.proyectoprogramacion;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

    public class Logger {
        private static final String ARCHIVO_LOG = "apocalipsis.log";
        private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        private static final Object lock = new Object();

        public static void log(String mensaje) {
            synchronized (lock) {
                try (PrintWriter out = new PrintWriter(new FileWriter(ARCHIVO_LOG, true))) {
                    String logEntry = "[" + dateFormat.format(new Date()) + "] " + mensaje;
                    out.println(logEntry);
                    System.out.println(logEntry); // También imprimir en consola
                } catch (IOException e) {
                    System.err.println("Error escribiendo en el log: " + e.getMessage());
                }
            }
        }
    }