package rocks.learnercouncil.events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import rocks.learnercouncil.Cameron;
import rocks.learnercouncil.Filter;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ReactEvent extends ListenerAdapter {

    private final Map<Emoji, Character> dictionary = new HashMap<>();
    public ReactEvent() {
        initializeDictionary();
    }



    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        if((Emoji.fromEmote(event.getReactionEmote().getEmote()).equals(Emoji.fromUnicode("\uD83D\uDD95"))))
            event.getReaction().removeReaction().queue();

        event.retrieveMessage().queue(m -> {

            StringBuilder sb = new StringBuilder();
            m.getReactions().forEach(r -> sb.append(dictionary.getOrDefault(Emoji.fromEmote(r.getReactionEmote().getEmote()), '-')));
            String result = sb.toString().replaceAll("-", "");

            Set<String> users = new HashSet<>();
            m.getReactions().forEach(r -> r.retrieveUsers().queue(l -> l.forEach(u -> users.add(u.getAsMention()))));

            if(Filter.isUnsafe(result)) {
                String link = "https://www.discord.com/channels/" + m.getGuild().getId() + "/" + m.getChannel().getId() + "/" + m.getId();
                Cameron.getChannel("kinda-sus-log").sendMessageEmbeds(new EmbedBuilder()
                        .setColor(Color.YELLOW)
                        .setAuthor("Suspicious sequence of reactions on " + m.getAuthor().getAsTag() + " message")
                        .setDescription("Do what you want with this information.")
                        .addField("Message: ", link, false)
                        .addField(new MessageEmbed.Field("Offenders: ", String.join(", ", users), false))
                        .build()
                ).queue(l -> m.getReactions().forEach(r -> l.addReaction(r.getReactionEmote().getEmote()).queue()));
            }
        });
    }
    private void initializeDictionary() {
        dictionary.put(Emoji.fromUnicode("\uD83C\uDDE6"), 'a');
        dictionary.put(Emoji.fromUnicode("\uD83C\uDDE7"), 'b');
        dictionary.put(Emoji.fromUnicode("\uD83C\uDDE8"), 'c');
        dictionary.put(Emoji.fromUnicode("\uD83C\uDDE9"), 'd');
        dictionary.put(Emoji.fromUnicode("\uD83C\uDDEA"), 'e');
        dictionary.put(Emoji.fromUnicode("\uD83C\uDDEB"), 'f');
        dictionary.put(Emoji.fromUnicode("\uD83C\uDDEC"), 'g');
        dictionary.put(Emoji.fromUnicode("\uD83C\uDDED"), 'h');
        dictionary.put(Emoji.fromUnicode("\uD83C\uDDEE"), 'i');
        dictionary.put(Emoji.fromUnicode("\uD83C\uDDEF"), 'j');
        dictionary.put(Emoji.fromUnicode("\uD83C\uDDF0"), 'k');
        dictionary.put(Emoji.fromUnicode("\uD83C\uDDF1"), 'l');
        dictionary.put(Emoji.fromUnicode("\uD83C\uDDF2"), 'm');
        dictionary.put(Emoji.fromUnicode("\uD83C\uDDF3"), 'n');
        dictionary.put(Emoji.fromUnicode("\uD83C\uDDF4"), 'o');
        dictionary.put(Emoji.fromUnicode("\uD83C\uDDF5"), 'p');
        dictionary.put(Emoji.fromUnicode("\uD83C\uDDF6"), 'q');
        dictionary.put(Emoji.fromUnicode("\uD83C\uDDF7"), 'r');
        dictionary.put(Emoji.fromUnicode("\uD83C\uDDF8"), 's');
        dictionary.put(Emoji.fromUnicode("\uD83C\uDDF9"), 't');
        dictionary.put(Emoji.fromUnicode("\uD83C\uDDFA"), 'u');
        dictionary.put(Emoji.fromUnicode("\uD83C\uDDFB"), 'v');
        dictionary.put(Emoji.fromUnicode("\uD83C\uDDFC"), 'w');
        dictionary.put(Emoji.fromUnicode("\uD83C\uDE00"), 'x');
        dictionary.put(Emoji.fromUnicode("\uD83C\uDE01"), 'y');
        dictionary.put(Emoji.fromUnicode("\uD83C\uDE02"), 'z');
        //other
        dictionary.put(Emoji.fromUnicode("\uD83C\uDD96"), 'n');
        dictionary.put(Emoji.fromUnicode("\uD83C\uDD96"), 'g');

        dictionary.put(Emoji.fromUnicode("\uD83C\uDD9A"), 'v');
        dictionary.put(Emoji.fromUnicode("\uD83C\uDD9A"), 's');

        dictionary.put(Emoji.fromUnicode("\uD83C\uDD70️"), 'a');

        dictionary.put(Emoji.fromUnicode("\uD83C\uDD71"), 'b');

        dictionary.put(Emoji.fromUnicode("\uD83C\uDD8E"), 'a');
        dictionary.put(Emoji.fromUnicode("\uD83C\uDD8E"), 'b');

        dictionary.put(Emoji.fromUnicode("\uD83C\uDD91"), 'c');
        dictionary.put(Emoji.fromUnicode("\uD83C\uDD91"), 'l');

        dictionary.put(Emoji.fromUnicode("\uD83C\uDD7E"), 'o');

        dictionary.put(Emoji.fromUnicode("\uD83C\uDD98"), 's');
        dictionary.put(Emoji.fromUnicode("\uD83C\uDD98"), 'o');

        dictionary.put(Emoji.fromUnicode("❌"), 'x');

        dictionary.put(Emoji.fromUnicode("⭕"), 'o');

        dictionary.put(Emoji.fromUnicode("\uD83C\uDD97"), 'o');
        dictionary.put(Emoji.fromUnicode("\uD83C\uDD97"), 'k');

        dictionary.put(Emoji.fromUnicode("\uD83C\uDD99"), 'u');
        dictionary.put(Emoji.fromUnicode("\uD83C\uDD99"), 'p');
    }
}
