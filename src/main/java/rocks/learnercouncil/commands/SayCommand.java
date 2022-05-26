package rocks.learnercouncil.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import org.jetbrains.annotations.NotNull;
import rocks.learnercouncil.Cameron;

import java.awt.Color;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SayCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if(event.getName().equals("say")) {
            if(event.getOption("message") == null) return;
            event.reply("Message Sent!").setEphemeral(true).queue();
            //noinspection ConstantConditions
            event.getChannel().sendMessage(event.getOption("message").getAsString()).queue();
        }

        if(event.getName().equals("sayembed")) {
            if(event.getOption("color") == null) return;
            //noinspection ConstantConditions
            String sColor = event.getOption("color").getAsString().toLowerCase();
            Color color = switch (sColor.toLowerCase()) {
                case "black" -> Color.BLACK;
                case "blue" -> Color.BLUE;
                case "cyan" -> Color.CYAN;
                case "dark_dray", "darkgray", "dark gray" -> Color.DARK_GRAY;
                case "gray" -> Color.GRAY;
                case "green" -> Color.GREEN;
                case "light_gray", "lightgray", "light gray" -> Color.LIGHT_GRAY;
                case "magenta" -> Color.MAGENTA;
                case "orange" -> Color.ORANGE;
                case "pink" -> Color.PINK;
                case "red" -> Color.RED;
                case "white" -> Color.WHITE;
                case "yellow" -> Color.YELLOW;
                default -> null;
            };
            if(color == null) {
                try {
                    color = Color.decode(sColor);
                } catch (NumberFormatException e) {
                    event.reply("Invalid color").setEphemeral(true).queue();
                }
            }
            if (event.getOption("title") == null || event.getOption("message") == null) return;
            event.reply("Message Sent!").setEphemeral(true).queue();
            //noinspection ConstantConditions
            event.getChannel().sendMessageEmbeds(new EmbedBuilder()
                    .setAuthor(event.getOption("title").getAsString())
                    .setDescription(event.getOption("message").getAsString())
                    .setColor(color)
                    .build()
            ).queue();
        }
    }

    @Override
    public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
        if(event.getName().equals("sayembed") && event.getFocusedOption().getName().equals("color")) {
            String[] arguments = new String[]{"black", "blue", "cyan", "dark gray", "gray", "green", "light gray", "magenta", "orange", "pink", "red", "white", "yellow"};
            List<Command.Choice> completions = Stream.of(arguments)
                    .filter(a -> a.startsWith(event.getFocusedOption().getValue()))
                    .map(a -> new Command.Choice(a, a))
                    .collect(Collectors.toList());
            event.replyChoices(completions).queue();
        }
    }
}
