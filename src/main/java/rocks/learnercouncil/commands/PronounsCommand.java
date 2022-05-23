package rocks.learnercouncil.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;
import rocks.learnercouncil.Cameron;


import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class PronounsCommand extends ListenerAdapter {

    private static final String PREFIX = "!";
    private static final List<Role> pronounRoles = new ArrayList<>();

    public static void initializeRoles() {
        pronounRoles.clear();
        Guild guild = Cameron.getJDA().getGuildById(Cameron.GUILD_ID);
        if(guild != null) {
           Cameron.getExistingChannel("pronouns").getIterableHistory().forEach(m -> {
               if(guild.getRolesByName(PREFIX + m.getContentStripped(), true).isEmpty()) {
                    addPronounRole(guild, PREFIX + m.getContentStripped());
               }
           });
            guild.getRoles().stream().filter(r -> r.getName().startsWith(PREFIX)).forEach(pronounRoles::add);
        }
    }

    private static void addPronounRole(Guild guild, String name) {
        guild.createRole().setName(name).queue();
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if(event.getName().equals("pronouns")) {
            EmbedBuilder embed = new EmbedBuilder()
                    .setColor(Color.GREEN)
                    .setAuthor("Set your pronouns!")
                    .setDescription("Choose from the list below.\n(If your pronouns are not on this list please @The Council with your \npronouns and someone will add them to the list)");



            event.replyEmbeds(embed.build()).addActionRows(/*buttonRows*/).queue();
        }
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        //TODO pronoun command button logic
    }
}
