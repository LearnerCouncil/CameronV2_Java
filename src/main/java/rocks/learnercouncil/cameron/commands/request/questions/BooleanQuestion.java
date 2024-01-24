package rocks.learnercouncil.cameron.commands.request.questions;

import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;
import rocks.learnercouncil.cameron.commands.request.Request;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class BooleanQuestion extends ListenerAdapter implements Question {
    private static final String YES_BUTTON_ID = "request_yesButton";
    private static final String NO_BUTTON_ID = "request_noButton";

    private final MessageEmbed embed;
    private final Duration expirationDelay;
    private final Function<Request, Question> nextQuestion;

    public BooleanQuestion(MessageEmbed embed, Duration expirationDelay, Function<Request, Question> nextQuestion) {
        this.embed = embed;
        this.expirationDelay = expirationDelay;
        this.nextQuestion = nextQuestion;
    }

    @Override
    public void display(Request request) {
        request.getChannel().sendMessageEmbeds(embed).setActionRow(
                Button.secondary(YES_BUTTON_ID + "_" + request.getRequesterId(), Emoji.fromUnicode("✅")),
                Button.secondary(NO_BUTTON_ID + "_" + request.getRequesterId(), Emoji.fromMarkdown("<:redtick:785223937495531520>"))
        ).queue(m -> request.expireMessage(m, expirationDelay));
        request.setActiveQuestion(this);
        System.out.println("Setting active question to: " + this);
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        String buttonId = event.getComponentId();
        if(!buttonId.contains("request")) return;
        
        long userId = event.getUser().getIdLong();
        if(!buttonId.contains(String.valueOf(userId))) {
            event.reply("This isn't your request.").setEphemeral(true).queue();
            return;
        }
        Request.get(userId).ifPresent(request -> {
            System.out.println("Is Active: " + request.isActive(this) + ", Active Question: " + request.getActiveQuestion() + ", Current Question: " + this);
            if(!request.isActive(this)) return;
            
            if(buttonId.contains(YES_BUTTON_ID)) {
                nextQuestion.apply(request).display(request);
                event.deferEdit().queue();
                event.getHook().deleteOriginal().queue();
                return;
            }
    
            if(buttonId.contains(NO_BUTTON_ID)) {
                request.cancel(false);
                event.deferEdit().queue();
                event.getHook().deleteOriginal().queue();
            }
        });
    }
    
    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        BooleanQuestion that = (BooleanQuestion) o;
        return embed.equals(that.embed) && expirationDelay.equals(that.expirationDelay);
    }
    @Override
    public int hashCode() {
        return Objects.hash(embed, expirationDelay);
    }
    @Override
    public String toString() {
        return "BooleanQuestion{" +
                "embed=" + embed.getDescription() +
                ", expirationDelay=" + expirationDelay +
                '}';
    }
}
