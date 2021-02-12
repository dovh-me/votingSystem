package JavaClass;

public class Candidate {
    protected String candidateId;
    protected String candidateName;
    protected String candidateParty;
    protected String candidateRegion;
    protected String candidateNIC;
    int votes;

    public Candidate(String candidateId, String candidateName, String candidateParty, String candidateRegion, String candidateNIC) {
        this.candidateId = candidateId;
        this.candidateName = candidateName;
        this.candidateParty = candidateParty;
        this.candidateRegion = candidateRegion;
        this.candidateNIC = candidateNIC;
    }

    public Candidate(String candidateId, String candidateName, String candidateParty) {
        this.candidateId = candidateId;
        this.candidateName = candidateName;
        this.candidateParty = candidateParty;
    }

    public Candidate(String candidateId, String candidateName, String candidateParty, int votes) {
        this.candidateId = candidateId;
        this.candidateName = candidateName;
        this.candidateParty = candidateParty;
        this.votes = votes;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public Candidate(String candidateId, int votes) {
        this.candidateId = candidateId;
        this.votes = votes;
    }

    public String getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(String candidateId) {
        this.candidateId = candidateId;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }

    public String getCandidateParty() {
        return candidateParty;
    }

    public void setCandidateParty(String candidateParty) {
        this.candidateParty = candidateParty;
    }

    public String getCandidateRegion() {
        return candidateRegion;
    }

    public void setCandidateRegion(String candidateRegion) {
        this.candidateRegion = candidateRegion;
    }

    public String getCandidateNIC() {
        return candidateNIC;
    }

    public void setCandidateNIC(String candidateNIC) {
        this.candidateNIC = candidateNIC;
    }
}
