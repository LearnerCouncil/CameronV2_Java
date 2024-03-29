package rocks.learnercouncil.cameron.events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import rocks.learnercouncil.cameron.Cameron;
import rocks.learnercouncil.cameron.Filter;

import java.awt.*;
import java.time.Instant;
import java.util.*;

public class ReactEvent extends ListenerAdapter {

    private final int THRESHOLD = 10;
    private final Map<String, Character> dictionary = new HashMap<>();

    public ReactEvent() {
        initializeDictionary();
    }

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        //Check for middle finger
        if(event.getUser() != null && event.getUser().isBot()) return;
        if((event.getReactionEmote().getEmoji().equals("\uD83D\uDD95")))
            event.retrieveUser().queue(u -> event.getReaction().removeReaction(u).queue());
        //check for a star
        if(event.getReactionEmote().getEmoji().equals("⭐") && !event.getChannel().getName().equals("starboard")) {
                event.retrieveMessage().queue(m -> {
                    //noinspection ConstantConditions
                    if(m.getReactionByUnicode("⭐").getCount() >= THRESHOLD) {
                        addToStarred(m, event.getGuild());
                    }
                });
        }
        //Check for spelled out word
        event.retrieveMessage().queue(m -> {

            StringBuilder sb = new StringBuilder();
            m.getReactions().forEach(r -> sb.append(dictionary.getOrDefault(r.getReactionEmote().isEmoji() ? r.getReactionEmote().getEmoji() : '-', '-')));
            String result = sb.toString().replaceAll("-", "");

            Set<String> users = new HashSet<>();
            m.getReactions().forEach(r -> r.retrieveUsers().queue(l -> l.forEach(u -> users.add(u.getAsMention()))));

            if(!Filter.isUnsafe(result)) return;
            StringBuilder reactions = new StringBuilder();
            m.getReactions().forEach(r -> reactions.append(r.getReactionEmote().getEmoji()).append(" "));
            String link = "https://www.discord.com/channels/" + m.getGuild().getId() + "/" + m.getChannel().getId() + "/" + m.getId();
            Cameron.getExistingChannel("kinda-sus-log").sendMessageEmbeds(new EmbedBuilder()
                    .setColor(Color.YELLOW)
                    .setAuthor("Suspicious sequence of reactions on " + m.getAuthor().getAsTag() + " message")
                    .setDescription("Do what you want with this information.")
                    .addField("Message: ", link, false)
                    .addField(new MessageEmbed.Field("Offenders:", String.join(", ", users), false))
                    .addField(new MessageEmbed.Field("Reactions:", reactions.toString(), false))
                    .build()
            ).queue(message -> message.editMessageEmbeds(new EmbedBuilder()
                    .setColor(Color.YELLOW)
                    .setAuthor("Suspicious sequence of reactions on " + m.getAuthor().getAsTag() + " message")
                    .setDescription("Do what you want with this information.")
                    .addField("Message: ", link, false)
                    .addField(new MessageEmbed.Field("Offenders:", String.join(", ", users), false))
                    .addField(new MessageEmbed.Field("Reactions:", reactions.toString(), false))
                    .setTimestamp(Instant.now())
                    .build()).queue());
        });
    }

    private void addToStarred(Message message, Guild guild) {
        TextChannel channel = Cameron.getExistingChannel("starboard");
        channel.getIterableHistory().queue(messageHistory -> guild.retrieveMember(message.getAuthor()).queue(member -> {
            for(Message msg : messageHistory) {
                if(msg.getEmbeds().isEmpty()) continue;
                if(msg.getEmbeds().get(0).getFields().size() < 2) continue;
                if(Objects.equals(msg.getEmbeds().get(0).getFields().get(1).getValue(), "https://www.discord.com/channels/" + message.getGuild().getId() + "/" + message.getChannel().getId() + "/" + message.getId())) return;
            }
            EmbedBuilder embed = new EmbedBuilder()
                    .setColor(Color.YELLOW)
                    .setAuthor(member.getEffectiveName(), null, message.getAuthor().getEffectiveAvatarUrl())
                    .addField("Starred Message", message.getContentDisplay(), false)
                    .addField("Message Link", "https://www.discord.com/channels/" + message.getGuild().getId() + "/" + message.getChannel().getId() + "/" + message.getId(), false)
                    .setTimestamp(Instant.now());
            if(!message.getAttachments().isEmpty() && message.getAttachments().get(0).isImage())
                embed.setImage(message.getAttachments().get(0).getUrl())
                        .addField("","**Attatched Image**", false);
            channel.sendMessageEmbeds(embed.build()).queue();
        }));
    }
    
    private void initializeDictionary() {
        //Regional Indicators
        dictionary.put("\uD83C\uDDE6", 'a');
        dictionary.put("\uD83C\uDDE7", 'b');
        dictionary.put("\uD83C\uDDE8", 'c');
        dictionary.put("\uD83C\uDDE9", 'd');
        dictionary.put("\uD83C\uDDEA", 'e');
        dictionary.put("\uD83C\uDDEB", 'f');
        dictionary.put("\uD83C\uDDEC", 'g');
        dictionary.put("\uD83C\uDDED", 'h');
        dictionary.put("\uD83C\uDDEE", 'i');
        dictionary.put("\uD83C\uDDEF", 'j');
        dictionary.put("\uD83C\uDDF0", 'k');
        dictionary.put("\uD83C\uDDF1", 'l');
        dictionary.put("\uD83C\uDDF2", 'm');
        dictionary.put("\uD83C\uDDF3", 'n');
        dictionary.put("\uD83C\uDDF4", 'o');
        dictionary.put("\uD83C\uDDF5", 'p');
        dictionary.put("\uD83C\uDDF6", 'q');
        dictionary.put("\uD83C\uDDF7", 'r');
        dictionary.put("\uD83C\uDDF8", 's');
        dictionary.put("\uD83C\uDDF9", 't');
        dictionary.put("\uD83C\uDDFA", 'u');
        dictionary.put("\uD83C\uDDFB", 'v');
        dictionary.put("\uD83C\uDDFC", 'w');
        dictionary.put("\uD83C\uDE00", 'x');
        dictionary.put("\uD83C\uDE01", 'y');
        dictionary.put("\uD83C\uDE02", 'z');
        //other
        dictionary.put("\uD83C\uDD96", 'n');

        dictionary.put("\uD83C\uDD9A", 's');

        dictionary.put("\uD83C\uDD70", 'a');

        dictionary.put("\uD83C\uDD71", 'b');

        dictionary.put("\uD83C\uDD8E", 'b');

        dictionary.put("\uD83C\uDD91", 'c');

        dictionary.put("\uD83C\uDD7E", 'o');

        dictionary.put("\uD83C\uDD98", 's');

        dictionary.put("❌", 'x');

        dictionary.put("⭕", 'o');

        dictionary.put("ℹ️", 'i');

        dictionary.put("\uD83C\uDD97", 'k');

        dictionary.put("\uD83C\uDD99", 'u');
    }
}
