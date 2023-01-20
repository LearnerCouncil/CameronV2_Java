package rocks.learnercouncil.cameron.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Random;

public class RpsCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if(!event.getName().equals("rps")) return;
        if(!event.getChannel().getName().equals("rock-paper-scissors")) {
            event.reply("Can't do that here.").setEphemeral(true).queue();
            return;
        }
        event.replyEmbeds(new EmbedBuilder()
                        .setAuthor("Rock, Paper, Scissors")
                        .setColor(Color.WHITE)
                        .setDescription("Click \uD83D\uDDFB, \uD83D\uDCF0 , or ✂️to play!")
                        .build()
        ).addActionRow(
                Button.secondary("rps_rock_" + event.getUser().getId(), Emoji.fromUnicode("\uD83D\uDDFB")),
                Button.secondary("rps_paper_" + event.getUser().getId(), Emoji.fromUnicode("\uD83D\uDCF0")),
                Button.secondary("rps_scissors_" + event.getUser().getId(), Emoji.fromUnicode("✂️"))
        ).queue();
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if(event.getComponentId().startsWith("rps_")) {
            String[] components = event.getComponentId().split("_");
            if(components.length != 3) return;
            if(!event.getUser().getId().equals(components[2])) {
                event.reply("This isn't your game. You can stary your own with **/rps**.").setEphemeral(true).queue();
                return;
            }

            int userChoice, cpuChoice;
            cpuChoice = new Random().nextInt(3);
            switch (components[1]) {
                case "rock" -> userChoice = 0;
                case "paper" -> userChoice = 1;
                case "scissors" -> userChoice = 2;
                default -> userChoice = 10;
            }
            event.editMessageEmbeds(evaluateAnswers(userChoice, cpuChoice, event.getJDA())).setActionRows().queue();
        }
    }
    private MessageEmbed evaluateAnswers(int userChoiceInt, int cpuChoiceInt, JDA jda) {
        int evaluated = userChoiceInt-cpuChoiceInt;
        String userChoice = switch (userChoiceInt) {
            case 0 -> "\uD83D\uDDFB";
            case 1 -> "\uD83D\uDCF0";
            case 2 -> "✂️";
            default -> "❓";
        };
        String cpuChoice = switch (cpuChoiceInt) {
            case 0 -> "\uD83D\uDDFB";
            case 1 -> "\uD83D\uDCF0";
            case 2 -> "✂️";
            default -> "❓";
        };

        EmbedBuilder embed = new EmbedBuilder()
                .setDescription(userChoice + " vs " + cpuChoice)
                .setFooter(jda.getSelfUser().getName(), jda.getSelfUser().getAvatarUrl());
        switch (evaluated) {
            case 0 -> embed.setAuthor("It's a tie!").setColor(Color.WHITE);
            case 1,-2 -> embed.setAuthor("You win!").setColor(Color.GREEN);
            case -1,2 -> embed.setAuthor("You lose!").setColor(Color.RED);
            default -> embed.setAuthor("I'm confused... who won?'").setColor(Color.RED);
        }
        return embed.build();
    }
}
