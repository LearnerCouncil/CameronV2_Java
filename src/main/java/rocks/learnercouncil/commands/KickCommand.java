package rocks.learnercouncil.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;
import rocks.learnercouncil.Cameron;

import java.awt.*;
import java.util.Objects;

public class KickCommand extends ListenerAdapter {

    private String reason;
    private User user;


    @SuppressWarnings("ConstantConditions")
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if(event.getName().equals("kick")) {
            if(!event.isFromGuild())
                event.reply("Has to be run from inside a guild").queue();
            Guild guild = event.getGuild();
            user = Objects.requireNonNull(event.getOption("user")).getAsUser();
            if(user.isBot())
                event.reply("I can't kick bots, you have to do that manually").queue();
            if(event.getOption("reason") != null)
                reason = Objects.requireNonNull(event.getOption("reason")).getAsString();
            if(!event.getMember().hasPermission(Permission.KICK_MEMBERS))
                event.reply("You do not have permission to kick members.").setEphemeral(true).queue();
            else {
                event.reply("Are you sure you want to kick " + user.getAsMention() + " (" + user.getAsTag() + ") from the server?").setEphemeral(true).addActionRow(
                        Button.primary("kickcmd_yes", Cameron.getExistingEmoji("greentick", guild)),
                        Button.danger("kickcmd_no", Cameron.getExistingEmoji("redtick", guild))
                ).queue();
            }
        }
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        String componentId = event.getComponentId();
        if ("kickcmd_yes".equals(componentId)) {
            if(event.getGuild() == null)
                event.editMessage("Has to be run from inside in a server").setActionRows().queue();
            event.editMessage("Kicked.").setActionRows().queue();
            event.getJDA().openPrivateChannelById(user.getId()).queue(p -> p.sendMessageEmbeds(new EmbedBuilder()
                    .setColor(Color.RED)
                    .setAuthor("You have been kicked from " + event.getGuild().getName())
                    .addField("Reason", reason != null ? reason : "No reason supplied.", false)
                    .build()
            ).queue(k -> event.getGuild().kick(user).queue()));
            Cameron.getExistingChannel("kick-log").sendMessageEmbeds(new EmbedBuilder()
                    .setColor(Color.ORANGE)
                    .setThumbnail(user.getEffectiveAvatarUrl())
                    .setFooter(user.getAsTag())
                    .setTimestamp(Cameron.CURRENT_DATE)
                    .setDescription("**> Kicked Member:**\n" + user.getAsMention() + " (" + user.getId() + ") " +
                                    "\n**> Kicked By:**\n" + event.getUser().getAsMention() + " (" + event.getUser().getId() + ") " +
                                    "\n**> Reason:**\n" + (reason != null ? reason : "No Reason Supplied")
                            )
                    .build()
            ).queue();
        } else if ("kickcmd_no".equals(componentId))
            event.editMessage("Cancelled.").setActionRows().queue();
    }
}
