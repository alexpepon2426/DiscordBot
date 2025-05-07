package org.example;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;

public class Main extends ListenerAdapter {
    private final Map<String, RunnableCommand> commands = new HashMap<>();
    private AlertaEpicardoGames alertaEpicardoGames;
    private static net.dv8tion.jda.api.JDA jda;

    public static void main(String[] args) throws LoginException {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        String token = dotenv.get("DISCORD_TOKEN");

        jda = JDABuilder.createDefault(token)
                .enableIntents(net.dv8tion.jda.api.requests.GatewayIntent.GUILD_MESSAGES,
                        net.dv8tion.jda.api.requests.GatewayIntent.DIRECT_MESSAGES)
                .addEventListeners(new Main())
                .build();

        try {
            jda.awaitReady();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Para Render (Web Service en plan gratuito): escuchar en un puerto falso
        try {
            int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
            ServerSocket socket = new ServerSocket(port);
            socket.accept(); // Bloquea indefinidamente
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Main() {
        commands.put("!ping", event -> event.getChannel().sendMessage("🏓 Pong!").queue());
        commands.put("!gay", event -> event.getChannel().sendMessage("Tu ere un gay manito, " + event.getAuthor().getName()).queue());

        commands.put("!juego", event -> {
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

    public void onReady() {
        TextChannel channel = jda.getTextChannelsByName("terapia-de-pareja", true).get(0);
        alertaEpicardoGames = new AlertaEpicardoGames(channel);
        alertaEpicardoGames.scheduleWeeklyReminder();
    }

    interface RunnableCommand {
        void execute(MessageReceivedEvent event);
    }
}
