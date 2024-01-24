package rocks.learnercouncil.cameron.commands.request;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import rocks.learnercouncil.cameron.Cameron;
import rocks.learnercouncil.cameron.commands.request.questions.*;

import java.awt.Color;
import java.time.Duration;

public class Questions {

    private static final MessageEmbed END_EMBED = new EmbedBuilder()
            .setAuthor("Expires in 1 minute")
            .setColor(Color.GREEN)
            .setDescription("You're all set. We will let you know when you've been accepted.")
            .build();
    private static final EndQuestion END = new EndQuestion(END_EMBED,
            Duration.ofMinutes(1));

    private static Question infoConfirmation(Request request) {
        EmbedBuilder embed = new EmbedBuilder()
                .setAuthor("Expires in 1 minute")
                .setColor(Color.GREEN)
                .setDescription("This is the information we received, is this correct?");
        request.addAnswers(embed);
        BooleanQuestion question = new BooleanQuestion(embed.build(),
                Duration.ofMinutes(1),
                r -> END);
        Cameron.getJDA().addEventListener(question);
        return question;
    }

    private static final MessageEmbed JOB_EMBED = new EmbedBuilder()
            .setAuthor("Expires in 2 minute")
            .setColor(Color.CYAN)
            .setDescription("Please type your job at ALP Nature Center and Perserve")
            .build();
    private static final MessageQuestion JOB = new MessageQuestion(JOB_EMBED,
            Duration.ofMinutes(2),
            Questions::infoConfirmation, Answer.JOB);

    private static final MessageEmbed CHILD_NAMES_EMBED = new EmbedBuilder()
            .setAuthor("Expires in 2 minute")
            .setColor(Color.CYAN)
            .setDescription("Please type your Child's name(s)")
            .build();
    private static final MessageQuestion CHILD_NAMES = new MessageQuestion(CHILD_NAMES_EMBED,
            Duration.ofMinutes(2),
            Questions::infoConfirmation, Answer.CHILD_NAMES);

    private static final MessageEmbed AGE_GROUP_EMBED = new EmbedBuilder()
            .setAuthor("Expires in 1 minute")
            .setColor(Color.CYAN)
            .setDescription("What is your age group?")
            .build();
    private static final ButtonQuestion AGE_GROUP = new ButtonQuestion(AGE_GROUP_EMBED,
            Duration.ofMinutes(1),
            Questions::infoConfirmation, Answer.AGE_GROUP,
            Button.secondary("request_9-11Button", "9-11"),
            Button.secondary("request_12-13Button", "12-13"),
            Button.secondary("request_14+Button", "14+"));

    private static final MessageEmbed EMAIL_EMBED = new EmbedBuilder()
            .setAuthor("Expires in 5 minutes")
            .setColor(Color.CYAN)
            .setDescription("Please type your email address.")
            .build();
    private static final MessageQuestion EMAIL = new MessageQuestion(EMAIL_EMBED,
            Duration.ofMinutes(5),
            r -> switch (r.getAnswer(Answer.RANK)) {
                case "Teacher" -> JOB;
                case "Parent" -> CHILD_NAMES;
                default -> infoConfirmation(r);
            },
            Answer.EMAIL);

    private static final MessageEmbed PARENT_EMAIL_EMBED = new EmbedBuilder()
            .setAuthor("Expires in 5 minutes")
            .setColor(Color.CYAN)
            .setDescription("Please type your parents email address.")
            .build();
    private static final MessageQuestion PARENT_EMAIL = new MessageQuestion(PARENT_EMAIL_EMBED,
            Duration.ofMinutes(5),
            r -> AGE_GROUP,
            Answer.PARENT_EMAIL);

    private static final MessageEmbed NAME_EMBED = new EmbedBuilder()
            .setAuthor("Expires in 2 minutes")
            .setColor(Color.CYAN)
            .setDescription("Please type your first and last name.")
            .build();
    private static final MessageQuestion NAME = new MessageQuestion(NAME_EMBED,
            Duration.ofMinutes(2),
            r -> (r.getAnswer(Answer.RANK).equals("Learner") ? PARENT_EMAIL : EMAIL), Answer.NAME);

    private static final MessageEmbed RANK_EMBED = new EmbedBuilder()
            .setAuthor("Expires in 1 minute")
            .setColor(Color.GREEN)
            .setDescription("What is your rank?")
            .build();
    private static final ButtonQuestion RANK = new ButtonQuestion(RANK_EMBED,
            Duration.ofMinutes(1),
            r -> NAME, Answer.RANK,
            Button.secondary("request_learnerButton", "Learner"),
            Button.secondary("request_teacherButton", "Teacher"),
            Button.secondary("request_parentButton", "Parent"),
            Button.secondary("request_thespianButton", "Thespian"));


    private static final MessageEmbed CONFIRM_EMBED = new EmbedBuilder()
            .setAuthor("Expires in 1 minute")
            .setColor(Color.GREEN)
            .setDescription("You are about to request access to the Learner Server, do you wish to proceed?")
            .build();
    public static BooleanQuestion CONFIRM = new BooleanQuestion(CONFIRM_EMBED,
            Duration.ofMinutes(1),
            r -> RANK);
    
    public static EventListener[] getListeners() {
        return new EventListener[] {
                CONFIRM,
                RANK,
                NAME,
                PARENT_EMAIL,
                EMAIL,
                AGE_GROUP,
                CHILD_NAMES,
                JOB,
        };
    }
}
