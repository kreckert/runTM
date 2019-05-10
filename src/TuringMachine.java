
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class TuringMachine {

    private String TMFile;
    private ArrayList<String> states = new ArrayList<>();
    private ArrayList<String> alphabet = new ArrayList<>();
    private String rejectState;
    private String acceptState;
    private HashMap<String, Transition> transitions = new HashMap<>();
    private ArrayList<String> tape = new ArrayList<>();
    private String currentState;
    private int currentPosition;
    private boolean firstState;
    private int stepsExe;

    /**
     * @param TMFile turing machine spec file
     * @throws Exception
     */
    public TuringMachine(String TMFile) throws Exception{
        this.TMFile = TMFile;
        firstState = true;
        createTM();
    }

    /**
     * Creates TM from spec file
     * @throws Exception
     */
    public void createTM( ) throws Exception{

        File file = new File(TMFile);
        Scanner sc = new Scanner(file);
        //checks for empty file
        if (file.length() == 0) {
            throw new TMSyntaxErrorException();
        }
        String line = sc.nextLine();

        String[] state = line.split("\\s+");
        int numOfStates = 0;
        //make sure this is the state line
        if (state.length != 2) {
            throw new TMSyntaxErrorException();
        }
        //makes sure numOfStates is a numerical value
        try {
            numOfStates = Integer.parseInt(state[1]);
        } catch (NumberFormatException e) {
            System.out.println("input error");
            System.exit(2);
        }
        //Minimum of 2 states
        if (numOfStates <= 2) {
            throw new TMSyntaxErrorException();
        }
        //add all states to arrayList and find accept and reject states
        for (int i = 0; i < numOfStates; i++) {
            line = sc.nextLine();
            addState(line);
        }

        //populate alphabet
        line = sc.nextLine();
        addAlpha(line);

        while (sc.hasNextLine()) {
            line = sc.nextLine();

            if (line.length() == 0) {
                break;
            }

            addTrans(line);
        }



    }

    private void addState(String line) throws TMSyntaxErrorException{
        String[] state = line.split("\\s+");

        if (firstState) {
            currentState = state[0];
            firstState = false;
        }

        //populates states and finds accept & reject state
        if (state.length == 2 && state[1].equals("-")) {
            rejectState = state[0];
            states.add(state[0]);
        } else if (state.length == 2 && state[1].equals("+")) {
            acceptState = state[0];
            states.add(state[0]);
        } else {
            states.add(state[0]);
        }
    }

    private void addAlpha(String line) throws TMSyntaxErrorException{
        String[] alphabet = line.split("\\s+");
        int alphaLength = 0;

        if (!alphabet[0].equals("alphabet")) {
            throw new TMSyntaxErrorException();
        }

        //makes sure length of alphabet is a numerical value
        try {
            alphaLength = Integer.parseInt(alphabet[1]);
        } catch (NumberFormatException e) {
            System.out.println("input error");
            System.exit(2);
        }

        //Verifies the length of the alphabet
        if (alphaLength < 1) {
            throw new TMSyntaxErrorException();
        } else if ((alphaLength + 2) != alphabet.length) {
            throw new TMSyntaxErrorException();
        }

        //Populate alphabet alphabet starts at [2] in array
        for (int i = 0; i < alphaLength; i++) {
            this.alphabet.add(alphabet[i+2]);
        }

        this.alphabet.add("_");
    }

    private void addTrans(String line) throws TMSyntaxErrorException{
        String[] tran = line.split("\\s+");

        //Verifies that state names and chars exist in alphabet
        if (statesContains(tran[0]) && statesContains(tran[2]) && alphContains(tran[1]) && alphContains(tran[3])) {

        } else {
            throw new TMSyntaxErrorException();
        }

        //verifies last char is left or right
        if (tran[4].equals("L") || tran[4].equals("R")) {

        } else {
            throw new TMSyntaxErrorException();
        }

        //creates key which is concat of current state and tape input
        String key = tran[0].concat(tran[1]);

        //create transition value.
        Transition transition = new Transition(tran[2], tran[3], tran[4]);

        if (transitions.containsKey(key)) {
            throw new TMSyntaxErrorException();
        }

        transitions.put(key, transition);

    }

    private boolean alphContains(String x) {
        for (int i = 0; i < alphabet.size(); i++) {
            if (alphabet.get(i).equals(x)) {
                return true;
            }
        }
        return false;
    }

    private boolean statesContains(String x) {
        for (int i = 0; i < states.size(); i++) {
            if (states.get(i).equals(x)) {
                return true;
            }
        }
        return false;
    }

    public void loadTape(String tapePath) throws Exception{
        stepsExe = 0;
        File file = new File(tapePath);
        StringBuilder tapeSB = new StringBuilder((int)file.length());

        //load tape into string with a single line
        try (Scanner scanner = new Scanner(file)) {
            while(scanner.hasNextLine()) {
                tapeSB.append(scanner.nextLine());
            }
            //remove all white space
            String tapeString = tapeSB.toString();
            tapeString = tapeString.replaceAll("\\s+","");

            for (int i = 0; i < tapeString.length(); i++) {
                tape.add(tapeString.substring(i, i+1));
            }
        }

        //check for empty tape
        if (checkEmptyTape()) {
            printAccepted();
        }

        currentPosition = 0;
        //printTape();
        runTM();
    }

    public void runEmptyTape() throws Exception{
        stepsExe = 0;
        currentPosition = 0;
        tape.add("_");
        runTM();
    }

    /**
    private void runTM() throws TMSyntaxErrorException {
        //get current key then corresponding transition form hashmap
        String currentKey = currentState.concat(tape.get(currentPosition));
        Transition currentTrans = new Transition();

        if (transitions.containsKey(currentKey)) {
            currentTrans = transitions.get(currentKey);
        } else {
            keyNotFound();
        }

        //change current state and tape output
        currentState = currentTrans.getState2();
        tape.set(currentPosition,currentTrans.getTapeoutput());

        //move tape head
        if (currentTrans.move.equals("L") && currentPosition != 0) {
            currentPosition--;
        } else {
            currentPosition++;
            //adds empty character to end of tape if current position longer than the tape.
            if (currentPosition == tape.size()) {
                tape.add("_");
            }
        }

        if (currentState.equals(acceptState)) {
            printAccepted();
        } else if (currentState.equals(rejectState)) {
            printNotAccepted();
        }

        stepsExe++;

        runTM();
    }
     **/

    private void runTM() throws TMSyntaxErrorException {
        while (true) {
            //get current key then corresponding transition form hashmap
            String currentKey = currentState.concat(tape.get(currentPosition));
            Transition currentTrans = new Transition();

            if (transitions.containsKey(currentKey)) {
                currentTrans = transitions.get(currentKey);
            } else {
                keyNotFound();
            }

            //change current state and tape output
            currentState = currentTrans.getState2();
            tape.set(currentPosition, currentTrans.getTapeoutput());

            //move tape head
            if (currentTrans.move.equals("L") && currentPosition != 0) {
                currentPosition--;
            } else {
                currentPosition++;
                //adds empty character to end of tape if current position longer than the tape.
                if (currentPosition == tape.size()) {
                    tape.add("_");
                }
            }

            if (currentState.equals(acceptState)) {
                printAccepted();
            } else if (currentState.equals(rejectState)) {
                printNotAccepted();
            }

            stepsExe++;
        }
    }

    /**
     * if alphabet is not valid an exception will be thrown.
     * if key is valid but does not exist then machine enters reject state.
     * @throws TMSyntaxErrorException
     */
    private void keyNotFound() throws TMSyntaxErrorException{
        //check if state and alphabet are valid
        boolean validAlpha = false;
        for (int i = 0; i < alphabet.size(); i++) {
            if (tape.get(currentPosition).equals(alphabet.get(i))) {
                validAlpha = true;
            }
        }

        if (validAlpha) {
            printNotAccepted();
        } else {
            throw new TMSyntaxErrorException();
        }
    }

    private boolean checkEmptyTape() {
        for (int i = 0; i < tape.size(); i++) {
            if (!tape.get(i).equals("_")) {
                return false;
            }
        }
        tape.clear();
        tape.add("_");
        return true;
    }

    private void printAccepted() {
        trimTape();
        System.out.println("accepted");
        System.out.println(stepsExe);
        printTape();
        System.out.println();
        System.exit(0);
    }

    private void printNotAccepted() {
        trimTape();
        System.out.println("not accepted");
        System.out.println(stepsExe);
        printTape();
        System.out.println();
        System.exit(0);
    }

    /**
     * trims all empty character off the end of tape.
     */
    private void trimTape() {
        for (int i = tape.size()-1; i > 0; i--) {
            if (tape.get(i).equals("_")) {
                tape.remove(tape.size()-1);
            } else {
                i = 0;
            }
        }
    }

    private void printTape(){
        for (int i = 0; i < tape.size(); i++) {
            System.out.print(tape.get(i));
        }
    }
}
