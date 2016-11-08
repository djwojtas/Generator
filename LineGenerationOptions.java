import java.util.ArrayList;

/**
 * Created by djwojtas on 2016-11-03.
 */
public class LineGenerationOptions
{
    /**
     * holds class name
     */
    String className;

    /**
     * holds folder structure for package
     */
    ArrayList<String> packageStructure = new ArrayList<>();

    /**
     * Determines if javadoc should be generated
     */
    Boolean javadocGeneration = true;

    /**
     * Determines if main class should be generated
     */
    Boolean mainGeneration = true;

    /**
     * Contains methods with their return value and args.
     * First level holds [method, method method] etc
     * Second level Holds [method signature, return type, arg name, arg type, arg name, arg type, ...]
     */
    ArrayList<ArrayList<String>> additionalMethods = new ArrayList<>();

    /**
     * Holds fields declarations;
     */
    ArrayList<String> fields = new ArrayList<>();

    /**
     * Holds parts of line that couldn't be parsed
     */
    String problem;

    @Override
    public String toString()
    {
        return (className + "\n" + packageStructure + "\n" + javadocGeneration + "\n" + mainGeneration + "\n" + additionalMethods + "\n" + fields);
    }

    /**
     * Method generates path from packageStructure
     *
     * @return String holding path to package
     */
    public String generatePath()
    {
        String path = "";

        for (String folder : packageStructure)
        {
            path = path + folder + "\\";
        }

        return path;
    }
}
