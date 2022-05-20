package rocks.learnercouncil;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.learnercouncil.commands.PingCommand;
import rocks.learnercouncil.commands.RequestCommand;
import rocks.learnercouncil.commands.RpsCommand;
import rocks.learnercouncil.commands.SayCommand;
import rocks.learnercouncil.events.EditEvent;
import rocks.learnercouncil.events.MessageEvent;
import rocks.learnercouncil.events.ReactEvent;

import javax.security.auth.login.LoginException;

public class Cameron {

    private static JDA jda;
    public static Logger logger = LoggerFactory.getLogger("rocks.learnercouncil.Cameron");

    public static void main(String[] args) throws LoginException, InterruptedException {

        logger.debug("Starting bot...");

        jda = JDABuilder.createDefault(args[0])
                .setActivity(Activity.watching("over you learners!"))
                .addEventListeners(
                        new PingCommand(),
                        new RequestCommand(),
                        new RpsCommand(),
                        new SayCommand(),
                        //
                        new EditEvent(),
                        new MessageEvent(),
                        new ReactEvent(),
                        new MessageCache()
                )
                .enableIntents(GatewayIntent.GUILD_MESSAGE_REACTIONS)
                .build().awaitReady();

        Filter.initializeLists(jda);

        logger.debug("Getting guild...");
        Guild guild = jda.getGuildById("976524936426442753");

        if(guild != null) {
            logger.info("Guild: " + guild.getName());
            guild.upsertCommand("ping", "Ping Pong!").queue();
            guild.upsertCommand("request", "Request Access to the server").queue();
            guild.upsertCommand("rps", "Play 'Rock, Paper, Scissors' with Cameron").queue();
            guild.upsertCommand("say", "Make cameron say something").addOption(OptionType.STRING, "message", "The thing to Cameron will say", true).setDefaultEnabled(false).queue();
        }
    }

    public static TextChannel getChannel(String channel) {
        try {
            return jda.getTextChannelsByName(channel, true).get(0);
        } catch (IndexOutOfBoundsException e) {
            logger.error("Channel '" + channel + "' is null.");
        }
        throw new NullPointerException();
    }
}
