package rocks.learnercouncil.commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import rocks.learnercouncil.Cameron;

import java.util.ArrayList;
import java.util.List;

public class PronounsCommand extends ListenerAdapter {

    private static final String PREFIX = "!";
    private static final List<Role> pronounRoles = new ArrayList<>();
    public PronounsCommand() {

    }

    public static void initializeRoles() {
        Guild guild = Cameron.getJDA().getGuildById(Cameron.GUILD_ID);
        if(guild != null)
            guild.getRoles().stream().filter(r -> r.getName().startsWith(PREFIX)).forEach(pronounRoles::add);
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if(event.getName().equals("pronouns")) {
            //TODO Pronoun command logic
        }
    }

}
