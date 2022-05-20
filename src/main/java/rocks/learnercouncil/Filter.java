package rocks.learnercouncil;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;

import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Filter {

    public static Set<String> word_blacklist = new HashSet<>();
    public static Set<String> word_whitelist = new HashSet<>();

    public static void initializeLists(JDA jda) {
        for(Message msg : jda.getTextChannelsByName("word-blacklist", false).get(0).getIterableHistory())
            word_blacklist.addAll(Arrays.asList(msg.getContentStripped().split("\n")));
        for(Message msg : jda.getTextChannelsByName("word-whitelist", false).get(0).getIterableHistory())
            word_whitelist.addAll(Arrays.asList(msg.getContentStripped().split("\n")));
    }

    public static void updateList(boolean whitelist, String e) {
        if(whitelist)
            word_whitelist.add(e);
        word_blacklist.add(e);
    }
    public static void updateList(boolean whitelist, Collection<String> e) {
        if(whitelist)
            word_whitelist.addAll(e);
        word_blacklist.addAll(e);
    }
    public static boolean isUnsafe(String e) {
        String[] msg = e.split(" ");
        for(String s : msg) {
            for(String b : word_blacklist) {
                if(s.contains(b)) {
                    boolean safe = false;
                    for (String w : word_whitelist) {
                        if(s.equals(w)) {
                            safe = true;
                            break;
                        }
                    }
                    if(!safe) return true;
                }
            }
        }
        return false;
    }

    public static boolean isSafe(Collection<String> e) {
        for(String s : e)
            if(isUnsafe(s)) return false;
        return true;
    }

    public static void deleteMessage(Member member, User user, Message message, Channel channel) {
        Cameron.getChannel("inappropriate-log").sendMessageEmbeds(new EmbedBuilder()
                .setAuthor(member.getNickname() == null ? user.getName() + " said something bad!" : member.getNickname() + " said something bad!")
                .setColor(Color.ORANGE)
                .setDescription("Do what you want with this information")
                .addField(new MessageEmbed.Field("User Information",
                        "> **User:**\n" +
                                user.getAsMention() +
                                "\n> **Username:**\n" +
                                user.getName() +
                                "\n> **Tag:**\n" +
                                user.getAsTag() +
                                "\n> **Created At:**\n" +
                                user.getTimeCreated(), false))
                .addField(new MessageEmbed.Field("Message",
                        "> **Users Message:**\n" +
                                message.getContentDisplay() +
                                "\n> **Channel:**\n" +
                                channel.getAsMention(),false))
                .setFooter(DateTimeFormatter.ofPattern("MM/dd/yyyy").format(LocalDate.now()))
                .build()
        ).queue();
        user.openPrivateChannel().flatMap(c -> c.sendMessageEmbeds(new EmbedBuilder()
                .setAuthor(member.getNickname() == null ? user.getName() : member.getNickname(), user.getEffectiveAvatarUrl())
                .setTitle("Whoa! That's not allowed here at" + message.getGuild().getName() + "!")
                .setColor(Color.RED)
                .setDescription("We have been notified of your actions. Do not do it again.")
                .addField("**> Your Message:**", message.getContentDisplay(), false)
                .setFooter("You could be Kicked or worse Banished").build()
        )).queue();
        message.delete().queue();
    }
}
