package rocks.learnercouncil.events;

import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class DeleteEvent extends ListenerAdapter {
    @Override
    public void onMessageDelete(@NotNull MessageDeleteEvent event) {
        /*
        This lays dormant until I can find a workaround. According to all sources I can find the Discord API doesn't allow you to
        content/author of deleted messages, or edited ones for that matter, however, I know that isn't true because the old Cameron
        (Written in javascript, using discord.js) didn't have any problem logging deleted messages, so if anyone reading this (like anyone
        else would actually see this) can find a workaround then let me know ASAP. Until then, this class lays dormant. I've commented it out
        in the main 'Cameron' class as that was the only one that accually used it (to register the listener)
         */
    }
}
