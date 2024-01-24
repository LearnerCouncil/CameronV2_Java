package rocks.learnercouncil.cameron.commands.request.questions;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;
import rocks.learnercouncil.cameron.commands.request.Answer;
import rocks.learnercouncil.cameron.commands.request.Request;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class ButtonQuestion extends ListenerAdapter implements Question {

    private final MessageEmbed embed;
    private final Duration expirationDelay;
    private final List<Button> buttons;
    private final Function<Request, Question> nextQuestion;
    private final Answer answer;

    public ButtonQuestion(MessageEmbed embed, Duration expirationDelay, Function<Request, Question> nextQuestion, Answer answer, Button... buttons) {
        this.embed = embed;
        this.expirationDelay = expirationDelay;
        this.nextQuestion = nextQuestion;
        this.answer = answer;
        this.buttons = List.of(buttons);
    }

    @Override
    public void display(Request request) {
        request.getChannel().sendMessageEmbeds(embed).setActionRow(buttons.stream()
                        .map(b -> b.withId(b.getId() + "_" + request.getRequesterId()))
                        .toArray(Button[]::new)
        ).queue(m -> request.expireMessage(m, expirationDelay));
        request.setActiveQuestion(this);
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
    
        buttons.stream()
                .filter(b -> buttonId.contains(Objects.requireNonNull(b.getId())))
                .findAny()
                .ifPresent(button -> Request.get(userId).ifPresent(request -> {
                    request.isActive(this);
                    request.addAnswer(answer, button.getLabel());
                    nextQuestion.apply(request).display(request);
                    event.deferEdit().queue();
                    event.getHook().deleteOriginal().queue();
                }));
    }
    
    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        ButtonQuestion that = (ButtonQuestion) o;
        return embed.equals(that.embed) && expirationDelay.equals(that.expirationDelay) && buttons.equals(that.buttons) && answer == that.answer;
    }
    @Override
    public int hashCode() {
        return Objects.hash(embed, expirationDelay, buttons, answer);
    }
    @Override
    public String toString() {
        return "ButtonQuestion{" +
                "embed=" + embed +
                ", expirationDelay=" + expirationDelay +
                ", buttons=" + buttons +
                ", answer=" + answer +
                '}';
    }
}
