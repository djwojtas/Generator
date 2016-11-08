import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Class responsible for handling output from program
 */
public class OutputHandler
{
    /**
     * Writes whole class to file from LineGenerationOptions object fields
     *
     * @param line LineGenerationOptions object holding all necessary information
     */
    public static void writeClassToFile(LineGenerationOptions line)
    {
        OutputHandler outHandler = new OutputHandler();

        if(!line.packageStructure.isEmpty()) FileHandler.mkDir(line.generatePath().replaceAll("\\$", ""));

        PrintWriter writer = FileHandler.openFile(line.generatePath() + line.className + ".java");

        outHandler.createPackagePath(line, writer);

        outHandler.openClass(line, writer);

        outHandler.createFields(line, writer);
        outHandler.createMethods(line, writer);

        if(line.mainGeneration)
        {
            outHandler.createMain(line, writer);
        }

        outHandler.closeClass(writer);

        writer.close();
    }

    /**
     * Method creates directory for packages
     *
     * @param line LineGenerationOptions objects containing package structure
     * @param writer writer used to create dirs
     */
    private void createPackagePath(LineGenerationOptions line, PrintWriter writer)
    {
        if(!line.packageStructure.isEmpty())
        {
            String pkg = "package ";
            for(String folder : line.packageStructure)
            {
                pkg += folder + ".";
            }
            writer.print(pkg.replaceAll("\\.$", ";\n\n"));
        }
    }

    /**
     * Method generate class opening
     *
     * @param line LineGenerationOptions objects containing class name
     * @param writer writer used to output java class
     */
    private void openClass(LineGenerationOptions line, PrintWriter writer)
    {
        if(line.javadocGeneration)
        {
            writer.print("/**\n *\n */\n");
        }
        writer.print("class " + line.className + "\n{\n");
    }

    /**
     * Method generate class closing
     *
     * @param writer writer used to output java class
     */
    private void closeClass(PrintWriter writer)
    {
        writer.print("}");
    }

    /**
     * Method generate fields in class
     *
     * @param line LineGenerationOptions objects containing fields structure
     * @param writer writer used to output java class
     */
    private void createFields(LineGenerationOptions line, PrintWriter writer)
    {
        for(String field : line.fields)
        {
            if(line.javadocGeneration)
            {
                writer.print("\t/**\n\t *\n\t */\n");
            }
            writer.print("\t" + field + "\n");
        }
    }

    /**
     * Method generate methods
     *
     * @param line LineGenerationOptions objects containing methods structure
     * @param writer writer used to output java class
     */
    private void createMethods(LineGenerationOptions line, PrintWriter writer)
    {
        for(ArrayList<String> method : line.additionalMethods)
        {
            writer.println();
            if(line.javadocGeneration)
            {
                writer.print("\t/**\n\t *\n");

                for (int i = 3; i < method.size(); i+=2)
                {
                    writer.print("\t * @param " + method.get(i) + "\n");
                }

                if(!method.get(1).equals("void")) writer.print("\t * @return " + method.get(1) + "\n");
                writer.print("\t*/\n");
            }
            writer.print("\t" + method.get(0) + "\n\t{\n\n\t}\n");
        }
    }

    /**
     * Method generate main class
     *
     * @param line LineGenerationOptions objects containing information if main should be generated
     * @param writer writer used to output java class
     */
    private void createMain(LineGenerationOptions line, PrintWriter writer)
    {
        writer.println();
        if(line.javadocGeneration)
        {
            writer.print("\t/**\n\t * Main method of the program\n\t * @param args Command line arguments\n\t */\n");
        }
        writer.print("\tpublic static void main(String[] args)\n\t{\n\n\t}\n");
    }
}
