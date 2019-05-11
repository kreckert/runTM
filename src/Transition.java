import java.util.ArrayList;

public class Transition {
    private String state2;
    private String tapeoutput;
    private String move;
    private ArrayList<Transition> transitions;
    private boolean multipleTransitions;

    public Transition() {
    }

    public Transition(String state2, String tapeoutput, String move) {
        this.state2 = state2;
        this.tapeoutput = tapeoutput;
        this.move = move;
        transitions = new ArrayList<>();
        multipleTransitions = false;
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

    public void addNDTransition(Transition ndTransition) {
        multipleTransitions = true;
        transitions.add(ndTransition);
    }

    public ArrayList<Transition> getTransitions() {
        return transitions;
    }

    public boolean hasMultipleTransitions() {
        return multipleTransitions;
    }
}
