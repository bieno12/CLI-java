import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.Arrays;
import java.util.LinkedList;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;


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

    public void echo(String[] args) {

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

    public void mkdir(String[] args) throws Exception{

    }

    public void rmdir(String[] args) throws Exception{

    }

    //zeyad
    public void touch(String[] args) throws Exception{
        for(String file_path :  args)
        {
            File newFile = new File(file_path);
            if (newFile.createNewFile())
            {
                System.out.println(newFile.getName() + ": new file created");
            }
            else{
                System.out.println(newFile.getName() + " file already exists");
            }
        }
    }

    public void cp(String[] args) throws Exception{

    }

    public void rm(String[] args) throws Exception{

    }

    public void cat(String[] args) throws Exception{

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
                parser.parse(statement);
                history.add(statement);
                chooseCommandAction();
            } catch (Exception e) {
                System.err.println(e.getMessage());
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
        class ParseException extends Exception {
            ParseException(String message) {
                super(message);
            }
        }

        Queue<String> words = new LinkedList<>(Arrays.asList(input.split(" ")));
        if (words.size() == 0) {
            commandName = null;
            args = null;
            throw new ParseException("Empty Command");
        }

        commandName = words.poll();
        List<String> argsList = new LinkedList<>();
        while (!words.isEmpty()) {
            String w = words.peek();
            if (w.equals(">>") || w.equals(">") || w.equals("<") || w.equals("|"))
                break;
            argsList.add(words.poll());
        }
        args = argsList.toArray(new String[0]);

        redirectFilename = null;
        appendMode = false;
        if (!words.isEmpty()) {
            String operator = words.poll();
            switch (operator) {
                case ">": {
                    if (words.isEmpty())
                        throw new ParseException("> operator must be followed by a filename");
                    redirectFilename = words.poll();
                    appendMode = false;
                }
                    break;
                case ">>": {
                    if (words.isEmpty())
                        throw new ParseException(">> operator must be followed by a filename");
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

    private boolean isInvalidFilenamePart(int codePoint) {
        return codePoint != '\\'
                && codePoint != '/'
                && codePoint != ':'
                && codePoint != '*'
                && codePoint != '?'
                && codePoint != '<'
                && codePoint != '>'
                && codePoint != '|';
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
