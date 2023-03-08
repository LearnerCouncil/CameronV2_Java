package rocks.learnercouncil.cameron;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.*;
import java.util.stream.Collectors;

public class DynamicRoleList {

    private final List<Role> roles = new LinkedList<>();
    public List<Role> getRoles() {
        return roles;
    }
    private final Guild guild;
    public final TextChannel channel;

    public DynamicRoleList(Guild guild, TextChannel channel) {
        this.guild = guild;
        this.channel = channel;
        convertLegacy();
        this.channel.getIterableHistory().forEach(message -> {
            String[] lines = message.getContentRaw().split("\n");
            for(String line : lines) {
                add(line);
            }
        });
    }

    private void convertLegacy() {
        Set<Role> legacyRoles = guild.getRoles().stream().filter(r -> r.getName().startsWith("\u200B")).collect(Collectors.toSet());
        if(legacyRoles.size() < 1) return;
        for(Role role : legacyRoles) {
            role.getManager().setName(role.getName().substring(1)).queue();
        }
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

    public ActionRow[] getButtons(Member member) {
        ActionRow[] result;

        Button[] buttons = new Button[roles.size()];
        for(int i = 0; i < buttons.length; i++) {
            Role role = roles.get(i);
            if(member.getRoles().contains(role))
                buttons[i] = Button.primary("pncb_" + role.getName(), role.getName());
            else
                buttons[i] = Button.secondary("pncb_" + role.getName(), role.getName());
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

    private Button[][] splitArray(Button[] input) {
        Button[][] result = new Button[(int)Math.ceil((double) input.length / 5)][];
        for(int i = 0; i < input.length; i += 5) {
            result[(int)Math.ceil((double) i/5)] = Arrays.copyOfRange(input, i, Math.min(input.length, i + 5));
        }
        return result;
    }
}
