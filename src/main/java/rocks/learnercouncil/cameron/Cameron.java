package rocks.learnercouncil.cameron;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.learnercouncil.cameron.commands.*;
import rocks.learnercouncil.cameron.events.EditEvent;
import rocks.learnercouncil.cameron.events.JoinEvent;
import rocks.learnercouncil.cameron.events.MessageEvent;
import rocks.learnercouncil.cameron.events.ReactEvent;

import javax.security.auth.login.LoginException;
import java.util.List;

public class Cameron {

    //Current guild (Server), Set by the second command-line argument.
    private static Guild guild;

    private static JDA jda;
    public static final Logger logger = LoggerFactory.getLogger("rocks.learnercouncil.cameron.Cameron");

    /**
     * The main method, it fires once the program starts up.
     * @param args Console arguments, the first one must be the bot token, the rest are ignored.
     * @throws InterruptedException If the JDA instance ever gets interrupted.
     * @throws LoginException I assume in case an invalid token is supplied.
     */
    public static void main(String[] args) throws InterruptedException, LoginException {
        logger.debug("Starting bot...");
        if(args.length <= 1)
            throw new IllegalArgumentException("Must supply the bot token as the first argument, and the guild id as the second.");
        jda = JDABuilder.createDefault(args[0])
                .setActivity(Activity.watching("over you learners!"))
                .addEventListeners(
                        //Commands
                        new PingCommand(),
                        new RequestCommand(),
                        new RpsCommand(),
                        new SayCommand(),
                        new PronounsCommand(),
                        new WhoisCommand(),
                        new ReportCommand(),
                        new KickCommand(),
                        new BanCommand(),
                        new JoinlistCommand(),
                        //Events
                        new EditEvent(),
                        new MessageEvent(),
                        new ReactEvent(),
                        new MessageCache(),
                        new JoinEvent()
                )
                .enableIntents(
                        GatewayIntent.GUILD_MESSAGE_REACTIONS,
                        GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.GUILD_PRESENCES
                ).enableCache(
                        CacheFlag.ONLINE_STATUS
                ).build().awaitReady();

        guild = jda.getGuildById(args[1]);
        if(guild == null) throw new IllegalArgumentException("Invalid guild ID.");

        Filter.initializeLists();
        PronounsCommand.initialize(guild);
        Cameron.logger.info("Found Guild: " + guild.getName());
        MessageCache.initializeMessages(guild);
        //Global commands don't seem to work, even after waiting for the alotted time, so for the time being I'm sticking to guild commands.
        guild.updateCommands().addCommands(
                Commands.slash("ping", "Ping Pong!"),
                Commands.slash("request", "Request Access to the server"),
                Commands.slash("rps", "Play 'Rock, Paper, Scissors' with Cameron"),
                Commands.slash("say", "Make cameron say something")
                        .addOption(OptionType.STRING, "message", "The thing to Cameron will say", true)
                        .setDefaultEnabled(false),
                Commands.slash("sayembed", "Make cameron say something as an embed")
                        .addOptions(
                                new OptionData(OptionType.STRING, "color", "The color of the sidebar of the embed", true, true),
                                new OptionData(OptionType.STRING, "title", "The title of the embed", true),
                                new OptionData(OptionType.STRING, "message", "The thing cameron will say", true))
                        .setDefaultEnabled(false),
                Commands.slash("pronouns", "Set your pronouns"),
                Commands.slash("whois", "Check the information of a certain user").addOption(OptionType.USER, "user", "The usre to get the information of", true),
                Commands.slash("report", "Report a user")
                        .addOptions(
                                new OptionData(OptionType.USER, "user", "The user you're reporting.", true),
                                new OptionData(OptionType.STRING, "reason", "The reason you're reporting them.", true)),
                Commands.slash("kick", "Kicks a user").addOptions(
                        new OptionData(OptionType.USER, "user", "the user to kick", true),
                        new OptionData(OptionType.STRING, "reason", "the reason you're kicking them", false))
                        .setDefaultEnabled(false),
                Commands.slash("ban", "Kicks a user")
                        .addOptions(
                                new OptionData(OptionType.USER, "user", "the user to kick", true),
                                new OptionData(OptionType.STRING, "reason", "the reason you're kicking them", false),
                                new OptionData(OptionType.INTEGER, "delete_history_days", "The amount of days of recent message history from this user you want to delete.", false))
                        .setDefaultEnabled(false),
                Commands.slash("joinlist", "Request access to the Minecraft Server")
                        .addOption(OptionType.STRING, "username", "Your Minecraft username", true)
        ).queue(c -> logger.info("Loaded Commands"));
    }

    /**
     * Gets a certain text channel by it's name.
     * @param name The name of the text channel, must refer to an existing channel.
     * @return The channel that matches the supplied name
     * @throws NullPointerException if a channel by that name does not exist.
     */
    public static TextChannel getExistingChannel(@NotNull String name) {
        if(!guild.getTextChannelsByName(name, true).isEmpty())
            return guild.getTextChannelsByName(name, true).get(0);
        logger.error("Channel '" + name + "' doesn't exist!");
        throw new NullPointerException();
    }

    /**
     * Gets a certain role by it's name.
     * @param name The name of the role, must refer to an existing role.
     * @param guild The guild to get the role from.
     * @return The role that matches the supplied role.
     * @throws NullPointerException if a role by that name does not exist.
     */
    public static Role getExistingRole(@NotNull String name) {
        if(!guild.getRolesByName(name, true).isEmpty())
            return guild.getRolesByName(name, true).get(0);
        logger.error("Role '" + name + "' doesn't exist!");
        throw new NullPointerException();
    }

    /**
     * Gets a certain emoji by it's name.
     * @param name The name of the emoji, nust refer to an existing emoji.
     * @param guild The guild to get the emoji from.
     * @return The emoji that matches the supplied name.
     * @throws NullPointerException if an emoji by that name doesn't exist
     */
    public static Emoji getExistingEmoji(@NotNull String name) {
        List<Emote> emojis = guild.getEmotesByName(name, true);
        if(!emojis.isEmpty())
            return Emoji.fromEmote(emojis.get(0));
        logger.error("Emoji '" + name + "' doesn't exist!");
        throw new NullPointerException();
    }

    /**
     * Getter for the JDA instance.
     * @return the JDA Instance.
     */
    public static JDA getJDA() {
        return jda;
    }

    @NotNull
    public static Guild getGuild() {
        return guild;
    }
}
