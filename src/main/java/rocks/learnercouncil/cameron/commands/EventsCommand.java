package rocks.learnercouncil.cameron.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import org.jetbrains.annotations.NotNull;
import rocks.learnercouncil.cameron.Cameron;
import rocks.learnercouncil.cameron.DynamicRoleList;

import java.awt.*;

public class EventsCommand extends ListenerAdapter {

    private static final MessageEmbed EVENT_EMBED = new EmbedBuilder()
            .setColor(Color.GREEN)
            .setAuthor("Set your event pings! (Select all that apply.)")
            .setDescription("Choose from the list below. Selecting an event means you will be pinged once it starts.")
            .build();
    public static DynamicRoleList roleList;
    private static ActionRow[] buttonRows;

    public static void initialize(@NotNull Guild guild) {
        roleList = new DynamicRoleList(guild, "-------------- Events --------------", Cameron.getExistingChannel("event-pings"));
    }


    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if(!event.getName().equals("events")) return;
        if(!event.isFromGuild()) {
            event.reply("This command must be run from inside a server").queue();
            return;
        }
        event.deferReply().setEphemeral(true).queue();
        buttonRows = roleList.getButtons(event.getMember(), "ev");
        event.getHook().sendMessageEmbeds(EVENT_EMBED).addActionRows(buttonRows).setEphemeral(true).queue();
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if(!event.getComponentId().startsWith("ev_rlb_")) return;
        Guild guild = event.getGuild();
        if(guild == null) return;
        if(event.getMember() == null) {
            Cameron.logger.error(event.getUser() + " is not in a guild!");
            return;
        }

        roleList.updateButtons(buttonRows, event);
        event.deferEdit().setActionRows(buttonRows).queue();
    }
}
