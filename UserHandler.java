import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Main class of the program, contains main method for whole program
 */
public class UserHandler
{
    /**
     * Array showing occurrence of particular problems
     */
    private boolean[] errors;

    /**
     * Scanner for reading file containing class generation options
     */
    private Scanner inputFile;

    /**
     * number of expected arguments passed in cmd
     */
    private byte expectedArguments = 2;

    /**
     * Container for particular files generation - each line is one class
     */
    private ArrayList<LineGenerationOptions> lineContainer= new ArrayList<LineGenerationOptions>();

    /**
     * Main method of whole program, takes care of error handling of particular functions
     *
     * @param args arguments passed in cmd
     */
    public static void main(String[] args)
    {
        UserHandler userHandler = new UserHandler();

        if(userHandler.checkUserCommands(args))
        {
            ErrorHandler.handleErrors(userHandler.errors, "JavaGeneratorErrors.txt");
            System.out.println("Errors occurred, check JavaGeneratorErrors.txt");
        }
        else
        {
            userHandler.inputFile = FileHandler.getInputFile(args[0]);

            while(userHandler.inputFile.hasNext())
            {
                String line = userHandler.inputFile.nextLine();
                LineGenerationOptions lineOptions = new LineGenerationOptions();
                LineHandler.handleLine(lineOptions, line);
                userHandler.lineContainer.add(lineOptions);
            }

            for(LineGenerationOptions line : userHandler.lineContainer)
            {
                OutputHandler.writeClassToFile(line);
            }
        }

        for(LineGenerationOptions line : userHandler.lineContainer)
        {
            System.out.println("\n\n" + line);
        }
    }

    /**
     * Method that checks for basic errors in user commands.
     * Checks if number of arguments is correct and if input file exists.
     *
     * @param args arguments to check
     * @return true if errors occurred, false if not
     */
    private boolean checkUserCommands(String[] args)
    {
        errors = new boolean[3];

        if(args.length == expectedArguments)
        {
            File f = new File(args[0]);
            errors[0] = !f.isFile();
        }
        else
        {
            errors[1] = args.length<expectedArguments;
            errors[2] = args.length>expectedArguments;
        }

        boolean result = false;

        for (boolean x : errors)
        {
            result = result || x;
        }

        return result;
    }
}