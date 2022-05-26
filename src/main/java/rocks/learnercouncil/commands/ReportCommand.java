package rocks.learnercouncil.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import rocks.learnercouncil.Cameron;

import java.awt.*;
import java.util.Objects;

public class ReportCommand extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if(event.getName().equals("report")) {
            if (!event.isFromGuild())
                event.reply("Must be sent from a server").setEphemeral(true).queue();
            User user = Objects.requireNonNull(event.getOption("user")).getAsUser();
            String reason = Objects.requireNonNull(event.getOption("reason")).getAsString();
            event.reply("Report sent, A council member will review it shortly, and take appropriate action.").setEphemeral(true).queue();
            Objects.requireNonNull(event.getGuild()).retrieveMember(user).queue(member -> Cameron.getExistingChannel("reporting-log").sendMessageEmbeds(new EmbedBuilder()
                    .setColor(Color.YELLOW)
                    .setTimestamp(Cameron.CURRENT_DATE)
                    .setFooter(Objects.requireNonNull(event.getGuild()).getName(), event.getGuild().getIconUrl())
                    .setAuthor(member.getEffectiveName() + " has been reported", null, user.getEffectiveAvatarUrl())
                    .setDescription("**> Member:** " + user.getAsMention() + " (" + user.getId() + ")\n" +
                            "**> Reported by:** " + event.getUser().getAsMention() + " (" + event.getUser().getId() + ")\n" +
                            "**> Reported in:** " + event.getChannel().getAsMention() + "\n" +
                            "**> Reason:**" + reason).build()
            ).queue()
            );
        }
    }
}