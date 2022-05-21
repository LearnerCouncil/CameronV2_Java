package rocks.learnercouncil;



import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageBulkDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Message cacheing function by https://gist.github.com/Almighty-Alpaca
 */
public class MessageCache extends ListenerAdapter
{

    private final Map<String, Message> messageMap;
    //Amount of messages the cache can hold before it starts deleting old ones;
    private final int THRESHOLD = 4096;

    public MessageCache()
    {
        this.messageMap = Collections.synchronizedMap(new LinkedHashMap<>());
        Cameron.logger.info("HashMap initialized");
    }

    public Message getMessage(final String Id)
    {
        return this.messageMap.get(Id);
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        final Message message = event.getMessage();
        if(message.getAuthor().isBot()) return;
        this.messageMap.put(message.getId(), message);
        Cameron.logger.info("Message '" + message + "' added, Size: " + messageMap.size());
        if(messageMap.size() > THRESHOLD) {
            messageMap.remove(messageMap.keySet().stream().findFirst().get());
            Cameron.logger.info("Threshold reached, voiding " + messageMap.get(messageMap.keySet().stream().findFirst().get()));
        }
    }
    @Override
    public void onMessageDelete(@NotNull MessageDeleteEvent event) {
        Cameron.logger.info("a message has been deleted");
        if(this.messageMap.containsKey(event.getMessageId())) {
            Cameron.logger.info("Map contains key");
            Message message = getMessage(event.getMessageId());
                Cameron.getExistingChannel("delete-log").sendMessageEmbeds(new EmbedBuilder()
                        .setColor(Color.YELLOW)
                        .setAuthor(message.getAuthor().getAsTag() + " message got deleted")
                        .addField(new MessageEmbed.Field("Message:", message.getContentDisplay(), false))
                        .setFooter(Cameron.CURRENT_DATE)
                        .build()
                ).queue();
        }
        this.messageMap.remove(event.getMessageId());
    }
    @Override
    public void onMessageBulkDelete(@NotNull MessageBulkDeleteEvent event) {
        event.getMessageIds().forEach(i -> {
            if(this.messageMap.containsKey(i)) {
                Message message = getMessage(i);
                Cameron.getExistingChannel("delete-log").sendMessageEmbeds(new EmbedBuilder()
                        .setColor(Color.YELLOW)
                        .setAuthor(message.getAuthor().getAsTag() + " message got deleted")
                        .addField(new MessageEmbed.Field("Message:", message.getContentDisplay(), false))
                        .setTimestamp(LocalDate.now())
                        .build()
                ).queue();
            }
        });
        event.getMessageIds().forEach(this.messageMap.keySet()::remove);
    }
    @Override
    public void onMessageUpdate(@NotNull MessageUpdateEvent event) {
        if(this.messageMap.containsKey(event.getMessageId())) {
            Cameron.getExistingChannel("edit-log").sendMessageEmbeds(new EmbedBuilder()
                    .setColor(Color.YELLOW)
                    .setAuthor(getMessage(event.getMessageId()).getAuthor().getAsTag() + " message got edited")
                    .addField(new MessageEmbed.Field("Original Message:", getMessage(event.getMessageId()).getContentDisplay(), false))
                    .addField(new MessageEmbed.Field("New Message:", event.getMessage().getContentDisplay(), false))
                    .setFooter(Cameron.CURRENT_DATE)
                    .build()
            ).queue();
        }
        final Message message = event.getMessage();
        this.messageMap.put(message.getId(), message);
    }
}