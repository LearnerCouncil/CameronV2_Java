package rocks.learnercouncil.cameron;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

import java.util.*;

public class DynamicRoleList {

    private final List<Role> roles = new LinkedList<>();
    private final Guild guild;
    private final Role indicatorRole;
    public final TextChannel channel;

    public DynamicRoleList(Guild guild, String indicatorRole, TextChannel channel) {
        this.guild = guild;
        this.indicatorRole = Cameron.getExistingRole(indicatorRole);
        this.channel = channel;
        this.channel.getIterableHistory().forEach(message -> {
            String[] lines = message.getContentRaw().split("\n");
            for(String line : lines) {
                add(line);
            }
        });
    }

    public void add(String name) {
        List<Role> rolesWithName = guild.getRolesByName(name, false);
        if(rolesWithName.isEmpty())
            guild.createRole().setName(name).queue(roles::add);
        else
            roles.add(rolesWithName.get(0));
    }

    public void remove(String name) {
        roles.remove(get(name));
    }

    public Role get(String name) {
        return roles.stream().filter(r -> r.getName().equals(name)).findAny().orElseThrow(() -> new IllegalArgumentException("No role called '" + name + "' exists in this list."));
    }

    public void updateIndicator(Member member) {
        if(member.getRoles().stream().anyMatch(roles::contains)) {
            guild.addRoleToMember(member, indicatorRole).queue();
        } else {
            guild.removeRoleFromMember(member, indicatorRole).queue();
        }
    }

    public ActionRow[] getButtons(Member member, String prefix) {
        ActionRow[] result;

        Button[] buttons = new Button[roles.size()];
        for(int i = 0; i < buttons.length; i++) {
            Role role = roles.get(i);
            if(member.getRoles().contains(role))
                buttons[i] = Button.primary(prefix + "_rlb_" + role.getName(), role.getName());
            else
                buttons[i] = Button.secondary(prefix + "_rlb_" + role.getName(), role.getName());
        }
        Button[][] rows = splitArray(buttons);
        result = new ActionRow[rows.length];

        for (int i = 0; i < result.length; i++) {
            if(rows[i] != null) {
                result[i] = ActionRow.of(rows[i]);
            }
        }
        return result;
    }

    public void updateButtons(ActionRow[] buttonRows, ButtonInteractionEvent event) {
        for(ActionRow row : buttonRows) {
            Optional<Button> buttonOptional = row.getButtons().stream().filter(b -> Objects.equals(b.getId(), event.getComponentId())).findAny();
            if(buttonOptional.isEmpty()) continue;
            Button button = buttonOptional.get();
            Member member = Objects.requireNonNull(event.getMember());

            if(button.getStyle() == ButtonStyle.PRIMARY) {
                row.updateComponent(button, event.getComponent().withStyle(ButtonStyle.SECONDARY));
                guild.removeRoleFromMember(member, get(event.getComponent().getLabel())).queue(r -> updateIndicator(event.getMember()));
            } else {
                row.updateComponent(button, event.getComponent().withStyle(ButtonStyle.PRIMARY));
                guild.addRoleToMember(member, get(event.getComponent().getLabel())).queue(r -> updateIndicator(event.getMember()));
            }
        }
    }

    private Button[][] splitArray(Button[] input) {
        Button[][] result = new Button[(int)Math.ceil((double) input.length / 5)][];
        for(int i = 0; i < input.length; i += 5) {
            result[(int)Math.ceil((double) i/5)] = Arrays.copyOfRange(input, i, Math.min(input.length, i + 5));
        }
        return result;
    }
}
