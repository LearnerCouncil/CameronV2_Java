package rocks.learnercouncil.events;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.util.List;

public class MessageEvent extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        TextChannel channel = event.getTextChannel();
        Message message = event.getMessage();
        String strMsg = event.getMessage().getContentStripped();

    }
}
