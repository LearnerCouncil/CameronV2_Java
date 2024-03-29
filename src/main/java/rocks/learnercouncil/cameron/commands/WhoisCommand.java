package rocks.learnercouncil.cameron.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import rocks.learnercouncil.cameron.Cameron;

import java.time.Instant;
import java.util.stream.Collectors;

public class WhoisCommand extends ListenerAdapter {

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if(!event.getName().equals("whois")) return;
        if(!event.isFromGuild()) {
            event.reply("Must me executed from a guild").queue();
            return;
        }
        if(event.getOption("user") == null) return;
        Member member = event.getOption("user").getAsMember();
        User user = event.getOption("user").getAsUser();
        event.replyEmbeds(new EmbedBuilder()
                .setFooter(member.getEffectiveName(), member.getEffectiveAvatarUrl())
                .setTimestamp(Instant.now())
                .setThumbnail(user.getEffectiveAvatarUrl())
                .addField("Member Information:",
                        "> **Display Name**:\n" + member.getEffectiveName() +
                                "\n> **Joined At:**\n" + member.getTimeJoined() +
                                "\n> **Roles:**\n" + member.getRoles().stream().map(r -> {
                                    if(!(r.getName().startsWith("-"))) return r.getAsMention();
                                    return "\n> **Pronouns:**\n";
                                }).collect(Collectors.joining(", ")).replace(", \n> **Pronouns:**\n, ", "\n> **Pronouns:**\n")
                        , true)
                .addField("User Information:",
                        "> **Username:**\n" + user.getName() +
                                "\n> **Tag:**\n" + user.getAsTag() +
                                "\n> **Date Created:**\n" + user.getTimeCreated()
                        , true)
                .addBlankField(false)
                .addField("Status:", switch (member.getOnlineStatus()) {
                    case OFFLINE, INVISIBLE -> "⚫ This user is currently offline.";
                    case ONLINE, IDLE -> "\uD83D\uDFE2 This user is currently online.";
                    case DO_NOT_DISTURB -> " ⛔ This user is currently online, but does not want to be disturbed.";
                    default -> "❔ I don't know if this user is currently online.";
                    }, false)
                .build()
        ).queue();
    }
}
