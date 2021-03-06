package rocks.learnercouncil;



import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageBulkDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import rocks.learnercouncil.commands.PronounsCommand;

import java.awt.Color;
import java.time.Instant;
import java.util.*;

/**
 * Mdidified from user https://gist.github.com/Almighty-Alpaca
 */
public class MessageCache extends ListenerAdapter
{

    private static final Map<String, Message> messageMap = Collections.synchronizedMap(new LinkedHashMap<>());
    //Amount of messages the cache can hold before it starts deleting old ones;
    private static final int THRESHOLD = 4096;
    //Amout of messages it initializes from each channel on startup
    private static final int INITIAL_THRESHOLD = 50;

    public static void initializeMessages(Guild guild) {
        for(MessageChannel c : guild.getTextChannels()) {
            int j = 0;
            for(Message message : c.getIterableHistory()) {
                if(j >= INITIAL_THRESHOLD) break;
                if(!message.getAuthor().isBot())
                    messageMap.put(message.getId(), message);
                if(messageMap.size() > THRESHOLD)
                    messageMap.remove(messageMap.keySet().stream().findFirst().get());

                j++;
            }
        }
    }

    public Message getMessage(final String Id)
    {
        return messageMap.get(Id);
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        final Message message = event.getMessage();
        if(message.getAuthor().isBot()) return;
        messageMap.put(message.getId(), message);
        if(messageMap.size() > THRESHOLD)
            messageMap.remove(messageMap.keySet().stream().findFirst().get());
    }
    @Override
    public void onMessageDelete(@NotNull MessageDeleteEvent event) {
        if(messageMap.containsKey(event.getMessageId())) {
            Message message = getMessage(event.getMessageId());
            Cameron.getExistingChannel("delete-log").sendMessageEmbeds(new EmbedBuilder()
                    .setColor(Color.YELLOW)
                    .setAuthor(message.getAuthor().getAsTag() + " message got deleted")
                    .addField(new MessageEmbed.Field("Message:", message.getContentDisplay(), false))
                    .setTimestamp(Instant.now())
                    .build()
            ).queue();

            if(event.getChannel().getName().equals("word-blacklist"))
                Filter.updateList(false, message.getContentStripped(), false);
            else if(event.getChannel().getName().equals("word-whitelist"))
                Filter.updateList(true, message.getContentStripped(), false);
            else if(event.getChannel().getName().equals("pronouns"))
                PronounsCommand.removePronounRole(message.getContentStripped(), event.getGuild());
        }
        messageMap.remove(event.getMessageId());
    }
    @Override
    public void onMessageBulkDelete(@NotNull MessageBulkDeleteEvent event) {
        event.getMessageIds().forEach(i -> {
            if(messageMap.containsKey(i)) {
                Message message = getMessage(i);
                Cameron.getExistingChannel("delete-log").sendMessageEmbeds(new EmbedBuilder()
                        .setColor(Color.YELLOW)
                        .setAuthor(message.getAuthor().getAsTag() + " message got deleted")
                        .addField(new MessageEmbed.Field("Message:", message.getContentDisplay(), false))
                        .setTimestamp(Instant.now())
                        .build()
                ).queue();

                if(event.getChannel().getName().equals("word-blacklist"))
                    Filter.updateList(false, message.getContentStripped(), false);
                else if(event.getChannel().getName().equals("word-whitelist"))
                    Filter.updateList(true, message.getContentStripped(), false);
                else if(event.getChannel().getName().equals("pronouns"))
                    PronounsCommand.removePronounRole(message.getContentStripped(), event.getGuild());
            }
        });
        event.getMessageIds().forEach(messageMap.keySet()::remove);
    }
    @Override
    public void onMessageUpdate(@NotNull MessageUpdateEvent event) {
        if(messageMap.containsKey(event.getMessageId())) {
            Cameron.getExistingChannel("edit-log").sendMessageEmbeds(new EmbedBuilder()
                    .setColor(Color.YELLOW)
                    .setAuthor(getMessage(event.getMessageId()).getAuthor().getAsTag() + " message got edited")
                    .addField(new MessageEmbed.Field("Original Message:", getMessage(event.getMessageId()).getContentDisplay(), false))
                    .addField(new MessageEmbed.Field("New Message:", event.getMessage().getContentDisplay(), false))
                    .setTimestamp(Instant.now())
                    .build()
            ).queue();
        }
        final Message message = event.getMessage();
        if(message.getAuthor().isBot()) return;
        messageMap.put(message.getId(), message);
    }
}