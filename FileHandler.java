import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * Class handles file operations
 */
public class FileHandler
{
    /**
     * Opens file and returns scanner to it
     *
     * @param inputFileName file name to open
     * @return scanner to opened file
     */
    public static Scanner getInputFile(String inputFileName)
    {
        try
        {
            Scanner scan = new Scanner(new File(inputFileName));
            return scan;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Method creates dirs
     *
     * @param directory path to create
     */
    public static void mkDir(String directory)
    {
        File dirs = new File(directory);
        if (!dirs.exists()) {
            if (dirs.mkdirs()) {
                System.out.println("Package directories are created!");
            } else {
                System.out.println("Failed to create package directories!");
            }
        }
    }

    /**
     * Opens/Creates file and returns PrintWriter to it
     *
     * @param file file name to create or open
     * @return PrintWriter for opened file
     */
    public static PrintWriter openFile(String file)
    {
        try
        {
            PrintWriter writer = new PrintWriter(file, "UTF-8");
            return writer;
        } catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
