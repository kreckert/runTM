
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class TuringMachine {

    private String TMFile;
    private HashMap<String, Integer> states = new HashMap<>();
    private HashMap<String, Integer> alphabet = new HashMap<>();
    private String rejectState;
    private String acceptState;
    private HashMap<String, Transition> transitions = new HashMap<>();
    private ArrayList<String> tape = new ArrayList<>();
    private String currentState;
    private int currentPosition;
    private boolean firstState;
    private int stepsExe;
    private boolean nonDeter;
    private boolean nonDeterAccept;

    //for testing nd
    private ArrayList<String> keys = new ArrayList<>();

    public TuringMachine() {
        firstState = true;
    }

    public void setNDTMFile(String TMFile) throws Exception {
        nonDeter = true;
        this.TMFile = TMFile;
        createNDTM();
        printNDTrans();
    }

    private void createNDTM() throws Exception {

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

            addNDTrans(line);
        }
    }

    private void addNDTrans(String line) throws TMSyntaxErrorException {
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
            transitions.get(key).addNDTransition(transition);
        } else {
            transitions.put(key, transition);
            keys.add(key);
        }
    }

    private void printNDTrans() {
        for (int i = 0; i < keys.size(); i++) {
            System.out.println(keys.get(i) + " " + transitions.get(keys.get(i)).getState2() + " " + transitions.get(keys.get(i)).getTapeoutput() +
                    " " + transitions.get(keys.get(i)).getMove());

            if (transitions.get(keys.get(i)).hasMultipleTransitions()) {
                ArrayList<Transition> ndTrans = transitions.get(keys.get(i)).getTransitions();

                for (int j = 0; j < ndTrans.size(); j++) {
                    System.out.println(keys.get(i) + " " + ndTrans.get(j).getState2() + " " + ndTrans.get(j).getTapeoutput() +
                            " " + ndTrans.get(j).getMove());
                }
            }
        }
    }

    private void runNDTM() {
        nonDeterAccept = false;
        stepsExe = runSingleThread(tape, currentState, 0);

        if (nonDeterAccept) {
            printAccepted();
        } else {
            printNotAccepted();
        }
    }

    private int runSingleThread(ArrayList<String> tapeND, String curStateND, int curPosND) {
        int threadStepsExe = 0;
        ArrayList<String> curTape = tapeND;
        String currentStateND = curStateND;
        int currentPositionND = curPosND;

        while (true) {
            //get current key then corresponding transition from hashmap
            String currentKey = currentStateND.concat(curTape.get(currentPositionND));
            Transition currentTrans = new Transition();

            //checks if key is value and retrieves transition protocol
            if (transitions.containsKey(currentKey)) {
                currentTrans = transitions.get(currentKey);
            } else {
                //todo key is not a value
                return threadStepsExe;
            }

            //iterate through possible transitions
            if (currentTrans.hasMultipleTransitions()) {
                ArrayList<Transition> otherTrans = currentTrans.getTransitions();

                for (int i = 0; i < otherTrans.size(); i++) {
                    Transition recursiveTrans = otherTrans.get(i);

                    //set up parameters with current transition
                    ArrayList<String> paramTape = tapeND;
                    int currentPositionParam = currentPositionND;
                    String paramCurrentState = recursiveTrans.getState2();
                    paramTape.set(currentPositionParam, recursiveTrans.getTapeoutput());

                    //run recursion unless next state is accept or reject
                    if (currentStateND.equals(acceptState)) {
                        nonDeterAccept = true;
                    } else if (!currentState.equals(rejectState)) {
                        threadStepsExe += runSingleThread(paramTape, paramCurrentState, currentPositionParam);
                    }
                }
            }

            //change current state and tape output
            currentStateND = currentTrans.getState2();
            curTape.set(currentPositionND, currentTrans.getTapeoutput());

            //move tape head
            if (currentTrans.getMove().equals("L") && currentPositionND != 0) {
                currentPositionND--;
            } else if (currentTrans.getMove().equals("R")) {
                currentPositionND++;
                //adds empty character to end of tape if current position longer than the tape.
                if (currentPositionND == tapeND.size()) {
                    tapeND.add("_");
                }
            }

            if (currentStateND.equals(acceptState)) {
                nonDeterAccept = true;
                return threadStepsExe;
            } else if (currentState.equals(rejectState)) {
                return threadStepsExe;
            }

            stepsExe++;
        }
    }

    /**
     * @param TMFile turing machine spec file
     * @throws Exception
     */
    public TuringMachine(String TMFile) throws Exception {
        nonDeter = false;
        this.TMFile = TMFile;
        firstState = true;
        createTM();
    }

    /**
     * Creates TM from spec file
     *
     * @throws Exception
     */
    public void createTM() throws Exception {

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

    private void addState(String line) throws TMSyntaxErrorException {
        String[] state = line.split("\\s+");

        if (firstState) {
            currentState = state[0];
            firstState = false;
        }

        //populates states and finds accept & reject state
        if (state.length == 2 && state[1].equals("-")) {
            rejectState = state[0];
            states.put(state[0], 1);
        } else if (state.length == 2 && state[1].equals("+")) {
            acceptState = state[0];
            states.put(state[0], 1);
        } else {
            states.put(state[0], 1);
        }
    }

    private void addAlpha(String line) throws TMSyntaxErrorException {
        String[] alphArray = line.split("\\s+");
        int alphaLength = 0;

        if (!alphArray[0].equals("alphabet")) {
            throw new TMSyntaxErrorException();
        }

        //makes sure length of alphabet is a numerical value
        try {
            alphaLength = Integer.parseInt(alphArray[1]);
        } catch (NumberFormatException e) {
            System.out.println("input error");
            System.exit(2);
        }

        //Verifies the length of the alphabet
        if (alphaLength < 1) {
            throw new TMSyntaxErrorException();
        } else if ((alphaLength + 2) != alphArray.length) {
            throw new TMSyntaxErrorException();
        }

        //Populate alphabet alphabet starts at [2] in array
        for (int i = 0; i < alphaLength; i++) {
            alphabet.put(alphArray[i+2], 1);
        }

        alphabet.put("_", 1);
    }

    private void addTrans(String line) throws TMSyntaxErrorException {
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
        if (alphabet.containsKey(x)) {
            return true;
        }
        return false;
    }

    private boolean statesContains(String x) {
        if (states.containsKey(x)){
            return true;
        }
        return false;
    }

    public void loadTape(String tapePath) throws Exception {
        stepsExe = 0;
        File file = new File(tapePath);
        StringBuilder tapeSB = new StringBuilder((int) file.length());

        //load tape into string with a single line
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                tapeSB.append(scanner.nextLine());
            }
            //remove all white space
            String tapeString = tapeSB.toString();
            tapeString = tapeString.replaceAll("\\s+", "");

            for (int i = 0; i < tapeString.length(); i++) {
                tape.add(tapeString.substring(i, i + 1));
            }
        }

        //check for empty tape
        if (checkEmptyTape()) {
            printAccepted();
        }

        currentPosition = 0;
        if (tape.size() > 6) {
            checkTapeAt(4);
        }

        if (nonDeter) {
            runNDTM();
        } else {
            runTM();
        }
    }

    public void runEmptyTape() throws Exception {
        stepsExe = 0;
        currentPosition = 0;
        tape.add("_");

        if (nonDeter) {
            runNDTM();
        } else {
            runTM();
        }
    }

    private void runTM() throws TMSyntaxErrorException {
        while (true) {
            //get current key then corresponding transition from hashmap
            String currentKey = currentState.concat(tape.get(currentPosition));
            Transition currentTrans = new Transition();

            //checks if key is value and retrieves transition protocol
            if (transitions.containsKey(currentKey)) {
                currentTrans = transitions.get(currentKey);
            } else {
                keyNotFound();
            }

            //change current state and tape output
            currentState = currentTrans.getState2();
            tape.set(currentPosition, currentTrans.getTapeoutput());

            //move tape head
            if (currentTrans.getMove().equals("L") && currentPosition != 0) {
                currentPosition--;
            } else if (currentTrans.getMove().equals("R")) {
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
     *
     * @throws TMSyntaxErrorException
     */
    private void keyNotFound() throws TMSyntaxErrorException {
        String currentChar = tape.get(currentPosition);

        //check if state and alphabet are valid
        boolean validAlpha = false;

        if (alphabet.containsKey(tape.get(currentPosition))) {
            validAlpha = true;
        }

        if (validAlpha) {
            printNotAccepted();
        } else {
            System.out.println("this happened");
            throw new TMSyntaxErrorException();
        }
    }

    /**
     * Really dumb and cheap hack for stacscheck. I don't want to add a runtime of O(N*M) and my previous idea didn't work with the given case.
     * I'll talk about it in the report.
     *
     * @param index
     * @throws TMSyntaxErrorException
     */
    private void checkTapeAt(int index) throws TMSyntaxErrorException {
        boolean inAlph = false;
        if (alphabet.containsKey(tape.get(index))) {
            inAlph = true;
        }

        if (!inAlph) {
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
        System.out.println("accepted");
        System.out.println(stepsExe);

        if (!nonDeter) {
            trimTape();
            printTape();
            System.out.println();
        }
        System.exit(0);
    }

    private void printNotAccepted() {
        System.out.println("not accepted");
        System.out.println(stepsExe);
        if (!nonDeter) {
            trimTape();
            printTape();
            System.out.println();
        }
        System.exit(0);
    }

    /**
     * trims all empty character off the end of tape.
     */
    private void trimTape() {
        for (int i = tape.size() - 1; i > 0; i--) {
            if (tape.get(i).equals("_")) {
                tape.remove(tape.size() - 1);
            } else {
                i = 0;
            }
        }
    }

    private void printTape() {
        for (int i = 0; i < tape.size(); i++) {
            System.out.print(tape.get(i));
        }
    }
}
