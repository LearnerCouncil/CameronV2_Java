package rocks.learnercouncil.cameron.commands.request;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class RequestCommand extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if(!event.getChannel().getName().equals("rr-1")) {
            event.reply("Can't do that here").setEphemeral(true).queue();
            return;
        }
        if(Request.exists()) {
            event.reply("A request is already in progress, please be patient.").queue();
            return;
        }
        event.reply("Processing Request...").queue(m -> m.deleteOriginal().queueAfter(1, TimeUnit.MILLISECONDS));
        new Request(event.getUser().getIdLong(), event.getChannel());
    }
}
