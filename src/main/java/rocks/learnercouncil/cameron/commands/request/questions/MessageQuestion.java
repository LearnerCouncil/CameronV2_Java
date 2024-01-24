package rocks.learnercouncil.cameron.commands.request.questions;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import rocks.learnercouncil.cameron.commands.request.Answer;
import rocks.learnercouncil.cameron.commands.request.Request;

import java.time.Duration;
import java.util.Objects;
import java.util.function.Function;

public class MessageQuestion extends ListenerAdapter implements Question {

    private final MessageEmbed embed;
    private final Duration expirationDelay;
    private final Function<Request, Question> nextQuestion;
    private final Answer answer;

    public MessageQuestion(MessageEmbed embed, Duration expirationDelay, Function<Request, Question> nextQuestion, Answer answer) {
        this.embed = embed;
        this.expirationDelay = expirationDelay;
        this.nextQuestion = nextQuestion;
        this.answer = answer;
    }

    @Override
    public void display(Request request) {
        request.getChannel().sendMessageEmbeds(embed).queue(m -> request.expireMessage(m, expirationDelay));
        request.setActiveQuestion(this);
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        long authorId = event.getAuthor().getIdLong();
        Request.get(authorId).ifPresent(request -> {
            if(!event.getChannel().equals(request.getChannel())) return;
            if(!request.isActive(this)) return;
            if(request.isLastMessage(event.getMessage())) return;
            
            request.addAnswer(answer, event.getMessage().getContentStripped());
            request.setLastMessage(event.getMessage());
            nextQuestion.apply(request).display(request);
        });
    }
    
    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        MessageQuestion that = (MessageQuestion) o;
        return embed.equals(that.embed) && expirationDelay.equals(that.expirationDelay) && nextQuestion.equals(that.nextQuestion) && answer == that.answer;
    }
    @Override
    public int hashCode() {
        return Objects.hash(embed, expirationDelay, nextQuestion, answer);
    }
    @Override
    public String toString() {
        return "MessageQuestion{" +
                "embed=" + embed +
                ", expirationDelay=" + expirationDelay +
                ", nextQuestion=" + nextQuestion +
                ", answer=" + answer +
                '}';
    }
}
