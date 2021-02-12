package JavaClass;

public class Ballot {
    protected String BallotID;
    protected String BallotName;
    protected String BallotNotes;
    protected Boolean isActive;
    protected Boolean isEnded;

    public Boolean getEnded() {
        return isEnded;
    }

    public void setEnded(Boolean ended) {
        isEnded = ended;
    }

    public Ballot(String ballotID, String ballotName, String ballotNotes, Boolean isActive, Boolean isEnded) {
        BallotID = ballotID;
        BallotName = ballotName;
        BallotNotes = ballotNotes;
        this.isActive = isActive;
        this.isEnded = isEnded;
    }

    public Ballot(String ballotID, String ballotName, String ballotNotes, Boolean isActive) {
        this.BallotID = ballotID;
        this.BallotName = ballotName;
        this.BallotNotes = ballotNotes;
        this.isActive = isActive;
    }

    public Ballot(String ballotID, String ballotName, String ballotNotes) {
        this.BallotID = ballotID;
        this.BallotName = ballotName;
        this.BallotNotes = ballotNotes;
    }

    public Ballot(String ballotID, String ballotName) {
        this.BallotID = ballotID;
        this.BallotName = ballotName;
    }

    public String getBallotID() {
        return BallotID;
    }

    public void setBallotID(String ballotID) {
        this.BallotID = ballotID;
    }

    public String getBallotName() {
        return BallotName;
    }

    public void setBallotName(String ballotName) {
        this.BallotName = ballotName;
    }

    public String getBallotNotes() {
        return BallotNotes;
    }

    public void setBallotNotes(String ballotNotes) {
        this.BallotNotes = ballotNotes;
    }

    public Boolean getActive() {
        return isActive;
    }

    public Ballot() {
    }


    public void setActive(Boolean active) {
        this.isActive = active;
    }
}
