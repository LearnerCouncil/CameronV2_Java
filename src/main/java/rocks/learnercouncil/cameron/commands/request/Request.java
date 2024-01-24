package rocks.learnercouncil.cameron.commands.request;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import rocks.learnercouncil.cameron.commands.request.questions.MessageQuestion;
import rocks.learnercouncil.cameron.commands.request.questions.Question;

import java.awt.*;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class Request {

    private static final Map<Long, Request> activeRequests = new HashMap<>();
    public static Optional<Request> get(long requesterId) {
        return Optional.ofNullable(activeRequests.get(requesterId));
    }
    public static boolean exists() {
        return !activeRequests.isEmpty();
    }


    private final MessageChannel channel;
    public MessageChannel getChannel() {
        return channel;
    }

    private final long requesterId;
    public long getRequesterId() {
        return requesterId;
    }

    private final Map<Answer, String> answers = new HashMap<>();
    public void addAnswer(Answer answer, String value) {
        answers.put(answer, value);
    }
    public String getAnswer(Answer key) {
        return answers.getOrDefault(key, "");
    }

    private Question activeQuestion = null;
    public void setActiveQuestion(Question question) {
        activeQuestion = question;
    }
    public boolean isActive(Question question) {
        return Objects.equals(activeQuestion, question);
    }
    public Question getActiveQuestion() {
        return activeQuestion;
    }
    
    private Message lastMessage = null;
    public boolean isLastMessage(Message other) {
        return Objects.equals(lastMessage, other);
    }
    public void setLastMessage(Message message) {
        lastMessage = message;
    }
    
    
    public Request(long requesterId, MessageChannel channel) {
        this.requesterId = requesterId;
        this.channel = channel;
        activeRequests.put(requesterId, this);
        Questions.CONFIRM.display(this);
    }

    public void addAnswers(EmbedBuilder embed) {
        answers.keySet().forEach(answer -> embed.addField(answer.displayName + ": ", answers.get(answer), false));
    }

    public void expireMessage(Message message, Duration delay) {
        if(message != null)
            message.delete().queueAfter(delay.getSeconds(), TimeUnit.SECONDS, m -> this.cancel(false), (err) -> {});
    }

    public void cancel(boolean silent) {
        activeRequests.remove(this.requesterId);
        if(!silent)
            channel.sendMessageEmbeds(new EmbedBuilder()
                    .setAuthor("Expires in 1 minute")
                    .setColor(Color.RED)
                    .setDescription("Request Cancelled, you can feel free to request again with /request.").build()
            ).queue(m -> m.delete().queueAfter(1, TimeUnit.MINUTES), (err) -> {});
    }
}
