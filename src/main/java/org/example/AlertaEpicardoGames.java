package org.example;

import net.dv8tion.jda.api.entities.TextChannel;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;

public class AlertaEpicardoGames {

    private TextChannel channel;

    // Constructor que recibe el canal de Discord donde enviar el mensaje
    public AlertaEpicardoGames(TextChannel channel) {
        this.channel = channel;
    }

    public void scheduleWeeklyReminder() {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                sendEpicGamesReminder();
            }
        };

        // Definir el primer evento en el próximo jueves a las 17:00
        LocalDateTime now = LocalDateTime.now();
        int daysUntilThursday = (DayOfWeek.THURSDAY.getValue() - now.getDayOfWeek().getValue() + 7) % 7;
        LocalDateTime nextThursdayAt5pm = now.plusDays(daysUntilThursday).withHour(17).withMinute(0).withSecond(0).withNano(0);

        long delay = java.time.Duration.between(now, nextThursdayAt5pm).toMillis();
        long period = 7 * 24 * 60 * 60 * 1000L; // 7 días en milisegundos

        // Programar el recordatorio para que se repita cada semana
        timer.scheduleAtFixedRate(task, delay, period);
    }

    // Método para enviar el recordatorio de los juegos gratis al canal
    private void sendEpicGamesReminder() {
        if (channel != null) {
            channel.sendMessage("¡Recuerda que los juegos gratuitos de Epic Games ya están disponibles!").queue();
        }
    }

    // Método para obtener el mensaje de los juegos gratuitos (se podría conectar con una API o base de datos)
    public String getFreeGamesMessage() {
        // Este es un ejemplo, puedes integrarlo con la API de Epic Games si lo deseas
        return "¡No olvides que los juegos gratuitos de Epic Games están disponibles este jueves!";
    }
}
