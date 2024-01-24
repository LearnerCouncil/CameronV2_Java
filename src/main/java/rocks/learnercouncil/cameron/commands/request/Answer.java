package rocks.learnercouncil.cameron.commands.request;

public enum Answer {
    NAME("Name"),
    RANK("Rank"),
    EMAIL("Email Address"),
    PARENT_EMAIL("Parent Email Address"),
    AGE_GROUP("Age Group"),
    JOB("Job at ALP"),
    CHILD_NAMES("Child(s) names");


    public final String displayName;
    Answer(String displayName) {
        this.displayName = displayName;
    }
}
