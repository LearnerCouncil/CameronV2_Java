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

public class BanCommand extends ListenerAdapter {

    private String reason = "No Reason Supplied";
    private User user;
    private int history = 0;

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if(event.getName().equals("ban")) {
            if(!event.isFromGuild())
                event.reply("Has to be run from inside a guild").queue();
            Guild guild = event.getGuild();

            user = Objects.requireNonNull(event.getOption("user")).getAsUser();
            if(user.isBot()) {
                event.reply("I can't ban bots, you'll have to do that manually").queue();
            }

            if(event.getOption("reason") != null)
                reason = Objects.requireNonNull(event.getOption("reason")).getAsString();
            if(event.getOption("delete_history_days") != null)
                history = Objects.requireNonNull(event.getOption("delete_history_days")).getAsInt();

            if(!Objects.requireNonNull(event.getMember()).hasPermission(Permission.BAN_MEMBERS))
                event.reply("You do not have permission to ban members.").setEphemeral(true).queue();
            else {
                assert guild != null;
                event.reply("Are you sure you want to ban " + user.getAsMention() + " (" + user.getAsTag() + ") from the server?").setEphemeral(true).addActionRow(
                        Button.success("bancmd_yes", Cameron.getExistingEmoji("greentick", guild)),
                        Button.danger("bancmd_no", Cameron.getExistingEmoji("redtick", guild))
                ).queue();
            }
        }
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        String componentId = event.getComponentId();
        if (componentId.equals("bancmd_yes")) {
            if(event.getGuild() == null)
                event.editMessage("Has to be run from inside in a server").setActionRows().queue();
            event.editMessage("Banned.").setActionRows().queue();
            event.getJDA().openPrivateChannelById(user.getId()).queue(p -> p.sendMessageEmbeds(new EmbedBuilder()
                    .setColor(Color.RED)
                    .setAuthor("You have been banned from " + event.getGuild().getName())
                    .addField("Reason", reason, false)
                    .build()
            ).queue(k -> event.getGuild().ban(user, history, reason).queue()));
            Cameron.getExistingChannel("ban-log").sendMessageEmbeds(new EmbedBuilder()
                    .setColor(Color.ORANGE)
                    .setThumbnail(user.getEffectiveAvatarUrl())
                    .setFooter(user.getAsTag())
                    .setTimestamp(Cameron.CURRENT_DATE)
                    .setDescription("**> Banned Member:**\n" + user.getAsMention() + " (" + user.getId() + ") " +
                            "**> Banned By:**\n" + event.getUser().getAsMention() + " (" + event.getUser().getId() + ") " +
                            "**> Reason:**\n" + (reason)
                    )
                    .build()
            ).queue();
        } else if (componentId.equals("bancmd_no"))
            event.editMessage("Cancelled.").setActionRows().queue();
    }
}
