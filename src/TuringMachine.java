
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class TuringMachine {

    private HashMap<String, Integer> states = new HashMap<>();
    private String startState;
    private HashMap<String, Integer> alphabet = new HashMap<>();
    private String rejectState;
    private String acceptState;
    private HashMap<String, Transition> transitions = new HashMap<>();
    private boolean endedInAccept;
    private boolean nonDeter;
    private boolean firstState;
    private int stepsExe;

    /**
     *
     */
    public TuringMachine() {
        firstState = true;
        stepsExe = 0;
    }

    /**
     * less global variables
     * @param NDTMFile
     * @throws Exception
     */
    public void setNDTMFile(String NDTMFile) throws Exception {
        createNDTM(NDTMFile);
        //printNDTrans();
    }

    /**
     * sets all global variables
     * @param NDTMFile
     * @throws Exception
     */
    private void createNDTM(String NDTMFile) throws Exception {

        File file = new File(NDTMFile);
        Scanner sc = new Scanner(file);

        //checks for empty file
        if (file.length() == 0) {
            throw new TMSyntaxErrorException();
        }

        //first line - states #
        String line = sc.nextLine();
        int numOfStates = checkStateSyntax(line);

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

    /**
     * transitions have an array list of transitions
     * @param line
     * @throws TMSyntaxErrorException
     */
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

        if (nonDeter) {
            if (transitions.containsKey(key)) {
                transitions.get(key).addNDTransition(transition);
            } else {
                transitions.put(key, transition);
            }
        } else {
            if (transitions.containsKey(key)) {
                throw new TMSyntaxErrorException();
            } else {
                transitions.put(key, transition);
            }
        }
    }

    /**
     *
     * @param tapeND
     */
    private void runNDTM(ArrayList<String> tapeND) {
        endedInAccept = false;

        NDTMInstance firstInstance = new NDTMInstance(tapeND, startState, 0);

        runSingleThread2(firstInstance);

        if (endedInAccept) {
            printAccepted(firstInstance.getTape());
        } else {
            printNotAccepted(firstInstance.getTape());
        }
    }

    private void runSingleThread2(NDTMInstance instance) {

        //check if current state is end
        if (instance.checkState(acceptState)) {
            endedInAccept = true;
            return;
        } else if (instance.checkState(rejectState)) {
            return;
        }

        while (true) {
            //enter reject state if the key is not valid
            Transition transition;
            if (transitions.containsKey(instance.getKey())) {
                 transition = transitions.get(instance.getKey());
            } else {
                return;
            }

            if (transition.hasMultipleTransitions()) {
                ArrayList<Transition> transitionsND = transition.getTransitions();

                for (int i = 0; i < transitionsND.size(); i++) {
                    NDTMInstance instanceND = new NDTMInstance(instance);
                    instanceND.applyTransition(transitionsND.get(i));
                    if (endedInAccept) {
                        return;
                    }
                }
            }

            instance.applyTransition(transition);

            //check for ending state
            if (instance.checkState(acceptState)) {
                endedInAccept = true;
                return;
            } else if (instance.checkState(rejectState)) {
                return;
            }

            stepsExe++;
        }

    }

    /**
     *
     * @param line
     * @return
     * @throws Exception
     */
    private int checkStateSyntax(String line) throws Exception {
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

        return numOfStates;
    }

    /**
     *
     * @param line
     * @throws TMSyntaxErrorException
     */
    private void addState(String line) throws TMSyntaxErrorException {
        String[] state = line.split("\\s+");

        if (firstState) {
            startState = state[0];
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

    /**
     *
     * @param line
     * @throws TMSyntaxErrorException
     */
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

    /**
     *
     * @param x
     * @return
     */
    private boolean alphContains(String x) {
        if (alphabet.containsKey(x)) {
            return true;
        }
        return false;
    }

    /**
     *
     * @param x
     * @return
     */
    private boolean statesContains(String x) {
        if (states.containsKey(x)){
            return true;
        }
        return false;
    }

    /**
     * less global variables
     * @param tapePath
     * @throws Exception
     */
    public void loadNDTape(String tapePath) throws Exception{

        ArrayList<String> tapeND = new ArrayList<>();
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
                tapeND.add(tapeString.substring(i, i + 1));
            }
        }

        //check for empty tape
        if (isTapeEmpty(tapeND)) {
            printAccepted(tapeND);
        } else {

            if (tapeND.size() > 6) {
                checkTapeAt(tapeND,4);
            }

            if (nonDeter) {
                runNDTM(tapeND);
                //printNDTrans();
            } else {
                runNDTM(tapeND);
            }
        }

    }

    /**
     *
     * @throws Exception
     */
    public void runEmptyTape() throws Exception {
        stepsExe = 0;

        ArrayList<String> tapeND = new ArrayList<>();
        tapeND.add("_");

        if (nonDeter) {
            runNDTM(tapeND);
        } else {
            runNDTM(tapeND);
        }
    }

    /**
     * @param index
     * @throws TMSyntaxErrorException
     */
    private void checkTapeAt(ArrayList<String> tapeND, int index) throws TMSyntaxErrorException {
        boolean inAlph = false;
        if (alphabet.containsKey(tapeND.get(index))) {
            inAlph = true;
        }

        if (!inAlph) {
            throw new TMSyntaxErrorException();
        }
    }

    /**
     * less global variables
     * @param tapeND
     * @return
     */
    private boolean isTapeEmpty(ArrayList<String> tapeND) {
        for (int i = 0; i < tapeND.size(); i++) {
            if (!tapeND.get(i).equals("_")) {
                return false;
            }
        }
        return true;
    }

    /**
     *
     * @param tapeND
     */
    private void printAccepted(ArrayList<String> tapeND) {
        System.out.println("accepted");
        System.out.println(stepsExe);

        if (!nonDeter) {
            printTape(tapeND);
            System.out.println();
        }
        System.exit(0);
    }

    /**
     *
     * @param tapeND
     */
    private void printNotAccepted(ArrayList<String> tapeND) {
        System.out.println("not accepted");
        System.out.println(stepsExe);
        if (!nonDeter) {
            printTape(tapeND);
            System.out.println();
        }
        System.exit(0);
    }

    /**
     *
     * @param tapeND
     */
    private void printTape(ArrayList<String> tapeND) {
        //trim first
        for (int i = tapeND.size() - 1; i > 0; i--) {
            if (tapeND.get(i).equals("_")) {
                tapeND.remove(tapeND.size() - 1);
            } else {
                i = 0;
            }
        }

        for (int i = 0; i < tapeND.size(); i++) {
            System.out.print(tapeND.get(i));
        }
    }

    /**
     *
     * @param nonDeter
     */
    public void setNonDeter(boolean nonDeter) {
        this.nonDeter = nonDeter;
    }
}
