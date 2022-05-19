package rocks.learnercouncil;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Filter {

    public static Set<String> word_blacklist = new HashSet<>();
    public static Set<String> word_whitelist = new HashSet<>();

    public static void initializeLists(JDA jda) {
        for(Message msg : jda.getTextChannelsByName("word-blacklist", false).get(0).getIterableHistory())
            word_blacklist.add(msg.getContentStripped());
        for(Message msg : jda.getTextChannelsByName("word-whitelist", false).get(0).getIterableHistory())
            word_whitelist.add(msg.getContentStripped());
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
    public static boolean isSafe(String e) {
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
                    if(!safe) return false;
                }
            }
        }
        return true;
    }

    public static boolean IsSafe(Collection<String> e) {
        for(String s : e)
            if(!isSafe(s)) return false;
        return true;
    }
}
