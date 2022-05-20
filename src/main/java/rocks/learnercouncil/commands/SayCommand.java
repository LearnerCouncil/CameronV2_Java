package rocks.learnercouncil.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Queue;
import java.util.concurrent.TimeUnit;

public class SayCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if(event.getName().equals("say")) {
            if(event.getMember() == null) return;
            if(event.getMember().hasPermission(Permission.VIEW_AUDIT_LOGS)) {
                if(event.getOption("message") == null) return;
                event.reply("Message Sent!").setEphemeral(true).queue();
                //noinspection ConstantConditions
                event.getChannel().sendMessage(event.getOption("message").getAsString()).queue();
            } else {
                event.reply("You do not have permission to perform this command.").setEphemeral(true).queue();
            }
        }
    }
}
