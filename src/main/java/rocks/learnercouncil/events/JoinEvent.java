package rocks.learnercouncil.events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import rocks.learnercouncil.Cameron;

public class JoinEvent extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        event.getGuild().addRoleToMember(event.getMember(), Cameron.getExistingRole("Waiting")).queue();
        Cameron.getExistingChannel("rr-1").sendMessage("Hello " + event.getMember().getAsMention() + "! Type **/request** to begin.").queue();
        Member m = event.getMember();
        Cameron.getExistingChannel("member-log").sendMessageEmbeds(new EmbedBuilder()
                .setAuthor(m.getEffectiveName(), "", m.getEffectiveAvatarUrl())
                .setTitle(m.getEffectiveName() + "Joined the server")
                .setDescription("And now we wait...")
                .setTimestamp(Cameron.CURRENT_DATE)
                .build()
        ).queue();
    }
}
