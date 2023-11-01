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
import java.nio.file.FileVisitOption;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.FileVisitResult;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.nio.file.DirectoryStream;
import java.util.Collections;
import java.util.ArrayList;

public class Terminal {
    private Parser parser;
    private boolean isRunning;
    private List<String> history;
    private Path currentDiretory;

    Terminal() {
        parser = new Parser();
        history = new LinkedList<>();
        currentDiretory = Paths.get(System.getProperty("user.dir"));
        isRunning = true;
    }

    // younes
    public void echo(String[] args) {
        // i don't understand
        StringBuilder result = new StringBuilder();
        for (String arg : args) {
            result.append(arg).append(" ");
        }
        System.out.println(result.toString().trim());
    }

    public void ls(String[] args) {
        if (args.length > 1) {
            System.out.println("usage: ls [-r]");
            return;
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(currentDiretory)) {
            List<Path> entries = new ArrayList<Path>();

            for (Path entry : stream)
                entries.add(entry);
            if (args.length > 0 && args[0].equals("-r"))
                Collections.reverse(entries);
            entries.forEach(entry -> System.out.println(entry.getFileName()));
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            ;
        }

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

    // farah
    public void mkdir(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("Error: command not found or invalid parameters are entered!");
        } else {
            for (String arg : args) {
                File newDir = currentDiretory.resolve(arg).toFile();
                if (newDir.mkdir()) {
                    System.out.println("created dir: '" + newDir.getAbsolutePath().toString() + "'");
                } else {
                    System.err.println("couldn't create '" + newDir.getAbsolutePath().toString() + "'");
                }
            }
        }
    }

    // younes
    public void rmdir(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: rmdir [directory]");
            return;
        }

        if (args[0].equals("*")) {
            try {
                Files.walkFileTree(currentDiretory, new HashSet<>(Arrays.asList(FileVisitOption.FOLLOW_LINKS)),
                        Integer.MAX_VALUE, new SimpleFileVisitor<Path>() {
                            @Override
                            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                                return FileVisitResult.CONTINUE;
                            }

                            @Override
                            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                                if (Files.isDirectory(dir) && dir.toFile().list().length == 0) {
                                    Files.delete(dir);
                                }
                                return FileVisitResult.CONTINUE;
                            }
                        });
            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
                ;
            }

            return;
        }
        Path directory = currentDiretory.resolve(args[0]);
        if (Files.exists(directory)) {
            try {
                if (Files.isDirectory(directory) && directory.toFile().list().length == 0)
                    Files.delete(directory);
                else
                    System.out.println("Directory is not empty or does not exist.");
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } else {
            System.out.println(args[0] + " Directory does not exist.");
        }
    }

    // zeyad
    public void touch(String[] args) throws Exception {
        for (String file_path : args) {
            File newFile = currentDiretory.resolve(file_path).toFile();
            if (newFile.createNewFile()) {
                System.out.println(newFile.getName() + ": new file created");
            } else {
                System.out.println(newFile.getName() + " file already exists");
            }
        }
    }

    // farah
    public void cp(String[] args) throws Exception {
        if (args.length == 2) {
            Path src = currentDiretory.resolve(args[0]);
            Path des = currentDiretory.resolve(args[1]);
            FileReader srcReader = new FileReader(src.toString());
            FileWriter desWriter = new FileWriter(des.toString());
            int character;
            // Read characters from source File and write them to destination File
            while ((character = srcReader.read()) != -1) {
                desWriter.write(character);
            }
            srcReader.close();
            desWriter.close();
        } else if (args.length == 3 && args[0].equals("-r")) {
            Path sourcePath = currentDiretory.resolve(args[1]);
            Path targetPath = currentDiretory.resolve(args[2]);

            try {
                Files.walkFileTree(sourcePath, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult preVisitDirectory(final Path dir,
                            final BasicFileAttributes attrs) throws IOException {
                        Files.createDirectories(targetPath.resolve(sourcePath
                                .relativize(dir)));
                        return FileVisitResult.CONTINUE;
                    }
            
                    @Override
                    public FileVisitResult visitFile(final Path file,
                            final BasicFileAttributes attrs) throws IOException {
                        Files.copy(file,
                                targetPath.resolve(sourcePath.relativize(file)));
                        return FileVisitResult.CONTINUE;
                    }
                });
            
            } catch (IOException e) {
                System.out.println(e);;
                ;
            }

        } else
            System.out.println("Error: Invalid arguments");
    }

    // younes
    public void rm(String[] args) {
        if (args.length != 1) {
            System.out.println("usage: rm [filename]");
            return;
        }
        Path filePath = currentDiretory.resolve(args[0]);

        if (Files.exists(filePath) && Files.isRegularFile(filePath)) {
            try {
                Files.delete(filePath);
                System.out.println("File '" + args[0] + "' has been removed.");
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } else {
            System.out.println("File '" + args[0] + "' does not exist.");
        }
    }

    // farah
    public void cat(String[] args) throws Exception {
        if (args.length == 1) {
            Path filePath = currentDiretory.resolve(args[0]);
            File file = filePath.toFile();
            if (file.exists()) {
                Scanner myReader = new Scanner(file);
                while (myReader.hasNextLine()) {
                    String data = myReader.nextLine();
                    System.out.println(data);
                }
                myReader.close();
            } else {
                System.out.println("Error: command not found or invalid parameters are entered!");
            }
        } else if (args.length == 2) {
            Path fileName1 = currentDiretory.resolve(args[0]);
            Path fileName2 = currentDiretory.resolve(args[1]);
            File file1 = fileName1.toFile();
            File file2 = fileName2.toFile();
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
            } else {
                System.out.println("Error: command not found or invalid parameters are entered!");
            }
        } else {
            System.out.println("Error: command not found or invalid parameters are entered!");
        }
    }

    // zeyad
    public void history() {
        for (int i = 0; i < history.size(); i++)
            System.out.println((i + 1) + " " + history.get(i));
    }

    // zeyad
    public void exit() {
        isRunning = false;
    }

    // This method will choose the suitable command method to be called
    public void chooseCommandAction() throws Exception {
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
            case "ls":
                ls(parser.getArgs());
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
                // parse statement
                parser.parse(statement);
                // redirect output
                PrintStream originalStream = null;
                PrintStream redirectedStream = null;
                FileOutputStream outputFile = null;
                if (parser.getRedirectFilename() != null) {
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

    private static final Pattern WORD_PATTERN = Pattern
            .compile("((?:[^<>\\\"\\|\\s]+(?:\\\"[^\\\"]*\\\")*)+|(?:(?:\\\"[^\\\"]*\\\")+(?:[[^<>\\\"\\|]]*))+)");
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

    private LinkedList<String> tokenize(String input) throws ParseException {
        LinkedList<String> tokens = new LinkedList<>();
        Matcher matcher;

        while (!input.isEmpty()) {
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
        for (int i = 0; i < group.length(); i++) {
            if (group.charAt(i) == '"')
                continue;
            if (group.charAt(i) == '\\' && group.length() > i + 1 && group.charAt(i + 1) == '"')
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
