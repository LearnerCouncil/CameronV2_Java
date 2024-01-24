package rocks.learnercouncil.cameron.commands.request;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;
import rocks.learnercouncil.cameron.Cameron;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class RequestCommand extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if(!event.getChannel().getName().equals("rr-1")) {
            event.reply("Can't do that here").setEphemeral(true).queue();
            return;
        }
        if(Request.exists()) {
            event.reply("A request is already in progress, please be patient.");
            return;
        }
        event.reply("Processing Request...").queue(m -> m.deleteOriginal().queueAfter(1, TimeUnit.MILLISECONDS));
        new Request(event.getUser().getIdLong(), event.getChannel());
    }
    /*
    private static final MessageEmbed CONFIRM_QUESTION = new EmbedBuilder()
            .setAuthor("Expires in 1 minute")
            .setColor(Color.GREEN)
            .setDescription("You are about to request access to the Learner Server, do you wish to proceed?").build();
    private static final MessageEmbed RANK_QUESTION = new EmbedBuilder()
            .setAuthor("Expires in 1 minute")
            .setColor(Color.GREEN)
            .setDescription("What is your rank?").build();
    private static final MessageEmbed NAME_QUESTION = new EmbedBuilder()
            .setAuthor("Expires in 2 minutes")
            .setColor(Color.CYAN)
            .setDescription("Please type your first and last name.").build();
    private static final MessageEmbed PARENT_EMAIL_QUESTION = new EmbedBuilder()
            .setAuthor("Expires in 5 minutes")
            .setColor(Color.CYAN)
            .setDescription("Please type your parents email address.").build();
    private static final MessageEmbed EMAIL_QUESTION = new EmbedBuilder()
            .setAuthor("Expires in 5 minutes")
            .setColor(Color.CYAN)
            .setDescription("Please type your email address.").build();
    private static final MessageEmbed AGE_GROUP_QUESTION = new EmbedBuilder()
            .setAuthor("Expires in 1 minute")
            .setColor(Color.CYAN)
            .setDescription("What is your age group?").build();
    private static final MessageEmbed CHILD_NAMES_QUESTION = new EmbedBuilder()
            .setAuthor("Expires in 2 minute")
            .setColor(Color.CYAN)
            .setDescription("Please type your Child's name(s)").build();
    private static final MessageEmbed ALP_JOB_QUESTION = new EmbedBuilder()
            .setAuthor("Expires in 2 minute")
            .setColor(Color.CYAN)
            .setDescription("Please type your job at ALP Nature Center and Perserve").build();
    private static final MessageEmbed END_QUESTION = new EmbedBuilder()
            .setAuthor("Expires in 1 minute")
            .setColor(Color.GREEN)
            .setDescription("You're all set. We will let you know when you've been accepted.").build();

    private void sendInfoConfirmation(MessageChannel channel) {
        EmbedBuilder embedbuilder = new EmbedBuilder()
                .setAuthor("Expires in 1 minute")
                .setColor(Color.GREEN)
                .setDescription("This is the information we received, is this correct?")
                .addField(new MessageEmbed.Field("Rank: ", role, false))
                .addField(new MessageEmbed.Field("Name: ", name, false))
                .addField(new MessageEmbed.Field(role.equals("Learner") ? "Parent Email: " : "Email: ", email, false));
        if(!role.equals("Thespian")) {
            embedbuilder.addField(new MessageEmbed.Field(switch (role) {
                case "Learner" -> "Age Group: ";
                case "Teacher" -> "Job at ALPNCP: ";
                case "Parent" -> "Child's name(s): ";
                default -> "Unknown: ";
            }, answer4, false));
        }
        channel.sendMessageEmbeds(embedbuilder.build()).setActionRow(
                Button.secondary("q5_1", Emoji.fromUnicode("✅")),
                Button.secondary("q5_2", Emoji.fromMarkdown("<:redtick:785223937495531520>"))
        ).queue(m -> deleteIfNotNull(m, 120));
    }


    private String role, name, email, answer4;

    private int questionNumber = -1;
    private long userid = 0;

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if(!event.getName().equals("request")) return;
        if(!event.getChannel().getName().equals("rr-1")) {
            event.reply("Can't do that here").setEphemeral(true).queue();
            return;
        }
        if(questionNumber != -1) {
            event.reply("A request is already in progress, please be patient").setEphemeral(true).queue();
            return;
        }
        event.reply("...").queue(m -> m.deleteOriginal().queueAfter(1, TimeUnit.MILLISECONDS));
        userid = event.getUser().getIdLong();
        questionNumber = 0;
        event.getChannel().sendMessageEmbeds(CONFIRM_QUESTION).setActionRow(
                Button.secondary("q0_1", Emoji.fromUnicode("✅")),
                Button.secondary("q0_2", Emoji.fromMarkdown("<:redtick:785223937495531520>"))
        ).queue(m -> deleteIfNotNull(m, 120));
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if(event.getUser().getIdLong() != userid && event.getComponentId().startsWith("q")) {
            event.reply("A request is already in progress, please be patient").setEphemeral(true).queue();
            return;
        }
        switch (event.getComponentId()) {
            case "q0_1" -> {
                event.deferEdit().queue();
                event.getHook().deleteOriginal().queue();
                questionNumber = 1;
                event.getChannel().sendMessageEmbeds(RANK_QUESTION).setActionRow(
                        Button.secondary("q1_1", "Learner"),
                        Button.secondary("q1_2", "Teacher"),
                        Button.secondary("q1_3", "Parent"),
                        Button.secondary("q1_4", "Thespian")
                ).queue(m -> deleteIfNotNull(m, 120));
            }
            case "q0_2" -> {
                questionNumber = -1;
                event.deferEdit().queue();
                event.getHook().deleteOriginal().queue();
            }
            case "q1_1", "q1_2", "q1_3", "q1_4" -> {
                questionNumber = 2;
                event.deferEdit().queue();
                role = event.getComponent().getLabel();
                event.getChannel().sendMessageEmbeds(NAME_QUESTION).queue(m -> deleteIfNotNull(m, 120));
                event.getHook().deleteOriginal().queue();
            }
            case "q4_1", "q4_2", "q4_3" -> {
                questionNumber = 5;
                event.deferEdit().queue();
                answer4 = event.getComponent().getLabel();
                sendInfoConfirmation(event.getChannel());
                event.getHook().deleteOriginal().queue();
            }
            case "q5_1" -> {
                questionNumber = -1;
                event.deferEdit().queue();
                event.getHook().deleteOriginal().queue();
                clearChannel(event.getTextChannel());
                event.getChannel().sendMessageEmbeds(END_QUESTION).queue(m -> deleteIfNotNull(m, 120));
                if(event.getUser().getIdLong() != userid) return;
                User user = event.getUser();
                EmbedBuilder embedBuilder = new EmbedBuilder()
                        .setAuthor(user.getName() + " made a request to come in.")
                        .setColor(Color.BLUE)
                        .addField(new MessageEmbed.Field("Rank: ", role, false))
                        .addField(new MessageEmbed.Field("Name: ", name, false))
                        .addField(new MessageEmbed.Field(role == "Learner" ? "Parent Email: " : "Email: ", email, false))
                        .setFooter(user.getName(), user.getEffectiveAvatarUrl());
                if(!role.equals("Thespian")) {
                    embedBuilder.addField(new MessageEmbed.Field(switch (role) {
                        case "Learner" -> "Age Group: ";
                        case "Teacher" -> "Job at ALPNCP: ";
                        case "Parent" -> "Child's name(s): ";
                        default -> "Unknown: ";
                    }, answer4, false));
                }
                event.getGuild().getTextChannelsByName("request-log", true).get(0).sendMessageEmbeds(embedBuilder.build()).queue();

            }
            case "q5_2" -> {
                questionNumber = -1;
                event.deferEdit().queue();
                clearChannel(event.getTextChannel());
                event.getChannel().sendMessageEmbeds(new EmbedBuilder()
                        .setAuthor("Expires in 1 minute")
                        .setColor(Color.RED)
                        .setDescription("Request Cancelled, you can feel free to request again with /request.").build()
                ).queue(m -> deleteIfNotNull(m, 120));
            }
        }
    }
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if(!event.getChannel().getName().equals("rr-1")) return;
        if(event.getMessage().getContentStripped().equalsIgnoreCase("/request") && questionNumber == -1) {
            userid = event.getMessage().getAuthor().getIdLong();
            questionNumber = 0;
            event.getChannel().sendMessageEmbeds(CONFIRM_QUESTION).setActionRow(
                    Button.secondary("q0_1", Emoji.fromUnicode("✅")),
                    Button.secondary("q0_2", Emoji.fromMarkdown("<:redtick:785223937495531520>"))
            ).queue(m -> deleteIfNotNull(m, 120));
        }
        if(event.getAuthor().getIdLong() != userid) return;
        switch (questionNumber) {
            case 2 -> {
                questionNumber = 3;
                name = event.getMessage().getContentStripped();
                event.getChannel().sendMessageEmbeds(role.equals("Learner") ? PARENT_EMAIL_QUESTION : EMAIL_QUESTION).queue(m -> m.delete().queueAfter(300, TimeUnit.SECONDS, m2 -> questionNumber = -1, f -> Cameron.logger.info("Message already deleted.")));
            }
            case 3 -> {
                questionNumber = 4;
                email = event.getMessage().getContentStripped();
                switch (role) {
                    case "Teacher" -> event.getChannel().sendMessageEmbeds(ALP_JOB_QUESTION).queue(m -> deleteIfNotNull(m, 120));
                    case "Parent" -> event.getChannel().sendMessageEmbeds(CHILD_NAMES_QUESTION).queue(m -> deleteIfNotNull(m, 120));
                    case "Learner" -> event.getChannel().sendMessageEmbeds(AGE_GROUP_QUESTION)
                            .setActionRow(
                                    Button.secondary("q4_1", "9-11"),
                                    Button.secondary("q4_2", "12-13"),
                                    Button.secondary("q4_3", "14+")
                            ).queue(m -> deleteIfNotNull(m, 120));
                    default -> {
                        questionNumber = 5;
                        sendInfoConfirmation(event.getChannel());
                    }
                }
            }
            case 4 -> {
                if(role.equals("Learner") || role.equals("Thespian")) return;
                questionNumber = 5;
                answer4 = event.getMessage().getContentStripped();
                sendInfoConfirmation(event.getChannel());
            }
        }
    }

    private void clearChannel(TextChannel channel) {
        for (Message msg : channel.getIterableHistory())
            msg.delete().queue(s -> {}, e -> Cameron.logger.error("Queue returned failure"));
    }
    
    private void deleteIfNotNull(Message m, int delay) {
        if(m != null)
            m.delete().queueAfter(delay, TimeUnit.SECONDS, s -> questionNumber = -1, f -> Cameron.logger.info("Message already deleted."));
    }
*/
}
