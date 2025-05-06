package org.example;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.util.HashMap;
import java.util.Map;

public class Main extends ListenerAdapter {
    private final Map<String, RunnableCommand> commands = new HashMap<>();
    private AlertaEpicardoGames alertaEpicardoGames;
    private static net.dv8tion.jda.api.JDA jda;

    public static void main(String[] args) throws LoginException {
        // Inicializar JDA con el token del bot

        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        String token = dotenv.get("DISCORD_TOKEN");

        jda = JDABuilder.createDefault(token)
                .enableIntents(net.dv8tion.jda.api.requests.GatewayIntent.GUILD_MESSAGES, net.dv8tion.jda.api.requests.GatewayIntent.DIRECT_MESSAGES) // Reemplazar por los intentos correctos
                .addEventListeners(new Main())
                .build();

        // Esperar hasta que JDA esté listo
        try {
            jda.awaitReady(); // Espera a que el bot esté completamente listo
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Main() {
        // Comandos que el bot responderá
        commands.put("!ping", (event) -> event.getChannel().sendMessage("🏓 Pong!").queue());
        commands.put("!gay", (event) -> event.getChannel().sendMessage("Tu ere un gay manito, " + event.getAuthor().getName()).queue());

        // Comando para obtener los juegos gratuitos
        commands.put("!juego", (event) -> {
            if (alertaEpicardoGames == null) {
                event.getChannel().sendMessage("⚠️ Aún no estoy listo para mostrar los juegos gratis.").queue();
                return;
            }
            String freeGamesMessage = alertaEpicardoGames.getFreeGamesMessage();
            event.getChannel().sendMessage(freeGamesMessage).queue();
        });

    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        String msg = event.getMessage().getContentRaw().trim();
        RunnableCommand command = commands.get(msg);

        if (command != null) {
            command.execute(event);
        }
    }

    // Cambiar el nombre de onReady() por onShardReady()
    public void onReady() {
        // Obtener el canal de texto por nombre cuando el bot esté listo
        TextChannel channel = jda.getTextChannelsByName("terapia-de-pareja", true).get(0); // Reemplaza "nombre_del_canal" por el canal real

        // Crear la instancia de AlertaEpicardoGames con el canal de Discord
        alertaEpicardoGames = new AlertaEpicardoGames(channel);

        // Iniciar la tarea programada para el recordatorio semanal
        alertaEpicardoGames.scheduleWeeklyReminder();
    }

    interface RunnableCommand {
        void execute(MessageReceivedEvent event);
    }
}
