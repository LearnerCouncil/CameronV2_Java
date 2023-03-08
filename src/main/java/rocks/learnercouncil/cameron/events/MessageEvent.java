package rocks.learnercouncil.cameron.events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import rocks.learnercouncil.cameron.Cameron;
import rocks.learnercouncil.cameron.Filter;
import rocks.learnercouncil.cameron.commands.PronounsCommand;

import java.awt.*;

public class MessageEvent extends ListenerAdapter {

    private static final int THRESHOLD = 15;

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if(event.getAuthor().isBot()) return;
        Member member = event.getMember();
        if(member == null) return;
        User user = event.getAuthor();
        Message message = event.getMessage();
        Channel channel = event.getChannel();
        if(channel.getName().equals("word-blacklist"))
            Filter.updateList(false, event.getMessage().getContentStripped(), true);
        else if(channel.getName().equals("word-whitelist"))
            Filter.updateList(true, event.getMessage().getContentStripped(), true);
        else if(channel.getName().equals("pronouns"))
            PronounsCommand.roleList.add(message.getContentStripped());
        else if(channel.getName().equals("rr-1"))
            return;
        if(event.getMember().hasPermission(Permission.VIEW_AUDIT_LOGS)) return;

        if(Filter.isUnsafe(message.getContentStripped()))
            Filter.deleteMessage(member, user, message);

        if(Filter.countNonASCII(message.getContentStripped()) >= THRESHOLD)
            Cameron.getExistingChannel("kinda-sus-log").sendMessageEmbeds(new EmbedBuilder()
                    .setColor(Color.YELLOW)
                    .setAuthor("Suspicious amount of unusual characters on " + user.getAsTag() + " message")
                    .setDescription("Do what you want with this information.")
                    .addField("Message:", message.getContentDisplay(), false)
                    .build()).queue();
    }
}
