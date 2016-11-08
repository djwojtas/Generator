import java.io.FileOutputStream;

/**
 * Class that handles error array using chain of rasponsibilities
 */
public class ErrorHandler
{
    /**
     * Checks if errors occurred and writes eventual errors to file
     *
     * @param errors array of boolean values of occurred errors
     * @param outputFileName file name to write errors to
     * @return true if errors occurred false if not
     */
    public static boolean handleErrors(boolean[] errors, String outputFileName)
    {
        Error first = new defaultError("Input file don't exist", 0);
        first.add(new defaultError("Not enough arguments to launch", 1));
        first.add(new defaultError("Too much arguments", 2));

        FileOutputStream outStream;

        try
        {
            outStream = new FileOutputStream(outputFileName);
            return first.handleError(errors, outStream);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return true;
        }
    }

    /**
     * Test method for this class
     *
     * @param args cmd args
     */
    public static void main(String[] args)
    {
        boolean[] errors = {true, false, false};
        String outputFileName = "JavaGeneratorErrors.txt";
        System.out.println(handleErrors(errors, outputFileName));
    }
}

/**
 * Interface for classes that want to connect to chain of responsibilities
 */
interface Error
{
    /**
     * Method adds Error object to the end of chain
     *
     * @param errToAdd new Error object that we want to add to the end of chain
     */
    void add(Error errToAdd);

    /**
     * handles checking for errors
     *
     * @param errors boolean array that we want to operate on
     * @param outputFile file that we want to write errors
     * @return true if errors occurred false if not
     */
    boolean handleError(boolean[] errors, FileOutputStream outputFile);
}

/**
 * Class implements default add method along with handleNext method that helps in creating handleError method in derived classes
 */
abstract class AddErrorHandlerToChain implements Error
{
    /**
     * holds reference to next Error
     */
    Error nextErr;

    public void add(Error errToAdd)
    {
        if(nextErr != null)
        {
            nextErr.add(errToAdd);
        }
        else
        {
            nextErr = errToAdd;
        }
    }

    /**
     * Method checks for end of chain
     *
     * @param errors boolean array that we want to operate on
     * @param outputFile file that we want to write errors
     * @return null if end of chain, or recursive call of next handleError if it's not end of chain
     */
    protected boolean handleNext(boolean[] errors, FileOutputStream outputFile)
    {
        if(nextErr != null)
        {
            return nextErr.handleError(errors, outputFile);
        }
        else
        {
            return false;
        }
    }
}

/**
 * Default error class
 */
class defaultError extends AddErrorHandlerToChain
{
    /**
     * what should be written to file if error occurs
     */
    private String output;

    /**
     * index of error in errors array
     */
    private int errorIndex;

    /**
     * Constructor of defaultError
     *
     * @param output what should be written to file if error occurs
     * @param errorIndex index of error in errors array
     */
    defaultError(String output, int errorIndex)
    {
        this.output = output;
        this.errorIndex = errorIndex;
    }

    /**
     * Checks for one particular error
     *
     * @param errors boolean array that we want to operate on
     * @param outputFile file that we want to write errors
     * @return true if errors occurred false if not
     */
    public boolean handleError(boolean[] errors, FileOutputStream outputFile)
    {
        if(errors[errorIndex])
        {
            try{outputFile.write(output.getBytes());}
            catch(Exception e){e.printStackTrace();}

            handleNext(errors, outputFile);
            return true;
        }
        else
        {
            return handleNext(errors, outputFile);
        }
    }
}