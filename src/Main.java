import java.io.FileNotFoundException;

public class Main {

    public static void main(String[] args) throws Exception{
        //System.out.println("NUMBER OF ARGS" + args.length);
        //System.out.println("Your first argument is: "+args[0]);

        try {
            TuringMachine turingMachine = new TuringMachine(args[0]);
            if (args.length > 1) {
                turingMachine.loadTape(args[1]);
            } else {
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
