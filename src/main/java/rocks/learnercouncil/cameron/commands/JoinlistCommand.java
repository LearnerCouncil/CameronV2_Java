package rocks.learnercouncil.cameron.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import rocks.learnercouncil.cameron.Cameron;

import java.awt.*;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JoinlistCommand extends ListenerAdapter {

    private static final OkHttpClient client = new OkHttpClient();

    private static final Button javaButton = Button.primary("jl_java", "Java Edition");
    private static final Button bedrockButton = Button.primary("jl_bedrock", "Bedrock Edition");

    private static final Button acceptButton = Button.success("jl_accept", "Accept");
    private static final Button denyButton = Button.danger("jl_deny", "Deny");

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if(event.getOption("username") == null) return;
        String username = Objects.requireNonNull(event.getOption("username")).getAsString();
        event.replyEmbeds(new EmbedBuilder()
                .setAuthor(username)
                .setTitle("Please select your edition.")
                .setColor(Color.WHITE)
                .build()
        ).addActionRow(javaButton, bedrockButton).setEphemeral(true).queue();
    }

    private String formatUUID(String uuid) {
        return new StringBuilder(uuid).insert(8, '-').insert(13, '-').insert(18, '-').insert(23, '-').toString();
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        String username;
        try {
            username = Objects.requireNonNull(event.getMessage().getEmbeds().get(0).getAuthor()).getName();
        } catch (Exception e) {
            Cameron.logger.error("Incorrectly configured embed, embod or author not present.");
            return;
        }

        if(event.getComponentId().equals("jl_java")) {
            String jsonString = sendRequest(username);
            if(jsonString.startsWith("#ERR INVALID_CHARS")) {
                String invalid_characters = jsonString.split(":")[1];
                StringBuilder stringBuilder = new StringBuilder();
                for(char character : invalid_characters.toCharArray()) {
                    stringBuilder.append('\'');
                    stringBuilder.append(character);
                    stringBuilder.append("', ");
                }
                event.reply("The username you typed contains characters that can't be in a 'Minecraft: Java Edition' username: "
                        + stringBuilder.substring(0, stringBuilder.length() - 2)
                        + ". Please check to make sure you spelled your username correctly, and selected the correct edition of Minecraft.").setEphemeral(true).queue();
                return;
            }
            if(jsonString.isEmpty()) {
                event.reply("I can't find the minecraft username '" + username + "', check that it's spelled correctly.").setEphemeral(true).queue();
                return;
            }
            Map<String, String> json = parseJson(jsonString);
            if(json.containsKey("error")) {
                event.reply("I can't find the minecraft username '" + username + "', check that it's spelled correctly.").setEphemeral(true).queue();
                return;
            }
            if (!json.containsKey("id")) return;
            if(event.getMember() == null) return;
            Cameron.getExistingChannel("mc-joinlist").sendMessageEmbeds(
                    new EmbedBuilder()
                            .setColor(Color.YELLOW)
                            .setAuthor("Request Pending...")
                            .setDescription(event.getUser().getAsMention() + " is requesting to join the Minecraft server.\n" +
                                    "If you wish to accept the request, add them to the joinlist and click **Accept**. Otherwise, click **Deny**.")
                            .addField(new MessageEmbed.Field("> **Name:**", event.getUser().getAsMention(), false))
                            .addField(new MessageEmbed.Field("> **Username:**", json.get("name"), false))
                            .addField(new MessageEmbed.Field("> **UUID:**", formatUUID(json.get("id")), false))
                            .addField(new MessageEmbed.Field("> **Edition:**", "Java Edition", false))
                            .build()
            ).setActionRow(acceptButton, denyButton).queue();

            event.editMessageEmbeds(new EmbedBuilder()
                    .setTitle("Success!")
                    .setDescription("Request sent! A council member will review it soon.")
                    .setColor(Color.GREEN)
                    .build()).setActionRows().queue();
        }
        if(event.getComponentId().equals("jl_bedrock")) {

            Cameron.getExistingChannel("mc-joinlist").sendMessageEmbeds(
                    new EmbedBuilder()
                            .setColor(Color.YELLOW)
                            .setAuthor("Request Pending...")
                            .setDescription(event.getUser().getAsMention() + " is requesting to join the Minecraft server.\n" +
                                    "If you wish to accept the request, add them to the joinlist and click **Accept**. Otherwise, click **Deny**.")
                            .addField(new MessageEmbed.Field("> **Name:**", event.getUser().getAsMention(), false))
                            .addField(new MessageEmbed.Field("> **Username:**", username, false))
                            .addField(new MessageEmbed.Field("> **UUID:**", "N/A", false))
                            .addField(new MessageEmbed.Field("> **Edition:**", "Bedrock Edition", false))
                            .build()
            ).setActionRow(acceptButton, denyButton).queue();

            event.editMessageEmbeds(new EmbedBuilder()
                    .setTitle("Success!")
                    .setDescription("Request sent! A council member will review it soon.")
                    .setColor(Color.GREEN)
                    .build()).setActionRows().queue();
        }

        List<MessageEmbed.Field> fields = new ArrayList<>();
        MessageEmbed embed = event.getMessage().getEmbeds().get(0);
        if(!embed.getFields().isEmpty())
            fields = embed.getFields();

        if(event.getComponentId().equals("jl_accept")) {
            event.editMessageEmbeds(
                    new EmbedBuilder()
                            .setColor(Color.GREEN)
                            .setAuthor("Accepted.")
                            .setDescription("Request accepted.\n")
                            .addField(fields.get(0))
                            .addField(fields.get(1))
                            .addField(fields.get(2))
                            .addField(fields.get(3))
                            .setTimestamp(Instant.now())
                            .build()).setActionRows().queue();
            return;
        }
        if(event.getComponentId().equals("jl_deny")) {
            event.editMessageEmbeds(new EmbedBuilder()
                    .setAuthor("Denied.")
                    .setColor(Color.RED)
                    .setDescription("Request Denied.")
                    .addField(fields.get(0))
                    .addField(fields.get(1))
                    .addField(fields.get(2))
                    .addField(fields.get(3))
                    .setTimestamp(Instant.now())
                    .build()).setActionRows().queue(m -> m.deleteOriginal().queueAfter(120, TimeUnit.SECONDS));
        }
    }

    private String sendRequest(String username) {
        Pattern pattern = Pattern.compile("[a-zA-Z0-9\\-_]+");
        Matcher matcher = pattern.matcher(username);
        if(!matcher.matches()) return "#ERR INVALID_CHARS:" + matcher.replaceAll("");
        String url = "https://api.mojang.com/users/profiles/minecraft/" + username;
        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "application/json")
                .build();
        try {
            Response response = client.newCall(request).execute();
            //System.out.println(Objects.requireNonNull(response.body()).string());
            return Objects.requireNonNull(response.body()).string();
        } catch (IOException e) {
            System.out.println("IOException occured via /joinlist");
            return "#ERR IOEXCEPTION";
        }
    }

    private Map<String, String> parseJson(String json) {
        json = json.replaceAll("\s*\n*\r*", "");
        Map<String, String> map = new LinkedHashMap<>();
        String[] pairs = json.substring(1, json.length() - 1).split(",");
        for(String pair : pairs) {
            String[] parts = pair.split(":");
            if(parts.length != 2) continue;
            map.put(parts[0].substring(1, parts[0].length() - 1), parts[1].substring(1, parts[1].length() - 1));
        }
        return map;
    }
}
