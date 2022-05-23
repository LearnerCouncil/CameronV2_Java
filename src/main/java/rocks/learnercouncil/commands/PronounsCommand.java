package rocks.learnercouncil.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import org.jetbrains.annotations.NotNull;
import rocks.learnercouncil.Cameron;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class PronounsCommand extends ListenerAdapter {

    private static final String PREFIX = "\u200B";
    private static final List<Role> pronounRoles = new ArrayList<>();
    private static ActionRow[] buttonRows;

    public static void initializeRoles() {
        pronounRoles.clear();
        Guild guild = Cameron.getJDA().getGuildById(Cameron.GUILD_ID);
        if(guild != null) {
           Cameron.getExistingChannel("pronouns").getIterableHistory().forEach(m -> {
               if(guild.getRolesByName(PREFIX + m.getContentStripped(), true).isEmpty()) {
                    addPronounRole(guild, PREFIX + m.getContentStripped());
               }
           });
            guild.getRoles().stream().filter(r -> r.getName().startsWith(PREFIX)).forEach(pronounRoles::add);
        }
    }

    public static void addPronounRole(Guild guild, String name) {
        if(guild.getRolesByName(PREFIX + name, true).isEmpty())
        guild.createRole().setName(PREFIX + name).queue(pronounRoles::add);
    }
    public static void removePronounRole(String name) {
        Cameron.logger.error("Deleting Role: " + name);
        pronounRoles.remove(Cameron.getExistingRole(PREFIX + name));
        Cameron.getExistingRole(PREFIX + name).delete().queue();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if(event.getName().equals("pronouns")) {
            if(!event.isFromGuild()) event.reply("This command must be run from inside a server").queue();
            MessageEmbed embed = new EmbedBuilder()
                    .setColor(Color.GREEN)
                    .setAuthor("Set your pronouns!")
                    .setDescription("Choose from the list below.\n(If your pronouns are not on this list please @The Council with your \npronouns and someone will add them to the list)"
                    ).build();
            Button[] buttons = new Button[pronounRoles.size()];
            for(int i = 0; i < pronounRoles.size(); i++) {
                if (event.getMember().getRoles().contains(pronounRoles.get(i)))
                    buttons[i] = Button.primary("pncb_" + pronounRoles.get(i).getName(), pronounRoles.get(i).getName());
                buttons[i] = Button.secondary("pncb_" + pronounRoles.get(i).getName(), pronounRoles.get(i).getName());
            }
            Button[][] rows = splitArray(buttons);
            buttonRows = new ActionRow[rows.length];
            for (int i = 0; i < buttonRows.length; i++) {
                buttonRows[i] = ActionRow.of(rows[i]);
            }

            event.replyEmbeds(embed).addActionRows(buttonRows).setEphemeral(true).queue();
        }
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if(event.getComponentId().startsWith("pncb_")) {
            Guild guild = event.getJDA().getGuildById(Cameron.GUILD_ID);
            assert guild != null;
            if(event.getMember() != null) {
                for(ActionRow a : buttonRows) {
                    for(Button b : a.getButtons()) {
                        if(Objects.equals(b.getId(), event.getComponentId())) {
                            if(b.getStyle() == ButtonStyle.PRIMARY) {
                                a.updateComponent(b, event.getComponent().withStyle(ButtonStyle.SECONDARY));
                                guild.removeRoleFromMember(event.getMember(), Cameron.getExistingRole(event.getComponent().getLabel())).queue();
                            } else {
                                a.updateComponent(b, event.getComponent().withStyle(ButtonStyle.PRIMARY));
                                guild.addRoleToMember(event.getMember(), Cameron.getExistingRole(event.getComponent().getLabel())).queue();
                            }
                            event.deferEdit().setActionRows(buttonRows).queue();
                        }
                    }
                }
            } else {
                Cameron.logger.error(event.getUser() + " is not in a guild!");
            }
        }
    }

    private Button[][] splitArray(Button[] input) {
        Button[][] result = new Button[(int)Math.ceil((double) input.length/ 5)][];
        for(int i = 0; i < input.length; i += 5) {
            result[i] = Arrays.copyOfRange(input, i, Math.min(input.length, i+ 5));
        }
        return result;
    }
}
