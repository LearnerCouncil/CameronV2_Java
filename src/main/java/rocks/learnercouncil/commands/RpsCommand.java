package rocks.learnercouncil.commands;

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
        if(event.getName().equals("rps")) {
            event.replyEmbeds(new EmbedBuilder()
                            .setAuthor("Rock, Paper, Scissors")
                            .setColor(Color.WHITE)
                            .setDescription("Click \uD83D\uDDFB, \uD83D\uDCF0 , or ✂️to play!")
                            .build()
            ).setEphemeral(true).addActionRow(
                    Button.secondary("rps_rock", Emoji.fromUnicode("\uD83D\uDDFB")),
                    Button.secondary("rps_paper", Emoji.fromUnicode("\uD83D\uDCF0")),
                    Button.secondary("rps_scissors", Emoji.fromUnicode("✂️"))
            ).queue();
        }
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        byte userChoice, cpuChoice;
        cpuChoice = (byte) new Random().nextInt(3);
        if(event.getComponentId().startsWith("rps_")) {
            switch (event.getComponentId()) {
                case "rps_rock" -> userChoice = 0;
                case "rps_paper" -> userChoice = 1;
                case "rps_scissors" -> userChoice = 2;
                default -> userChoice = 10;
            }
            event.editMessageEmbeds(evalutateAnswers(userChoice, cpuChoice, event.getJDA())).setActionRows().queue();
        }

        //user-cpu
        //   0  1  2
        //0  0  1  2
        //1 -1  0  1
        //2 -2 -1  0
    }
    private MessageEmbed evalutateAnswers(byte u, byte c, JDA jda) {
        byte e = (byte) (u-c);
        String userChoice = switch (u) {
            case 0 -> "\uD83D\uDDFB";
            case 1 -> "\uD83D\uDCF0";
            case 2 -> "✂️";
            default -> "❓";
        };
        String cpuChoice = switch (c) {
            case 0 -> "\uD83D\uDDFB";
            case 1 -> "\uD83D\uDCF0";
            case 2 -> "✂️";
            default -> "❓";
        };

        EmbedBuilder embed = new EmbedBuilder()
                .setDescription(userChoice + " vs " + cpuChoice)
                .setFooter(jda.getSelfUser().getName(), jda.getSelfUser().getAvatarUrl());
        switch (e) {
            case 0 -> embed.setAuthor("It's a tie!").setColor(Color.WHITE);
            case 1,-2 -> embed.setAuthor("You win!").setColor(Color.GREEN);
            case -1,2 -> embed.setAuthor("You lose!").setColor(Color.RED);
            default -> embed.setAuthor("I'm confused... who won?'").setColor(Color.RED);
        }
        return embed.build();
    }
}
