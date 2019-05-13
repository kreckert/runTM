
import java.util.ArrayList;

public class NDTMInstance {
    private ArrayList<String> tape;
    private String currentState;
    private int currentPosition;

    public NDTMInstance(ArrayList<String> tape, String currentState, int currentPosition) {
        this.tape = new ArrayList<>();
        for (int i = 0; i < tape.size(); i++) {
            this.tape.add(tape.get(i));
        }
        this.currentState = currentState;
        this.currentPosition = currentPosition;
    }

    public ArrayList<String> getTape() {
        return tape;
    }

    public void setTape(ArrayList<String> tape) {
        this.tape = tape;
    }

    public String getCurrentState() {
        return currentState;
    }

    public void setCurrentState(String currentState) {
        this.currentState = currentState;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }

    public boolean checkState(String state) {
        return currentState.equals(state);
    }

    public NDTMInstance(NDTMInstance instance) {
        this.tape = new ArrayList<>();
        for (int i = 0; i < instance.getTape().size(); i++) {
            this.tape.add(instance.getTape().get(i));
        }
        currentState = instance.getCurrentState();
        currentPosition = instance.getCurrentPosition();
    }

    public void applyTransition(Transition transition) {
        currentState = transition.getState2();
        tape.set(currentPosition, transition.getTapeoutput());

        //move tape head
        if (transition.getMove().equals("L") && currentPosition != 0) {
            currentPosition--;
        } else if (transition.getMove().equals("R")) {
            currentPosition++;
            //adds empty character to end of tape if current position is greater than tape size.
            if (currentPosition == tape.size()) {
                tape.add("_");
            }
        }
    }

    public String getKey() {
        return currentState.concat(tape.get(currentPosition));
    }

}
