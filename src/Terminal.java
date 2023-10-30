import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Arrays;
import java.util.LinkedList;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.nio.file.Files;
import java.io.FileWriter;
import java.io.FileReader;

public class Terminal {
    private Parser parser;
    private boolean isRunning;
    private List<String> history;
    private Path currentDiretory;
    Terminal()
    {
        parser = new Parser();
        history = new LinkedList<>();
        currentDiretory = Paths.get(System.getProperty("user.dir"));
        isRunning = true;
    }

    //younes
    public void echo(String[] args) {
        System.out.println(String.join(" ", args));
    }

    // zeyad
    public String pwd() {
        return currentDiretory.toString();
    }

    // zeyad
    public void cd(String[] args) throws Exception {
        if (args.length != 1)
            throw new Exception("invalid number of arguments");
        Path relativePath = Paths.get(args[0]);
        Path resolvedPath = currentDiretory.resolve(relativePath).normalize();
        if (!Files.exists(resolvedPath) || !Files.isDirectory(resolvedPath))
            throw new Exception(args[0] + "path is invalid ");
        currentDiretory = resolvedPath;
    }

    //farah
    public void mkdir(String[] args) throws Exception{
        if (args.length < 1){
            System.out.println("Error: command not found or invalid parameters are entered!");
        }else {
            for (String arg : args) {
                File newDir = new File(arg);
                if (newDir.isAbsolute()) {
                    File parentDir = newDir.getParentFile();
                    if (parentDir != null && parentDir.isDirectory()) {
                        String newDirName = newDir.getName();
                        File nDir = new File(parentDir, newDirName);//putting the new dir in the parent dir
                        nDir.mkdir();
                    }
                }else {
                    newDir.mkdir();
                }
            }
        }
    }

    //younes
    public void rmdir(String[] args) throws Exception{

    }

    //zeyad
    public void touch(String[] args) throws Exception{
        for(String file_path :  args)
        {
            File newFile = currentDiretory.resolve(file_path).toFile();
            if (newFile.createNewFile())
            {
                System.out.println(newFile.getName() + ": new file created");
            }
            else{
                System.out.println(newFile.getName() + " file already exists");
            }
        }
    }

    //farah
    public void cp(String[] args) throws Exception{
        if (args.length < 2){
            System.out.println("Error: command not found or invalid parameters are entered!");
        }else {
            String src = args[0];
            String des = args[1];
            FileReader srcReader = new FileReader(src);
            FileWriter desWriter = new FileWriter(des);
            int character;
            //Read characters from source File and write them to destination File
            while ((character = srcReader.read()) != -1) {
                desWriter.write(character);
            }
            srcReader.close();
            desWriter.close();

        }
    }

    //younes
    public void rm(String[] args) throws Exception{

    }

    //farah
    public void cat(String[] args) throws Exception {
        if (args.length == 1) {
            String fileName = args[0];
            File file = new  File(fileName);
            if (file.exists()) {
                Scanner myReader = new Scanner(file);
                while (myReader.hasNextLine()) {
                    String data = myReader.nextLine();
                    System.out.println(data);
                }
                myReader.close();
            }else {
                System.out.println("Error: command not found or invalid parameters are entered!");
            }
        }else if (args.length == 2){
            String fileName1 = args[0];
            String fileName2 = args[1];
            File file1 = new  File(fileName1);
            File file2 = new  File(fileName2);
            if (file1.exists() && file2.exists()) {
                Scanner fileReader1 = new Scanner(file1);
                Scanner fileReader2 = new Scanner(file2);
                while (fileReader1.hasNextLine()) {
                    String data = fileReader1.nextLine();
                    System.out.println(data);
                }
                fileReader1.close();
                while (fileReader2.hasNextLine()) {
                    String data = fileReader2.nextLine();
                    System.out.println(data);
                }
                fileReader2.close();
            }else {
                System.out.println("Error: command not found or invalid parameters are entered!");
            }
        } else {
            System.out.println("Error: command not found or invalid parameters are entered!");
        }
    }

    // zeyad
    public void history() {
        for (int i = 0; i < history.size(); i++) {
            System.out.println((i + 1) + " " + history.get(i));
        }
    }

    // zeyad
    public void exit() {
        isRunning = false;
    }

    // This method will choose the suitable command method to be called
    public void chooseCommandAction() throws Exception{
        String commandName = parser.getCommandName();
        switch (commandName) {
            case "echo":
                echo(parser.getArgs());
                break;
            case "touch":
                touch(parser.getArgs());
                break;
            case "pwd":
                System.out.println(pwd());
                break;
            case "cd":
                cd(parser.getArgs());
                break;
            case "mkdir":
                mkdir(parser.getArgs());
                break;
            case "rmdir":
                rmdir(parser.getArgs());
                break;

            case "cp":
                cp(parser.getArgs());
                break;
            case "rm":
                rm(parser.getArgs());
                break;
            case "cat":
                cat(parser.getArgs());
                break;
            case "history":
                history();
                break;
            case "exit":
                exit();
                break;
            default:
                System.out.println(commandName + ": no such command");
                break;
        }
    }
    
    public void runPrompt() {
        Scanner input = new Scanner(System.in);
        while (isRunning) {
            System.out.print(">>");
            String statement = input.nextLine();
            try {
                //parse statement
                parser.parse(statement);
                //redirect output
                PrintStream originalStream = null; 
                PrintStream redirectedStream = null; 
                FileOutputStream outputFile = null;
                if (parser.getRedirectFilename() != null)
                {
                    originalStream = System.out;
                    outputFile = new FileOutputStream(parser.getRedirectFilename(),
                        parser.isAppend());
                    redirectedStream = new PrintStream(outputFile);

                    System.setOut(redirectedStream);
                }
                history.add(statement);
                chooseCommandAction();

                if (originalStream != null)
                    System.setOut(originalStream);
                if (redirectedStream != null)
                    redirectedStream.close();
                if (outputFile != null)
                    outputFile.close();
            } catch (Exception e) {
                System.err.println(e);
                continue;
            }
        }
        input.close();

    }
    public static void main(String[] args) {
        Terminal term = new Terminal();
        term.runPrompt();
    }
}
class Parser {
    String commandName;
    String[] args;
    String redirectFilename;
    private boolean appendMode;

    private static final Pattern WORD_PATTERN = Pattern.compile("((?:[^<>\\\"\\|\\s]+(?:\\\"[^\\\"]*\\\")*)+|(?:(?:\\\"[^\\\"]*\\\")+(?:[[^<>\\\"\\|]]*))+)");
    private static final Pattern OPERATOR_PATTERN = Pattern.compile("(>>|>)");
    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s+");

    BufferedReader stream;

    // This method will divide the input into commandName and args
    // where "input" is the string command entered by the user
    Parser() {
        commandName = null;
        args = null;
        redirectFilename = null;
        appendMode = false;
    }

    public boolean parse(String input) throws IOException, Exception {
        commandName = null;
        appendMode = false;
        redirectFilename = null;
        appendMode = false;
        args = null;

        if (input.trim() == "")
            throw new ParseException("Empty Command", 0);

        Queue<String> words = tokenize(input);

        commandName = words.poll();


        List<String> argsList = new LinkedList<>();
        while (!words.isEmpty()) {
            String w = words.peek();
            if (w.equals(">>") || w.equals(">") || w.equals("<") || w.equals("|"))
                break;
            argsList.add(words.poll());
        }
        args = argsList.toArray(new String[0]);


        if (!words.isEmpty()) {
            String operator = words.poll();
            switch (operator) {
                case ">": {
                    if (words.isEmpty())
                        throw new ParseException("> operator must be followed by a filename", 0);
                    redirectFilename = words.poll();
                    appendMode = false;
                }
                    break;
                case ">>": {
                    if (words.isEmpty())
                        throw new ParseException(">> operator must be followed by a filename", 0);
                    redirectFilename = words.poll();
                    appendMode = true;
                }
                    break;
                default:
                    throw new UnsupportedOperationException("operator " + operator + " isn't supported yet");
            }
        }
        return true;
    }

    private LinkedList<String> tokenize(String input) throws ParseException
    {
        LinkedList<String> tokens = new LinkedList<>();
        Matcher matcher;

        while(!input.isEmpty())
        {
            matcher = WORD_PATTERN.matcher(input);
            if (matcher.lookingAt()) {
                String word = normalize(matcher.group(1));
                tokens.add(word); // Capture the word
                input = input.substring(matcher.end());
                continue;
            }
            matcher = OPERATOR_PATTERN.matcher(input);
            if (matcher.lookingAt()) {
                tokens.add(matcher.group(1)); // Capture the operator
                input = input.substring(matcher.end());
                continue;
            }
            matcher = WHITESPACE_PATTERN.matcher(input);
            if (matcher.lookingAt()) {
                input = input.substring(matcher.end());
                continue;
            }
            throw new ParseException("unexpected character " + input.charAt(0), 0);
        }
        return tokens;

    }
    private String normalize(String group) {
        StringBuilder builder = new StringBuilder(group.length());
        for(int i = 0; i < group.length(); i++)
        {
            if(group.charAt(i) == '"')
                continue;
            if(group.charAt(i) == '\\' && group.length() > i + 1 && group.charAt(i + 1) == '"')
                builder.append('"');
            else
                builder.append(group.charAt(i));
        }
        return builder.toString();
    }

    public String getCommandName() {
        return commandName;
    }

    public String[] getArgs() {
        if (args == null)
            return new String[0];
        
        return args.clone();
    }

    public boolean isAppend() {
        return appendMode;
    }

    public String getRedirectFilename() {
        return redirectFilename;
    }
}
