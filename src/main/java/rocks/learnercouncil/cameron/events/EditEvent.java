package rocks.learnercouncil.cameron.events;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import rocks.learnercouncil.cameron.Filter;

public class EditEvent extends ListenerAdapter {

    @Override
    public void onMessageUpdate(@NotNull MessageUpdateEvent event) {
        if(event.getAuthor().isBot()) return;
        Member member = event.getMember();
        if(member == null) return;
        User user = event.getAuthor();
        Message message = event.getMessage();
        if(!event.getMember().hasPermission(Permission.VIEW_AUDIT_LOGS)) {
            if(Filter.isUnsafe(message.getContentStripped())) {
                Filter.deleteMessage(member, user, message);
            }
        }
    }
}
