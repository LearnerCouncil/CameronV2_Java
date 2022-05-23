package rocks.learnercouncil;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.learnercouncil.commands.*;
import rocks.learnercouncil.events.EditEvent;
import rocks.learnercouncil.events.JoinEvent;
import rocks.learnercouncil.events.MessageEvent;
import rocks.learnercouncil.events.ReactEvent;

import javax.security.auth.login.LoginException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

public class Cameron {

    //Current guild (Server) ID, currently set to my test server, but must be changed on release to the Learner Server's.
    public static final String GUILD_ID = "976524936426442753";
    public static final String CURRENT_DATE = (DateTimeFormatter.ofPattern("MM/dd/yyyy").format(LocalDate.now()));

    private static JDA jda;
    public static final Logger logger = LoggerFactory.getLogger("rocks.learnercouncil.Cameron");

    /**
     * The main method, it fires once the program starts up.
     * @param args Console arguments, the first one must be the bot token, the rest are ignored.
     * @throws InterruptedException If the JDA instance ever gets interrupted.
     * @throws LoginException I assume in case an invalid token is supplied.
     */
    public static void main(String[] args) throws InterruptedException, LoginException {

        logger.debug("Starting bot...");
        if(args.length <= 0)
            throw new IllegalArgumentException("Must supply the bot token as the first argument.");
        jda = JDABuilder.createDefault(args[0])
                .setActivity(Activity.watching("over you learners!"))
                .addEventListeners(
                        //Commands
                        new PingCommand(),
                        new RequestCommand(),
                        new RpsCommand(),
                        new SayCommand(),
                        new PronounsCommand(),
                        //Events
                        new EditEvent(),
                        new MessageEvent(),
                        new ReactEvent(),
                        new MessageCache(),
                        new JoinEvent()
                )
                .enableIntents(
                        GatewayIntent.GUILD_MESSAGE_REACTIONS,
                        GatewayIntent.GUILD_MEMBERS
                ).build().awaitReady();

        Filter.initializeLists();
        PronounsCommand.initializeRoles();

        logger.debug("Getting guild...");
        Guild guild = jda.getGuildById(GUILD_ID);

        if(guild != null) {
            logger.info("Guild: " + guild.getName());
            MessageCache.initializeMessages(guild);
            //Currently guild commands but may be changed to global commands on release
            //i.e. jda.updateCommands().addCommands(...).queue();
            guild.updateCommands().addCommands(
            Commands.slash("ping", "Ping Pong!"),
            Commands.slash("request", "Request Access to the server"),
            Commands.slash("rps", "Play 'Rock, Paper, Scissors' with Cameron"),
            Commands.slash("say", "Make cameron say something").addOption(OptionType.STRING, "message", "The thing to Cameron will say", true).setDefaultEnabled(false),
            Commands.slash("sayembed", "Make cameron say something as an embed").addOptions(
                    new OptionData(OptionType.STRING, "color", "The color of the sidebar of the embed", true, true),
                    new OptionData(OptionType.STRING, "title", "The title of the embed", true),
                    new OptionData(OptionType.STRING, "message", "The thing cameron will say", true)).setDefaultEnabled(false),
            Commands.slash("pronouns", "Set your pronouns")
            ).queue();
            guild.loadMembers().onSuccess(l -> {
                logger.error(l.toString());
                for(Member m : l) {
                    Role pnListRole = Cameron.getExistingRole("----------------  Pronouns ----------------");
                    if (m.getRoles().contains(pnListRole) || m.getUser().isBot()) continue;
                    guild.addRoleToMember(m, pnListRole).queue();
                    logger.error("Adding role to: " + m);
                }
            });

        }
    }

    /**
     * Gets a certain text channel by it's name.
     * @param name The name of the text channel, must refer to an existing channel.
     * @return The channel that matches the supplied name
     * @throws NullPointerException if a channel by that name does not exist.
     */
    public static TextChannel getExistingChannel(@NotNull String name) {
        if(!jda.getTextChannelsByName(name, true).isEmpty())
            return jda.getTextChannelsByName(name, true).get(0);
        logger.error("Channel '" + name + "' doesn't exist!");
        throw new NullPointerException();
    }

    /**
     * Gets a certain role by it's name.
     * @param name The name of the role, must refer to an existing role.
     * @return The role taht matches the supplied role.
     * @throws NullPointerException if a role by that name does not exist.
     */
    public static Role getExistingRole(@NotNull String name) {
        Guild guild = jda.getGuildById(GUILD_ID);
        if(!guild.getRolesByName(name, true).isEmpty())
            return guild.getRolesByName(name, true).get(0);
        logger.error("Role '" + name + "' doesn't exist!");
        throw new NullPointerException();
    }

    /**
     * Getter for the JDA instance.
     * @return the JDA Instance.
     */
    public static JDA getJDA() {
        return jda;
    }
}
