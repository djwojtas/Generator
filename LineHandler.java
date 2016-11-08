import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class that handles parsing one line of user input to one {@link LineGenerationOptions} class that contains all
 * necessary data for {@link OutputHandler} to generate java files
 */
public class LineHandler
{
    /**
     * Function creates chain of responsibilities to parse line to {@link LineGenerationOptions}
     *
     * @param options LineGenerationOptions object that we want to operate on
     * @param line line to parse
     * @return LineGenerationOptions with filled fields by parsing line
     */
    public static LineGenerationOptions handleLine(LineGenerationOptions options, String line)
    {
        OptionHandler first = new PackageHandler();
        first.add(new ClassNameHandler());
        first.add(new MethodNameHandler());
        first.add(new FieldHandler());
        first.add(new JavaDocHandler());
        first.add(new MainGenerationHanler());

        line = first.handleOption(options, line);
        String lastLine = "";

        while(!line.equals(lastLine))
        {
            lastLine = line;
            line = first.handleOption(options, line);
        }

        options.problem = line;

        return options;
    }

    /**
     * Test method for this class
     *
     * @param args cmd arguments
     */
    public static void main(String[] args)
    {
        LineGenerationOptions test = new LineGenerationOptions();
        String line = "pl.agh.Test public static void dsf() public void xt(int x) int=x boolean=y nodoc nomain";

        System.out.println("" + test);
        System.out.println("\nProblem with: " + line);
    }
}


/**
 * Interface for classes that want to connect to chain of responsibilities
 */
interface OptionHandler
{
    /**
     * Method adds OptionHandler object to the end of chain
     *
     * @param optionToAdd new OptionHandler object that we want to add to the end of chain
     */
    void add(OptionHandler optionToAdd);

    /**
     * handles parsing of line
     *
     * @param options LineGenerationOptions object that we want to operate on
     * @param line line to parse
     * @return filled LineGenerationOptions by object connected to this chain
     */
    String handleOption(LineGenerationOptions options, String line);
}


/**
 * Class implements default add method along with handleNextOption method that helps in creating handleOption method in derived classes
 */
abstract class AddLineHandlerToChain implements OptionHandler
{
    /**
     * holds reference to next OptionHandler
     */
    OptionHandler nextOption;

    public void add(OptionHandler optionToAdd)
    {
        if(nextOption != null)
        {
            nextOption.add(optionToAdd);
        }
        else
        {
            nextOption = optionToAdd;
        }
    }

    /**
     * Method checks for end of chain
     *
     * @param options LineGenerationOptions object that we want to operate on
     * @param line line to parse
     * @return null if end of chain, or recursive call of next handleOption if it's not end of chain
     */
    public String handleNextOption(LineGenerationOptions options, String line)
    {
        if(nextOption != null)
        {
            return nextOption.handleOption(options, line);
        }
        else
        {
            return line;
        }
    }
}


/**
 * Handles package information parsing
 */
class PackageHandler extends AddLineHandlerToChain
{
    public String handleOption(LineGenerationOptions options, String line)
    {
        String regexp = "^([a-zA-Z0-9]+\\.)+";
        Matcher optionMatcher = Pattern.compile(regexp).matcher(line);
        if(optionMatcher.find())
        {
            for(String folder : optionMatcher.group().replaceAll("\\.$", "").split("\\."))
            {
                options.packageStructure.add(folder);
            }

            line = line.replaceFirst(regexp, "%classname%=");
            return handleNextOption(options, line);
        }
        else
        {
            line = line.replaceFirst("^\\.", "%classname%=");
            return handleNextOption(options, line);
        }
    }
}


/**
 * Handles class name information parsing
 */
class ClassNameHandler extends AddLineHandlerToChain
{
    public String handleOption(LineGenerationOptions options, String line)
    {
        String regexp = "^%classname%=[a-zA-Z0-9]+";
        Matcher optionMatcher = Pattern.compile(regexp).matcher(line);
        if(optionMatcher.find())
        {
            options.className = optionMatcher.group().replaceAll("%classname%=", "");

            line = line.replaceFirst(regexp, "");
            return handleNextOption(options, line);
        }
        else
        {
            return handleNextOption(options, line);
        }
    }
}


/**
 * Handles method names and their args parsing
 */
class MethodNameHandler extends AddLineHandlerToChain
{
    public String handleOption(LineGenerationOptions options, String line)
    {
        String regexp = " (public |private |protected )?(static )?[a-zA-Z0-9]+ [a-zA-Z0-9]+\\(([a-zA-Z0-9]+ +[a-zA-Z0-9]+(, +)?)*\\)";
        Matcher optionMatcher = Pattern.compile(regexp).matcher(line);
        if(optionMatcher.find())
        {
            String fieldsString = optionMatcher.group().replaceAll("^ ", "");

            options.additionalMethods.add(new ArrayList<>());
            options.additionalMethods.get(options.additionalMethods.size()-1).add(fieldsString);

            options.additionalMethods.get(options.additionalMethods.size()-1).add(fieldsString.replaceFirst(" ?(public |private |protected )?(static )?", "").replaceFirst(" [a-zA-Z0-9]+\\(([a-zA-Z0-9]+ +[a-zA-Z0-9]+(, +)?)*\\)", ""));

            fieldsString = fieldsString.replaceFirst(" ?(public |private |protected )?(static )?[a-zA-Z0-9]+ [a-zA-Z0-9]+\\(", "").replaceFirst("\\)", "");

            if(!fieldsString.isEmpty())
            {
                String[] fieldsArray = fieldsString.split(" ");

                for (int i = 0; i < fieldsArray.length; ++i)
                {
                    options.additionalMethods.get(options.additionalMethods.size() - 1).add(fieldsArray[i++]);
                    options.additionalMethods.get(options.additionalMethods.size() - 1).add(fieldsArray[i].replace(",", ""));
                }
            }

            line = line.replaceFirst(regexp, "");
            return handleNextOption(options, line);
        }
        else
        {
            return handleNextOption(options, line);
        }
    }
}

/**
 * Handles fields information parsing
 */
class FieldHandler extends AddLineHandlerToChain
{
    public String handleOption(LineGenerationOptions options, String line)
    {
        String regexp = " [a-zA-Z0-9]+=[a-zA-Z0-9]+";
        Matcher optionMatcher = Pattern.compile(regexp).matcher(line);
        if(optionMatcher.find())
        {
            options.fields.add(optionMatcher.group().replaceAll("^ ", "").replaceAll("=", " ") + ";");

            line = line.replaceFirst(regexp, "");
            return handleNextOption(options, line);
        }
        else
        {
            return handleNextOption(options, line);
        }
    }
}

/**
 * Handles checking for javadoc generation
 */
class JavaDocHandler extends AddLineHandlerToChain
{
    public String handleOption(LineGenerationOptions options, String line)
    {
        String regexp = " nodoc";
        Matcher optionMatcher = Pattern.compile(regexp).matcher(line);
        if(optionMatcher.find())
        {
            options.javadocGeneration = false;

            line = line.replaceFirst(regexp, "");
            return handleNextOption(options, line);
        }
        else
        {
            return handleNextOption(options, line);
        }
    }
}

/**
 * Handles checking for main method generation
 */
class MainGenerationHanler extends AddLineHandlerToChain
{
    public String handleOption(LineGenerationOptions options, String line)
    {
        String regexp = " nomain";
        Matcher optionMatcher = Pattern.compile(regexp).matcher(line);
        if(optionMatcher.find())
        {
            options.mainGeneration = false;

            line = line.replaceFirst(regexp, "");
            return handleNextOption(options, line);
        }
        else
        {
            return handleNextOption(options, line);
        }
    }
}