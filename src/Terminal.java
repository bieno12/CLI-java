import java.util.List;

public class Terminal {
    Parser parser;
    List<String> history;


    public void echo(String[] args)
    {
        
    }

    public String pwd()
    {
        return "hello";
    }

    public void cd(String[] args)
    {

    }
    
    public void mkdir(String[] args)
    {

    }

    public void rmdir(String[] args)
    {

    }

    public void touch(String[] args)
    {

    }

    public void cp(String[] args)
    {

    }
    public void rm(String[] args)
    {

    }

    public void cat(String[] args)
    {
        
    }

    public void history()
    {

    }

    public void exit()
    {

    }
    //This method will choose the suitable command method to be called
    public void chooseCommandAction()
    {
        
    }

    public static void main(String[] args)
    {
        
    }
}


class Parser {
    String commandName;
    String[] args;

    String redirectFilename;
    boolean appendMode;

    //This method will divide the input into commandName and args
    //where "input" is the string command entered by the user
    Parser()
    {
        commandName = "";
        args = new String[0];
        redirectFilename = null;
        appendMode = false;
    }

    public boolean parse(String input) {
        return true;
    }

    public String getCommandName() {
        return commandName;
    }

    public String[] getArgs() {
        return args.clone();
    }

    public boolean isAppend()
    {
        return appendMode;
    }
    public String getRedirectFilename()
    {
        return redirectFilename;
    }
}

