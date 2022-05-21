package rocks.learnercouncil.events;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import rocks.learnercouncil.Filter;

public class MessageEvent extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        Member member = event.getMember();
        if(member == null) return;
        User user = event.getAuthor();
        Message message = event.getMessage();
        Channel channel = event.getChannel();

        if(channel.getName().equals("blacklisted-words"))
            Filter.updateList(false, event.getMessage().getContentStripped());
        else if(channel.getName().equals("whitelisted-words"))
            Filter.updateList(true, event.getMessage().getContentStripped());

        if (!event.getMember().hasPermission(Permission.VIEW_AUDIT_LOGS)) {
            if (Filter.isUnsafe(message.getContentStripped())) {
                Filter.deleteMessage(member, user, message);
            }
        }
    }
}
