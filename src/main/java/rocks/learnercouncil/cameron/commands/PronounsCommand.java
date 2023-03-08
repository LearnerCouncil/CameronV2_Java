package rocks.learnercouncil.cameron.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import org.jetbrains.annotations.NotNull;
import rocks.learnercouncil.cameron.Cameron;
import rocks.learnercouncil.cameron.DynamicRoleList;

import java.awt.Color;
import java.util.*;

public class PronounsCommand extends ListenerAdapter {

    private static final String PREFIX = "\u200B";
    private static final MessageEmbed PRONOUN_EMBED = new EmbedBuilder()
            .setColor(Color.GREEN)
            .setAuthor("Set your pronouns! (Select all that apply.)")
            .setDescription("Choose from the list below.\n(If your pronouns are not on this list please @The Council with your \npronouns and someone will add them to the list.)")
            .build();
    public static DynamicRoleList roleList;
    private static ActionRow[] buttonRows;

    public static void initialize(@NotNull Guild guild) {
        roleList = new DynamicRoleList(guild, "--------------  Pronouns --------------", Cameron.getExistingChannel("pronouns"));
    }


    @SuppressWarnings("ConstantConditions")
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if(!event.getName().equals("pronouns")) return;
        if(!event.isFromGuild()) {
            event.reply("This command must be run from inside a server").queue();
            return;
        }
        event.deferReply().setEphemeral(true).queue();
        buttonRows = roleList.getButtons(event.getMember());
        event.getHook().sendMessageEmbeds(PRONOUN_EMBED).addActionRows(buttonRows).setEphemeral(true).queue();
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if(!event.getComponentId().startsWith("pncb_")) return;
        Guild guild = event.getGuild();
        if(guild == null) return;
        if(event.getMember() == null) {
            Cameron.logger.error(event.getUser() + " is not in a guild!");
            return;
        }

        for(ActionRow row : buttonRows) {
            Optional<Button> buttonOptional = row.getButtons().stream().filter(b -> b.getId().equals(event.getComponentId())).findAny();
            if(buttonOptional.isEmpty()) continue;
            Button button = buttonOptional.get();

            if(button.getStyle() == ButtonStyle.PRIMARY) {
                row.updateComponent(button, event.getComponent().withStyle(ButtonStyle.SECONDARY));
                guild.removeRoleFromMember(event.getMember(), roleList.get(event.getComponent().getLabel())).queue(r -> roleList.updateIndicator(event.getMember()));
            } else {
                row.updateComponent(button, event.getComponent().withStyle(ButtonStyle.PRIMARY));
                guild.addRoleToMember(event.getMember(), roleList.get(event.getComponent().getLabel())).queue(r -> roleList.updateIndicator(event.getMember()));
            }
        }
        event.deferEdit().setActionRows(buttonRows).queue();
    }
}
