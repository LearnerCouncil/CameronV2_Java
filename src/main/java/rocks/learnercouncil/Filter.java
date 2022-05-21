package rocks.learnercouncil;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;

import java.awt.*;
import java.util.*;

public class Filter {

    public static Set<String> word_blacklist = new HashSet<>();
    public static Set<String> word_whitelist = new HashSet<>();

    /**
     * Initializes the word whitelist/blacklist from their respective channels
     */
    public static void initializeLists() {
        for(Message msg : Cameron.getExistingChannel("word-blacklist").getIterableHistory())
            word_blacklist.addAll(Arrays.asList(msg.getContentStripped().split("\n")));
        for(Message msg : Cameron.getExistingChannel("word-whitelist").getIterableHistory())
            word_whitelist.addAll(Arrays.asList(msg.getContentStripped().split("\n")));
    }

    /**
     * Adds a new entry to the whitelist/blacklist.
     * @param whitelist Whether it's updating the whitelist or blacklist. true = whitelist, false = blacklist;
     * @param e the entry to add to the list.
     */
    public static void updateList(boolean whitelist, String e) {
        if(whitelist)
            word_whitelist.add(e);
        word_blacklist.add(e);
    }
    /**
     * Adds a list of new entries to the whitelist/blacklist.
     * @param whitelist Whether it's updating the whitelist or blacklist. true = whitelist, false = blacklist;
     * @param e the entry to add to the list.
     */
    public static void updateList(boolean whitelist, Collection<String> e) {
        if(whitelist)
            word_whitelist.addAll(e);
        word_blacklist.addAll(e);
    }

    /**
     * Checks if a certain string is unsafe, and should be deleted.
     * @param e The string to check.
     * @return True if it contains a word that's in the blacklist, but not the whitelist.
     */
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

    /**
     * Deteltes a message, logs it to the inappropriate log, and DMs the user.
     * @param member The person that sent the mesage, but as a 'net.dv8tion.jda.api.entities.Member' as to access things like the nickname.
     * @param user The person that sent the message, but as a 'net.dv8tion.jda.api.entities.User' as to access things like username.
     * @param message The message to delete.
     */
    public static void deleteMessage(Member member, User user, Message message) {
        Cameron.getExistingChannel("inappropriate-log").sendMessageEmbeds(new EmbedBuilder()
                .setAuthor(member.getEffectiveName() + " said something bad!")
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
                                message.getChannel().getAsMention(),false))
                .setTimestamp(Cameron.CURRENT_DATE)
                .build()
        ).queue();
        user.openPrivateChannel().flatMap(c -> c.sendMessageEmbeds(new EmbedBuilder()
                .setAuthor(member.getEffectiveName(), null,  member.getEffectiveAvatarUrl())
                .setTitle("Whoa! That's not allowed here at" + message.getGuild().getName() + "!")
                .setColor(Color.RED)
                .setDescription("We have been notified of your actions. Do not do it again.")
                .addField("**> Your Message:**", message.getContentDisplay(), false)
                .setFooter("You could be Kicked or worse Banished").build()
        )).queue();
        message.delete().queue();
    }
}
