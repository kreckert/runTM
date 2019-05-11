public class Transition {
    private String state2;
    private String tapeoutput;
    private String move;

    public Transition() {
    }

    public Transition(String state2, String tapeoutput, String move) {
        this.state2 = state2;
        this.tapeoutput = tapeoutput;
        this.move = move;
    }

    public String getState2() {
        return state2;
    }

    public void setState2(String state2) {
        this.state2 = state2;
    }

    public String getTapeoutput() {
        return tapeoutput;
    }

    public void setTapeoutput(String tapeoutput) {
        this.tapeoutput = tapeoutput;
    }

    public String getMove() {
        return move;
    }

    public void setMove(String move) {
        this.move = move;
    }
}
