package rocks.learnercouncil.events;

import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class ReactEvent extends ListenerAdapter {

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        super.onMessageReactionAdd(event);
    }
}
