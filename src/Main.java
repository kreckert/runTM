import java.io.FileNotFoundException;

public class Main {

    public static void main(String[] args) throws Exception{
        //System.out.println("NUMBER OF ARGS" + args.length);
        //System.out.println("Your first argument is: "+args[0]);

        try {
            TuringMachine turingMachine;

            if (args[0].equals("-n")) {
                turingMachine = new TuringMachine();
                turingMachine.setNDTMFile(args[1]);

                if (args.length == 3) {
                    //turingMachine.loadTape(args[2]);
                } else {
                    turingMachine.runEmptyTape();
                }

            } else if (args.length > 1) {
                turingMachine = new TuringMachine(args[0]);
                turingMachine.loadTape(args[1]);
            } else {
                turingMachine = new TuringMachine(args[0]);
                turingMachine.runEmptyTape();
            }
        }
        catch (TMSyntaxErrorException e) {
            System.out.println("input error");
            System.exit(2);
        }
        catch (FileNotFoundException e ) {
            System.exit(3);
        }
    }
}
