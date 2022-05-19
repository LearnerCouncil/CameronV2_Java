package rocks.learnercouncil.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.util.concurrent.TimeUnit;

public class RequestCommand extends ListenerAdapter {


    private static final MessageEmbed QUESTION_0 = new EmbedBuilder()
            .setAuthor("Expires in 1 minute")
            .setColor(Color.GREEN)
            .setDescription("You are about to request access to the Learner Server, do you wish to proceed?").build();
    private static final MessageEmbed QUESTION_1 = new EmbedBuilder()
            .setAuthor("Expires in 1 minute")
            .setColor(Color.GREEN)
            .setDescription("What is your rank?").build();
    private static final MessageEmbed QUESTION_2 = new EmbedBuilder()
            .setAuthor("Expires in 2 minutes")
            .setColor(Color.CYAN)
            .setDescription("Please type your first and last name.").build();
    private static final MessageEmbed QUESTION_3A = new EmbedBuilder()
            .setAuthor("Expires in 5 minutes")
            .setColor(Color.CYAN)
            .setDescription("Please type your parents email address for Village Home.").build();
    private static final MessageEmbed QUESTION_3B = new EmbedBuilder()
            .setAuthor("Expires in 5 minutes")
            .setColor(Color.CYAN)
            .setDescription("Please type your email address for Village Home.").build();
    private static final MessageEmbed QUESTION_4A = new EmbedBuilder()
            .setAuthor("Expires in 1 minute")
            .setColor(Color.CYAN)
            .setDescription("What is your age group?").build();
    private static final MessageEmbed QUESTION_4B = new EmbedBuilder()
            .setAuthor("Expires in 2 minute")
            .setColor(Color.CYAN)
            .setDescription("Please type your Child's name(s)").build();
    private static final MessageEmbed QUESTION_4C = new EmbedBuilder()
            .setAuthor("Expires in 2 minute")
            .setColor(Color.CYAN)
            .setDescription("Please type your job at Village Home").build();
    private static final MessageEmbed QUESTION_END = new EmbedBuilder()
            .setAuthor("Expires in 1 minute")
            .setColor(Color.GREEN)
            .setDescription("You're all set. We will let you know when you've been accepted.").build();


    private String role, name, email, answer_4;

    private int questionNumber = -1;
    private long userid = 0;

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if(event.getName().equals("request")) {
            if (event.getChannel().getName().equals("rr-1")) {
                if (questionNumber == -1) {
                    event.reply("...").queue(m -> m.deleteOriginal().queueAfter(1, TimeUnit.MILLISECONDS));
                    userid = event.getUser().getIdLong();
                    questionNumber = 0;
                    event.getChannel().sendMessageEmbeds(QUESTION_0).setActionRow(
                            Button.secondary("q0_1", Emoji.fromMarkdown("<:greentick:976577931642040370>")),
                            Button.secondary("q0_2", Emoji.fromMarkdown("<:redtick:976578391434203207>"))
                    ).queue(m -> m.delete().queueAfter(120, TimeUnit.SECONDS, m2 -> questionNumber = -1));
                } else
                    event.reply("A request is already in progress, please be patient").setEphemeral(true).queue();
            } else
                event.reply("Can't do that here").setEphemeral(true).queue();
        }
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        switch (event.getComponentId()) {
            case "q0_1" -> {
                event.deferEdit().queue();
                event.getHook().deleteOriginal().queue();
                questionNumber = 1;
                event.getChannel().sendMessageEmbeds(QUESTION_1).setActionRow(
                        Button.secondary("q1_1", "Learner"),
                        Button.secondary("q1_2", "Teacher"),
                        Button.secondary("q1_3", "Parent")
                ).queue(m -> m.delete().queueAfter(120, TimeUnit.SECONDS, m2 -> questionNumber = -1));
            }
            case "q0_2" -> {
                questionNumber = -1;
                event.deferEdit().queue();
                event.getHook().deleteOriginal().queue();
            }
            case "q1_1", "q1_2", "q1_3" -> {
                questionNumber = 2;
                event.deferEdit().queue();
                role = event.getComponent().getLabel();
                event.getChannel().sendMessageEmbeds(QUESTION_2).queue(m -> m.delete().queueAfter(120, TimeUnit.SECONDS, m2 -> questionNumber = -1));
                event.getHook().deleteOriginal().queue();
            }
            case "q4_1", "q4_2", "q4_3" -> {
                questionNumber = 5;
                event.deferEdit().queue();
                answer_4 = event.getComponent().getLabel();
                event.getChannel().sendMessageEmbeds(new EmbedBuilder()
                                .setAuthor("Expires in 1 minute")
                                .setColor(Color.GREEN)
                                .setDescription("This is the information we recieved, is this correct?")
                                .addField(new MessageEmbed.Field("Rank: ", role, false))
                                .addField(new MessageEmbed.Field("Name: ", name, false))
                                .addField(new MessageEmbed.Field("email", "Parent Email: " + email, false))
                                .addField(new MessageEmbed.Field("Age Group: ", answer_4, false)).build())
                        .setActionRow(
                                Button.secondary("q5_1", Emoji.fromMarkdown("<:greentick:976577931642040370>")),
                                Button.secondary("q5_2", Emoji.fromMarkdown("<:redtick:976578391434203207>"))
                        ).queue(m -> m.delete().queueAfter(120, TimeUnit.SECONDS, m2 -> questionNumber = -1));
                event.getHook().deleteOriginal().queue();
            }
            case "q5_1" -> {
                questionNumber = -1;
                event.deferEdit().queue();
                event.getHook().deleteOriginal().queue();
                clearChannel(event.getTextChannel());
                event.getChannel().sendMessageEmbeds(QUESTION_END).queue(m -> m.delete().queueAfter(120, TimeUnit.SECONDS, m2 -> questionNumber = -1));
                if(event.getUser().getIdLong() == userid) {
                    User user = event.getJDA().getUserById(userid);
                    assert user != null;
                    if (event.getGuild() != null)
                        event.getGuild().getTextChannelsByName("request-log", true).get(0).sendMessageEmbeds(
                                new EmbedBuilder()
                                        .setAuthor(user.getName() + " made a request to come in.")
                                        .setColor(Color.BLUE)
                                        .addField(new MessageEmbed.Field("Rank: ", role, false))
                                        .addField(new MessageEmbed.Field("Name: ", name, false))
                                        .addField(new MessageEmbed.Field(role == "Learner" ? "Parent Email: " : "Email: ", email, false))
                                        .addField(new MessageEmbed.Field(switch (role) {
                                            case "Learner" -> "Age Group: ";
                                            case "Teacher" -> "Job at Village Home: ";
                                            case "Parent" -> "Child's name(s): ";
                                            default -> "Unknown: ";
                                        }, answer_4, false))
                                        .setFooter(user.getName(), user.getAvatarUrl()).build()
                        ).queue(m -> m.delete().queueAfter(120, TimeUnit.SECONDS, m2 -> questionNumber = -1));
                }
            }
            case "q5_2" -> {
                questionNumber = -1;
                event.deferEdit().queue();
                clearChannel(event.getTextChannel());
                event.getChannel().sendMessageEmbeds(new EmbedBuilder()
                        .setAuthor("Expires in 1 minute")
                        .setColor(Color.RED)
                        .setDescription("Request Cancelled, you can feel free to request again with /request.").build()
                ).queue(m -> m.delete().queueAfter(120, TimeUnit.SECONDS, m2 -> questionNumber = -1));
            }
        }
    }
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if(event.getChannel().getName().equals("rr-1") && event.getAuthor().getIdLong() == userid) {
            switch (questionNumber) {
                case 2 -> {
                    questionNumber = 3;
                    name = event.getMessage().getContentStripped();
                    switch (role) {
                        case "Parent", "Teacher" -> event.getChannel().sendMessageEmbeds(QUESTION_3B).queue(m -> m.delete().queueAfter(300, TimeUnit.SECONDS, m2 -> questionNumber = -1));
                        default -> event.getChannel().sendMessageEmbeds(QUESTION_3A).queue(m -> m.delete().queueAfter(300, TimeUnit.SECONDS, m2 -> questionNumber = -1));
                    }

                }
                case 3 -> {
                    questionNumber = 4;
                    email = event.getMessage().getContentStripped();
                    switch (role) {
                        case "Teacher" -> event.getChannel().sendMessageEmbeds(QUESTION_4C).queue(m -> m.delete().queueAfter(120, TimeUnit.SECONDS, m2 -> questionNumber = -1));
                        case "Parent" -> event.getChannel().sendMessageEmbeds(QUESTION_4B).queue(m -> m.delete().queueAfter(120, TimeUnit.SECONDS, m2 -> questionNumber = -1));
                        default -> event.getChannel().sendMessageEmbeds(QUESTION_4A)
                                .setActionRow(
                                        Button.secondary("q4_1", "6-9"),
                                        Button.secondary("q4_2", "10-14"),
                                        Button.secondary("q4_3", "15-18")
                                ).queue(m -> m.delete().queueAfter(120, TimeUnit.SECONDS, m2 -> questionNumber = -1));
                    }
                }
                case 4 -> {
                    if(role.equals("Learner")) return;
                    questionNumber = 5;
                    answer_4 = event.getMessage().getContentStripped();
                    event.getChannel().sendMessageEmbeds(new EmbedBuilder()
                                    .setAuthor("Expires in 1 minute")
                                    .setColor(Color.GREEN)
                                    .setDescription("This is the information we recieved, is this correct?")
                                    .addField(new MessageEmbed.Field("Role:  ", role, false))
                                    .addField(new MessageEmbed.Field("Name: ", name, false))
                                    .addField(new MessageEmbed.Field("Email: ", email, false))
                                    .addField(new MessageEmbed.Field(switch (role) {
                                        case "Teacher" -> "Job at Village Home: ";
                                        case "Parent" -> "Child's name(s): ";
                                        default -> "Unknown: ";
                                    }, answer_4, false)).build())
                            .setActionRow(
                                    Button.secondary("q5_1", Emoji.fromMarkdown("<:greentick:976577931642040370>")),
                                    Button.secondary("q5_2", Emoji.fromMarkdown("<:redtick:976578391434203207>"))
                            ).queue(m -> m.delete().queueAfter(120, TimeUnit.SECONDS, m2 -> questionNumber = -1));
                }
            }
        }
    }

    public void clearChannel(TextChannel channel) {
        for (Message msg : channel.getIterableHistory())
            msg.delete().queue();
    }

}
