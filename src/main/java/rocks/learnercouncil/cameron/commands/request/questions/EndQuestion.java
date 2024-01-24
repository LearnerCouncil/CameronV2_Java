package rocks.learnercouncil.cameron.commands.request.questions;

import net.dv8tion.jda.api.entities.MessageEmbed;
import rocks.learnercouncil.cameron.commands.request.Request;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class EndQuestion implements Question {

    private final MessageEmbed embed;
    private final Duration expirationDelay;

    public EndQuestion(MessageEmbed embed, Duration expirationDelay) {
        this.embed = embed;
        this.expirationDelay = expirationDelay;
    }

    @Override
    public void display(Request request) {
        request.getChannel().sendMessageEmbeds(embed).queue(m -> m.delete().queueAfter(expirationDelay.getSeconds(), TimeUnit.SECONDS));
        request.cancel(true);
    }
    
    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        EndQuestion that = (EndQuestion) o;
        return embed.equals(that.embed) && expirationDelay.equals(that.expirationDelay);
    }
    @Override
    public int hashCode() {
        return Objects.hash(embed, expirationDelay);
    }
    @Override
    public String toString() {
        return "EndQuestion{" +
                "embed=" + embed +
                ", expirationDelay=" + expirationDelay +
                '}';
    }
}
