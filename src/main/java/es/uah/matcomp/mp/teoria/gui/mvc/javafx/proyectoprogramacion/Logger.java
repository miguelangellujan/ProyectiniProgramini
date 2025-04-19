package es.uah.matcomp.mp.teoria.gui.mvc.javafx.proyectoprogramacion;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

public class Logger {
    private static final AtomicReference<PrintWriter> writer = new AtomicReference<>();
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    static {
        try {
            writer.set(new PrintWriter(new FileWriter("apocalipsis.log", true)));
        } catch (IOException e) {
            System.err.println(STR."Error inicializando logger: \{e.getMessage()}");
        }
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            PrintWriter w = writer.get();
            if (w != null) w.close();
        }));
    }

    public static synchronized void log(String mensaje) {
        String logEntry = STR."[\{dateFormat.format(new Date())}] \{mensaje}";
        System.out.println(logEntry);
        PrintWriter w = writer.get();
        if (w != null) {
            w.println(logEntry);
            w.flush();
        }
    }
}