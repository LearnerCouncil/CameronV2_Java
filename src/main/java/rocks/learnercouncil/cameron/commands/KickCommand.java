package rocks.learnercouncil.cameron.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;
import rocks.learnercouncil.cameron.Cameron;

import java.awt.*;
import java.time.Instant;
import java.util.Objects;

public class KickCommand extends ListenerAdapter {

    private String reason = "No reason supplied.";
    private User user;

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if(!event.getName().equals("kick")) return;
        if(!event.isFromGuild()) {
            event.reply("Has to be run from inside a guild").queue();
            return;
        }
        Guild guild = event.getGuild();

        user = Objects.requireNonNull(event.getOption("user")).getAsUser();
        if(user.isBot()) {
            event.reply("I can't kick bots, you have to do that manually").queue();
            return;
        }

        if(event.getOption("reason") != null)
            reason = event.getOption("reason").getAsString();
        if(!Objects.requireNonNull(event.getMember()).hasPermission(Permission.KICK_MEMBERS)) {
            event.reply("You do not have permission to kick members.").setEphemeral(true).queue();
            return;
        }

        event.reply("Are you sure you want to kick " + user.getAsMention() + " (" + user.getAsTag() + ") from the server?").setEphemeral(true).addActionRow(
                Button.success("kickcmd_yes", Cameron.getExistingEmoji("greentick")),
                Button.danger("kickcmd_no", Cameron.getExistingEmoji("redtick"))
        ).queue();
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        String componentId = event.getComponentId();
        if(componentId.equals("kickcmd_no")) {
            event.editMessage("Cancelled.").setActionRows().queue();
            return;
        }
        if(!componentId.equals("kickcmd_yes")) return;

        if(!event.isFromGuild())
            event.editMessage("Has to be run from inside in a server").setActionRows().queue();
        event.editMessage("Kicked.").setActionRows().queue();
        event.getJDA().openPrivateChannelById(user.getId()).queue(p -> p.sendMessageEmbeds(new EmbedBuilder()
                .setColor(Color.RED)
                .setAuthor("You have been kicked from " + event.getGuild().getName())
                .addField("Reason", reason, false)
                .build()
        ).queue(k -> event.getGuild().kick(user).queue()));
        Cameron.getExistingChannel("kick-log").sendMessageEmbeds(new EmbedBuilder()
                .setColor(Color.ORANGE)
                .setThumbnail(user.getEffectiveAvatarUrl())
                .setFooter(user.getAsTag())
                .setTimestamp(Instant.now())
                .setDescription("**> Kicked Member:**\n" + user.getAsMention() + " (" + user.getId() + ") " +
                                "\n**> Kicked By:**\n" + event.getUser().getAsMention() + " (" + event.getUser().getId() + ") " +
                                "\n**> Reason:**\n" + reason
                        )
                .build()
        ).queue();
    }
}
