import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Queue;

import java.util.Arrays;
import java.util.LinkedList;

public class Terminal {
    Parser parser;
    List<String> history;

    public void echo(String[] args) {

    }

    public String pwd() {
        return "hello";
    }

    public void cd(String[] args) {

    }

    public void mkdir(String[] args) {

    }

    public void rmdir(String[] args) {

    }

    public void touch(String[] args) {

    }

    public void cp(String[] args) {

    }

    public void rm(String[] args) {

    }

    public void cat(String[] args) {

    }

    public void history() {

    }

    public void exit() {

    }

    // This method will choose the suitable command method to be called
    public void chooseCommandAction() {
    }

    public static void main(String[] args) {
        Parser parser = new Parser();
        try {
            parser.parse("echo hello there ");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(parser.getCommandName());
        System.out.println(Arrays.toString(parser.getArgs()));
        System.out.println(parser.getRedirectFilename());
        System.out.println(parser.isAppend());
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
        return args;
    }

    public boolean isAppend() {
        return appendMode;
    }

    public String getRedirectFilename() {
        return redirectFilename;
    }
}
