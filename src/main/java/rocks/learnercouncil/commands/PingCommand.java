package rocks.learnercouncil.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import rocks.learnercouncil.Cameron;

import java.time.Instant;

public class PingCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if(event.getName().equals("ping")) {
            long now = System.currentTimeMillis();
            event.reply(":ping_pong: Pinging...").setEphemeral(true).queue(m -> m.editOriginal(":ping_pong: Pong! Latency is " + (System.currentTimeMillis() - now) + "ms").queue());
        }
    }
}
