package JavaClass;


public class Voter {
    protected String voterEID;
    protected String voterNIC;
    protected String voterName;
    protected Boolean voted;

    public Voter(String voterEID, String voterNIC, String voterName, Boolean voted) {
        this.voterEID = voterEID;
        this.voterNIC = voterNIC;
        this.voterName = voterName;
        this.voted = voted;
    }

    public Voter(String voterEID, String voterNIC, String voterName) {
        this.voterEID = voterEID;
        this.voterNIC = voterNIC;
        this.voterName = voterName;
    }

    public Voter(String voterEID, String voterNIC) {
        this.voterEID = voterEID;
        this.voterNIC = voterNIC;
    }

    public Voter() {
    }

    public String getVoterEID() {
        return voterEID;
    }

    public void setVoterEID(String voterEID) {
        this.voterEID = voterEID;
    }

    public String getVoterNIC() {
        return voterNIC;
    }

    public void setVoterNIC(String voterNIC) {
        this.voterNIC = voterNIC;
    }

    public String getVoterName() {
        return voterName;
    }

    public void setVoterName(String voterName) {
        this.voterName = voterName;
    }

    public Boolean getVoted() {
        return voted;
    }

    public void setVoted(Boolean voted) {
        this.voted = voted;
    }
}
