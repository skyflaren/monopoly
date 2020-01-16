/*
    Justin Lu
    11/27/19
    ICS3U

    Monopoly | Simulates a multi-player game of Monopoly

    Credits:
        Community Chest Cards:
            https://monopoly.fandom.com/wiki/Community_Chest
        Chance Cards:
            https://monopoly.fandom.com/wiki/Chance
        Checking if a file is empty or not:
            https://www.java67.com/2018/03/a-simple-example-to-check-if-file-is-empty-in-java.html
        Previous Java knowledge from Olympiads School clases


    Run-Through:
        After a brief introductory Splash Screen, the user is brought to the Main Menu, where they can choose one of six options
            1.  New Game
                    Here, the user can enter information as to what they would like to name the game, the amount of players, what each
                    of the players wish to be called, and to choose a model color. Then, the leaderboard and game files are created if
                    they don't already exist, and the user is sent to Display
            2.  Resume Game
                    If a game file was already created, the user can choose a slot in the recent games array or return to the Main Menu,
                    and then either resume the game stored there, load a game that has a correctly formatted file, or exit back to the
                    Main Menu.

                    1.  Display
                            In  this method the user is first prompted to hand the game over to the next player, shown a dice roll, and
                            then is brought to where they can view the board, all the tokens, and all the properties (that have been
                            bought) upgrade level. They are also shown their various options for a move
                                1.  Buy/Upgrade/Pay for something
                                2.  View Properties and Card (If they have a Get Out of Jail)
                                3.  Forfeit
                                4.  Pause the game
                                5.  Sell the property
                                6.  Use the Get Out Of Jail Card
                    2.  ViewCardsProp
                            This lets the user to view the cards and properties the current user owns, and lets them remotely sell a property
                    3.  Forfeit
                            ExitGameMenu
                                This confirms that the user wants to forfeit. If so, it's marked in the array at [0][playerNumber][0]. Otherwise, it
                                brings you back
                    4.  Pause
                            ExitGameMenu
                                This confirms that the user wants to pause the game. If so, the game is saved and then the `option` variable is reset to
                                bring the user back to the Main Menu
                    5.  Sell This Property
                            If the user owns the property they are on, they have the option to sell it back at 75%
            3.  Leaderboards
                    The user may also choose to view the top 10 scores, where a user's score is their balance left over after they forfeit
                    or the game ends
            4.  Instructions
                    The user is brought to a page where they can finish reading the modified instructions, and continue back to the Main
                    Menu whenever they are ready
            5.  Reset Data
                    This method will reset all Leaderboards data
            6.  Exit
                    This brings the user to an exit menu, and then leaves.
*/


import java.awt.*;                  //Built in java library to help console
import java.io.*;                   //Allows use of file reading and writing
import hsa.Console;                 //Imports class to display to a window
import javax.swing.JOptionPane;     //Used to display popup error messages to the user
import java.lang.*;                 // Imports the library used for the Thread class, in this case to use pause, as well as for Random

public class Monopoly
{
    /*
        Class Variables
        ---
        Type        Name        Purpose
        Console     c           The Console that will be outputted on to
        String      gameName    This stores what the user chooses as the game's name
        int         gameNum     This stores the current game's ID, as the program can have 10 games loaded to an array that stores game names to search for later
        int         option      Which method the outer loop should go to (-1 for mainMenu, 1 for newGame() and then display(), 2 for resumeGam() then display,
                                    3 for leaderboards(), 4 for instructions(), 5 for resetData(), and 6 for exit())
                                    
        int         move        The choice the user makes once in the display() inner loop (1 for display(), 2 for exitGameMenu(), 3 for newCard(),
                                4 for viewCardsProps(), 6 typically used to leave the inner loop to return to mainMenu())
                                
        String[][]  gameDataStr This stores all game data stored in Strings:
                                [0][0]  the name of the game
                                [1][i]  the name of the i'th player
                                
        int[][]     gameDataInt This stores all game data stored in ints:
                                [0][0][0]   the ID of the game
                                [1][0][0]   the number of players
                                    [1][1][0]   which player's turn it is
                                    [1][2][0]   whether or not the player chose to end their turn
                                    [1][3][0]   the number of players still alive
                                    [1][4][0]   the roll from the left dice
                                        [1][4][1]   the roll from the right dice
                                [2][i][0]   the model color ID (1-8) of the i'th player
                                [3][i][0]   the balance of the i'th player
                                [4][i][0]   the amount of properties the i'th player has
                                    [4][i][j]   the ID (0-39) of a property the i'th player owns
                                [5][i][0]   the amount of cards (specifically, Get out of Jail Cards)
                                    [5][i][j]   the ID of the card the player has
                                [6][i][0]   the tile ID (0-39) the i'th player is on
                                [7][i][0]   the owner of the i'th property (0-39)
                                    [7][i][1]   the degree to which they upgraded that property
                                [8][i][0]   if larger than 0, the remaining amount of turns the i'th player has to spend in jail
                                
        int[]       reference   the array where all game names loaded are stored (0-9)
        string[]    cardDeck    the deck of cards the player can get, but the messages that will appear
    */
    
    Console c;                       //Creates an instance of the Console class, used to output to the screen
    String gameName = "";            //Stores what the user named the current game as
    int gameNum = -1;                //Stores the ID of the game
    int option = -1;                 //Stores what the user wishes to do
    int move = 1;                   //Stores the numerical ID of the move the user chooses to do in display
    String gameDataStr[] [] = new String [11] [40]; //Stores the game data that is stored as Strings
    int gameDataInt[] [] [] = new int [11] [40] [];     //Stores the game data that is stored as ints
    String reference[] = new String [11];       //Acts as a map of the game names and the game ID
    final String cardDeck[] =
        {
        "Advance to \"Go\". Collect $200",  //Deck of Cards and Effects
        "Bank error in your favour. Collect $200",
        "Doctor's fees. Pay $50",
        "From sale of stock you get $50",
        "Get out of Jail free",
        "Go to Jail. Do not pass Go, do not collect $200",
        "Holiday fund matures. Receive $100",
        "Income tax refund. Collect $20",
        "It's your birthday! Collect $10 from every player",
        "Life insurance matures. Collect $100",
        "Hospital fees. Pay $50",
        "School fees. Pay $50",
        "You are assessed for street repairs: Pay $50 for each property and upgrade.",
        "You have won second prize in a beauty contest. Collect $10",
        "You inherit $100.",
        };

        
    /*
        Constructor
        ---
        This is the constructor, which first initializes the array to a jagged array and sets the defaults. It also creates the Console. 
    */
    public Monopoly ()
    {
        for (int j = 0 ; j <= 8 ; j++)
        { //Making gameDataInt into a jagged array
            for (int i = 0 ; i <= 5 ; i++)
            {
                if (j == 1)
                    gameDataInt [1] [i] = new int [3];
                else if (j == 4 && i != 0)
                    gameDataInt [4] [i] = new int [40];
                else if (j == 5 && i != 0)
                    gameDataInt [5] [i] = new int [20];
                else if (j == 8)
                    gameDataInt [8] [i] = new int [2];
                else if (j != 7)
                    gameDataInt [j] [i] = new int [1];
            }
        }
        for (int i = 0 ; i < 40 ; i++)
        {
            gameDataInt [7] [i] = new int [2];
            for (int j = 0 ; j < 2 ; j++)
            {
                gameDataInt [7] [i] [j] = 0;
            }
        }

        gameDataInt [1] [2] [0] = -1;
        c = new Console (40, 60, "Monopoly"); //Creates the instance of Console which will be used throughout the program
    }


    /*
        Title()
        ---
        This will create a radial gradient from a bright green in the middle to a darker one on the edges.
    */
    public void title ()
    {
        c.clear (); //Clears screen for new one

        for (int i = 1200 ; i > 800 ; i -= 10)
        { //Draw a circular gradient for the background
            c.setColor (new Color (209 - (i / 10), 226 - (i / 14), 205 - (i / 13)));
            c.fillOval (320 - (i / 2), 250 - (i / 4), i, i / 2);
        }

        c.setColor (Color.white);
        c.setFont (new Font ("MonopolyTitle", Font.PLAIN, 20)); //Set the font for the title
        c.drawString ("MONOPOLY\n", 265, 32); //Displays the title

        c.drawRect (0, 0, 640, 500); //Outline the border of the screen
    }


    /*
        SplashScreen()
        ---
        This will make an instance of the SplashScreen class, and join() it so it will only move on once it's done running
        
        Type            Name        Purpose
        SplashScreen    ss          Creates an instance of SplashScreen, which will call Orb and display the graphics
    */
    public void splashScreen ()  //Creates the class of the Splash Screen
    {
        SplashScreen ss = new SplashScreen (c);
        ss.start ();
        try
        {
            ss.join ();
            Thread.sleep (200);
        }
        catch (Exception e)
        {
        }
    }

    
    /*
        Instructions()
        ---
        This displays a page's worth of instructions on a modified version of Monopoly I implemented
    */
    public void instructions ()
    {
        title ();   //Clears screen and outputs title
        c.setColor (Color.white);
        c.setFont (new Font ("Raleway-Light", Font.BOLD, 20));
        c.drawString ("Instructions", 30, 50);

        c.setFont (new Font ("Raleway-Light", Font.BOLD, 13));
        c.setColor (new Color (230, 230, 230));
        c.drawString ("On your turn, you will get to roll two dice, which indicate how many squares you advance.", 30, 80);
        c.drawString ("You may land on either a property, utility, chance/community chest, tax, GO, JAIL,", 30, 100);
        c.drawString ("FREE PARKING, or GO TO JAIL, ", 30, 120);

        c.drawString ("If you land on a property, if it is unowned, you may press '1' to buy it", 30, 160);
        c.drawString ("If you own it, you may upgrade it a max of three times, indicated by the bars around it.", 30, 180);
        c.drawString ("If someone else owns it, you must pay the rent indicated.", 30, 200);

        c.drawString ("The same applies for a utility, except rent is determined by you dice roll multiplied by either", 30, 220);
        c.drawString ("4 or 10 (if they  own just one utility, or two respectively). Utilities can't be upgraded.", 30, 240);

        c.drawString ("If you land on a chance/community chest, you will recieve a randomized card effect.", 30, 280);
        c.drawString ("If you land on a tax, you must pay the amount indicated.", 30, 300);
        c.drawString ("If you land or pass GO, you gain $200", 30, 320);
        c.drawString ("If you land on FREE PARKING, nothing happens", 30, 340);

        c.drawString ("If you land on JAIL but are not in jail, you nothing will happen. If you land on GO TO JAIL ", 30, 380);
        c.drawString ("(or from a card), you will go to that square without collecting GO money, and can get out", 30, 400);
        c.drawString ("by paying a $50 fine, rolling two of a kind, or (after three turns) forced to pay $50.", 30, 420);

        c.drawString ("If your balance is negative, you must release a property 75% of its price and upgrades.", 30, 440);
        c.drawString ("If you have no more properties, you go bankrupt, and lose the game.", 30, 460);

        c.setColor (Color.white);
        c.drawString ("Any key to return.", 30, 490);
        pauseProgram ();  //Reads in whether the user wants to continue with the program
        option = -1;
    }





    /*
        newGame()
        ---
        This takes in all the input regarding starting a game, and creates the Game File and ensures a Leaderboard File is made
        
        Type        Name        Purpose
        String      convert     When taking in input, this will hold the raw String of what they enter, which will eventually be clipped/converted as needed
        char        letter      Used to store the latest letter the user has inputted
        boolean[]   cols        Used to store which colours have been used, so each player has a unique color
    */
    public void newGame ()
    {
        for (int j = 0 ; j <= 8 ; j++)  //Reset the array
        { //Making gameDataInt into a jagged array
            for (int i = 0 ; i <= 5 ; i++)
            {
                if (j == 1)
                    gameDataInt [1] [i] = new int [3];
                else if (j == 4 && i != 0)
                    gameDataInt [4] [i] = new int [40];
                else if (j == 5 && i != 0)
                    gameDataInt [5] [i] = new int [20];
                else if (j == 8)
                    gameDataInt [8] [i] = new int [2];
                else if (j != 7)
                    gameDataInt [j] [i] = new int [1];
            }
        }
        for (int i = 0 ; i < 40 ; i++)
        {
            gameDataInt [7] [i] = new int [2];
            for (int j = 0 ; j < 2 ; j++)
            {
                gameDataInt [7] [i] [j] = 0;
            }
        }
        gameDataInt [1] [2] [0] = -1;




        if (gameNum < 9 && reference [gameNum + 1] == null)
        { //A new game is possible to make
            gameNum++; //Change game ID's

            while (true)    //Read in the game name
            {
                try
                {
                    title ();   //Clears screen and outputs title
                    c.setColor (Color.white);
                    c.fillRect (40, 50, 600, 450);

                    c.setColor (Color.black);
                    c.setFont (new Font ("Raleway-Light", Font.BOLD, 20));
                    c.drawString ("Game Name", 70, 200);

                    c.drawLine (70, 220, 90, 220);

                    c.setColor (Color.darkGray);
                    c.setFont (new Font ("Raleway-Light", Font.PLAIN, 12));
                    c.drawString ("What would you like to name this game? (1-20 characters): ", 70, 250);
                    c.setCursor (14, 10);



                    //Reads in whatever the user inputted
                    c.setColor (new Color (129, 169, 143));
                    c.setFont (new Font ("Raleway-Light", Font.PLAIN, 17));
                    String convert = "";
                    char letter = ' ';
                    while (letter != '\n' || convert.length () == 1)
                    {
                        if (letter != 8 && letter != '\n' && letter != '\t' && convert.length () <= 42)
                            convert += letter;
                        else if (letter == 8 && convert.length () >= 2)
                            convert = convert.substring (0, convert.length () - 1);
                        c.setColor (Color.white);
                        c.fillRect (60, 255, 580, 40);
                        c.setColor (new Color (129, 169, 143));
                        c.drawString (convert, 65, 275);
                        letter = c.getChar ();
                    }
                    gameDataStr [0] [0] = convert.substring (1);



                    if (gameDataStr [0] [0].length () > 20) //Checks whether the game name is over 20 characters
                    {
                        throw new Exception (); //If it is, restart the input prompt
                    }
                    break;
                }
                catch (Exception e)
                {
                    JOptionPane.showMessageDialog (null, "Please enter a game name less than 20 characters.", "Error", JOptionPane.ERROR_MESSAGE);
                } //Throw the exception and show the error panel
            }
            reference [gameNum] = gameDataStr [0] [0]; //pass the name of the game over to the game reference list
            c.setColor (Color.white);


            c.fillRect (65, 275, 100, 30);  //Erase the phrase "Game Name" to allow the new title of "Player Number
            while (true)    //Read in the amount of players
            {
                try
                {
                    c.setColor (Color.white);
                    title ();
                    c.fillRect (40, 50, 600, 450);

                    c.setColor (Color.black);
                    c.setFont (new Font ("Raleway-Light", Font.BOLD, 20));
                    c.drawString ("Player Number", 70, 200);

                    c.drawLine (70, 220, 90, 220);

                    c.setColor (Color.darkGray);
                    c.setFont (new Font ("Raleway-Light", Font.PLAIN, 12));
                    c.drawString ("What would you like to name this game? (1-20 characters): ", 70, 250);

                    c.setColor (new Color (129, 169, 143));
                    c.setFont (new Font ("Raleway-Light", Font.PLAIN, 17));
                    c.drawString (" " + reference [gameNum], 65, 275);

                    c.setColor (Color.darkGray);
                    c.setFont (new Font ("Raleway-Light", Font.PLAIN, 12));
                    c.drawString ("How many people are playing? (2-4): ", 70, 320);
                    c.setCursor (18, 10);


                    c.setFont (new Font ("Raleway-Light", Font.PLAIN, 12));
                    //Reads in whatever the user inputted
                    c.setColor (new Color (129, 169, 143));
                    c.setFont (new Font ("Raleway-Light", Font.PLAIN, 17));
                    String convert = "";
                    char letter = ' ';
                    while (letter != '\n' || convert.length () == 1)
                    {
                        if (letter != 8 && letter != '\n' && letter != '\t' && convert.length () <= 42)
                            convert += letter;
                        else if (letter == 8 && convert.length () >= 2)
                            convert = convert.substring (0, convert.length () - 1);
                        c.setColor (Color.white);
                        c.fillRect (60, 325, 580, 40);
                        c.setColor (new Color (129, 169, 143));
                        c.drawString (convert, 65, 340);
                        letter = c.getChar ();
                    }
                    gameDataInt [1] [0] [0] = Integer.parseInt (convert.substring (1));



                    if (gameDataInt [1] [0] [0] > 4 || gameDataInt [1] [0] [0] < 2) //Checks whether the number of players is between 1 and 4
                    {
                        throw new Exception (); //If it is, restart the input prompt
                    }

                    gameDataInt [1] [3] [0] = gameDataInt [1] [0] [0]; //The amount of players still in the game is equal to the amount of players
                    break;
                }
                catch (NumberFormatException n)
                {
                    JOptionPane.showMessageDialog (null, "Please enter a whole number from 1-4.", "Error", JOptionPane.ERROR_MESSAGE);
                }
                catch (Exception e)
                {
                    JOptionPane.showMessageDialog (null, "Please enter an amount of players between 1 and 4", "Error", JOptionPane.ERROR_MESSAGE);
                } //Throw the exception and show the error panel
            }





            for (int i = 1 ; i <= gameDataInt [1] [0] [0] ; i++)
            {
                while (true)    //Read in each players names and a model
                {
                    try
                    {
                        title ();   //Clears screen and outputs title
                        c.setColor (Color.white);
                        c.fillRect (40, 50, 600, 450);

                        c.setColor (Color.black);
                        c.setFont (new Font ("Raleway-Light", Font.BOLD, 20));
                        c.drawString ("Player " + i, 70, 200);

                        c.drawLine (70, 220, 90, 220);

                        c.setColor (Color.darkGray);
                        c.setFont (new Font ("Raleway-Light", Font.PLAIN, 12));
                        c.drawString ("What would you like to be called (1-20 characters): ", 70, 250);
                        c.setCursor (14, 10);


                        //Reads in whatever the user inputted
                        c.setColor (new Color (129, 169, 143));
                        c.setFont (new Font ("Raleway-Light", Font.PLAIN, 17));
                        String convert = "";
                        char letter = ' ';
                        while (letter != '\n' || convert.length () == 1)
                        {
                            if (letter != 8 && letter != '\n' && letter != '\t' && convert.length () <= 42)
                                convert += letter;
                            else if (letter == 8 && convert.length () >= 2)
                                convert = convert.substring (0, convert.length () - 1);
                            c.setColor (Color.white);
                            c.fillRect (60, 255, 580, 40);
                            c.setColor (new Color (129, 169, 143));
                            c.drawString (convert, 65, 275);
                            letter = c.getChar ();
                        }
                        gameDataStr [1] [i] = convert.substring (1);


                        if (gameDataStr [1] [i].length () > 20) //Checks whether the game name is over 20 characters
                        {
                            throw new Exception (); //If it is, restart the input prompt
                        }
                        break;
                    }
                    catch (Exception e)
                    {
                        JOptionPane.showMessageDialog (null, "Please enter a username less than 20 characters.", "Error", JOptionPane.ERROR_MESSAGE);
                    } //Throw the exception and show the error panel
                }

                while (true)    //Read in each players names and a model
                {
                    try
                    {
                        title ();   //Clears screen and outputs title
                        c.setColor (Color.white);
                        c.fillRect (40, 50, 600, 450);

                        c.setColor (Color.black);
                        c.setFont (new Font ("Raleway-Light", Font.BOLD, 20));
                        c.drawString (gameDataStr [1] [i], 70, 200);

                        c.drawLine (70, 220, 90, 220);

                        c.setColor (Color.darkGray);
                        c.setFont (new Font ("Raleway-Light", Font.PLAIN, 12));
                        c.drawString ("What would you like to be called (1-20 characters): ", 70, 250);

                        c.setColor (new Color (129, 169, 143));
                        c.setFont (new Font ("Raleway-Light", Font.PLAIN, 17));
                        c.drawString (" " + gameDataStr [1] [i], 65, 275);

                        c.setColor (Color.darkGray);
                        c.setFont (new Font ("Raleway-Light", Font.PLAIN, 12));
                        c.drawString ("Select a token color associated with ID's numbered 1-8: ", 70, 320);




                        boolean cols[] = new boolean [11];
                        for (int k = 1 ; k < i ; k++)
                        {
                            cols [gameDataInt [2] [k] [0]] = true;
                        }

                        c.setFont (new Font ("Raleway-Light", Font.BOLD, 12));
                        if (!cols [1])
                        {
                            c.setColor (new Color (140, 90, 60));           //Brown    (Choosing colors for the model tokens)
                            c.fillRect (600, 90, 15, 15);
                            c.drawString ("1", 590, 102);
                        }
                        if (!cols [2])
                        {
                            c.setColor (new Color (140, 180, 210));         //Light Blue
                            c.fillRect (600, 110, 15, 15);
                            c.drawString ("2", 590, 122);
                        }
                        if (!cols [3])
                        {
                            c.setColor (new Color (200, 75, 150));          //Pink
                            c.fillRect (600, 130, 15, 15);
                            c.drawString ("3", 590, 142);
                        }
                        if (!cols [4])
                        {
                            c.setColor (new Color (235, 150, 60));          //Orange
                            c.fillRect (600, 150, 15, 15);
                            c.drawString ("4", 590, 162);
                        }
                        if (!cols [5])
                        {
                            c.setColor (new Color (220, 55, 50));          //Red
                            c.fillRect (600, 170, 15, 15);
                            c.drawString ("5", 590, 181);
                        }
                        if (!cols [6])
                        {
                            c.setColor (new Color (255, 245, 75));          //Yellow
                            c.fillRect (600, 190, 15, 15);
                            c.drawString ("6", 590, 201);
                        }
                        if (!cols [7])
                        {
                            c.setColor (new Color (85, 175, 100));          //Green
                            c.fillRect (600, 210, 15, 15);
                            c.drawString ("7", 590, 221);
                        }
                        if (!cols [8])
                        {
                            c.setColor (new Color (50, 115, 180));          //Dark Blue
                            c.fillRect (600, 230, 15, 15);
                            c.drawString ("8", 590, 241);
                        }


                        //Reads in whatever the user inputted
                        c.setColor (new Color (129, 169, 143));
                        c.setFont (new Font ("Raleway-Light", Font.PLAIN, 17));
                        String convert = "";
                        char letter = ' ';
                        while (letter != '\n' || convert.length () == 1)
                        {
                            if (letter != 8 && letter != '\n' && letter != '\t' && convert.length () <= 42)
                                convert += letter;
                            else if (letter == 8 && convert.length () >= 2)
                                convert = convert.substring (0, convert.length () - 1);
                            c.setColor (Color.white);
                            c.fillRect (60, 325, 580, 40);
                            c.setColor (new Color (129, 169, 143));
                            c.drawString (convert, 65, 345);
                            letter = c.getChar ();
                        }
                        gameDataInt [2] [i] [0] = Integer.parseInt (convert.substring (1));



                        for (int j = i - 1 ; j >= 1 ; j--)
                        {
                            if (gameDataInt [2] [i] [0] == gameDataInt [2] [j] [0])
                                throw new FileNotFoundException ();
                        }
                        if (gameDataInt [2] [i] [0] > 8 || gameDataInt [2] [i] [0] < 1) //Checks whether the game name is over 10 or below 1
                        {
                            throw new Exception (); //If it is, restart the input prompt
                        }
                        break;
                    }
                    catch (FileNotFoundException e)
                    {
                        JOptionPane.showMessageDialog (null, "Please enter a different number than anyone else", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    catch (Exception e)
                    {
                        JOptionPane.showMessageDialog (null, "Please enter a number between 1 and 8.", "Error", JOptionPane.ERROR_MESSAGE);
                    } //Throw the exception and show the error panel
                }
            }

            //Write this to a file
            try
            {
                File lb = new File ("Leaderboards.txt");  //Makes leaderboard file
                if (lb.length () == 0)
                {
                    PrintWriter lbpr = new PrintWriter (new FileWriter ("Leaderboards.txt"));
                    lbpr.println (0); //If the leaderboard file isn't made, set it to 0 scores saved
                    lbpr.close ();
                }

                PrintWriter pr = new PrintWriter (new FileWriter (reference [gameNum] + ".txt"));
                pr.println (reference [gameNum] + "\n" + gameDataInt [1] [0] [0] + "\n0\n-1\n" + gameDataInt [1] [0] [0] + "\n0\n0"); //Game name, number of players, who's turn it is, whether to change to next player, amount of players still alive, and last left and right dice roll


                for (int i = 1 ; i <= gameDataInt [1] [0] [0] ; i++)
                {
                    pr.println (gameDataStr [1] [i] + "\n" + gameDataInt [2] [i] [0]); //Name and Model
                    pr.println ("1500\n0\n0\n0"); //Balance, Number of Properties, Number of Cards, and Position

                    gameDataInt [3] [i] [0] = 1500;
                }
                for (int i = 0 ; i < 40 ; i++)
                    pr.println ("0\n0");
                for (int i = 0 ; i <= 4 ; i++)
                    pr.println ("0");
                pr.println ("terminus");
                pr.close ();
                return;
            }
            catch (IOException e)
            {
                JOptionPane.showMessageDialog (null, "An error occured saving game data. Please restart the game.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            catch (Exception e)
            {
                JOptionPane.showMessageDialog (null, "Something went wrong. Please restart the game.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        else
        {
            while (true)    //Read in the game name or number
            {
                try
                {
                    title ();   //Clears screen and outputs title
                    c.setColor (Color.white);
                    c.fillRect (40, 50, 600, 450);

                    for (int i = 0 ; i < 9 ; i++)
                    {
                        c.setCursor (5, i + 5);
                        c.print ((i + 1) + " " + reference [i]);
                    }

                    c.setColor (Color.black);
                    c.setFont (new Font ("Raleway-Light", Font.BOLD, 20));
                    c.drawString ("Replace a Game", 300, 200);

                    c.drawLine (300, 220, 320, 220);

                    c.setColor (Color.darkGray);
                    c.setFont (new Font ("Raleway-Light", Font.PLAIN, 12));
                    c.drawString ("Enter the ID of the game you wish to replace (1-10), or -1 to exit: ", 300, 250);
                    c.setCursor (10, 15);



                    //Reads in whatever the user inputted
                    c.setColor (new Color (129, 169, 143));
                    c.setFont (new Font ("Raleway-Light", Font.PLAIN, 17));
                    String convert = "";
                    char letter = ' ';
                    while (letter != '\n' || convert.length () == 1)
                    {
                        if (letter != 8 && letter != '\n' && letter != '\t' && convert.length () <= 42)
                            convert += letter;
                        else if (letter == 8 && convert.length () >= 2)
                            convert = convert.substring (0, convert.length () - 1);
                        c.setColor (Color.white);
                        c.fillRect (245, 255, 395, 40);
                        c.setColor (new Color (129, 169, 143));
                        c.drawString (convert, 250, 285);
                        letter = c.getChar ();
                    }
                    gameNum = Integer.parseInt (convert.substring (1));



                    if (gameNum == -1)
                    {
                        move = 6;

                        option = -1;
                        return;
                    }
                    else if (gameNum > 10 || gameNum < 1) //Checks whether the game name is over 10 or below 1
                    {
                        throw new Exception (); //If it is, restart the input prompt
                    }
                    break;
                }
                catch (Exception e)
                {
                    JOptionPane.showMessageDialog (null, "Please enter a game ID between 1 and 8.", "Error", JOptionPane.ERROR_MESSAGE);
                } //Throw the exception and show the error panel
            }

            newGame (); //create the game with the updated game number
        }
    }






    /*
        resumeGame()
        ---
        If a game file has already been created, this method allows a user to resume a past game they paused
        
        Type    Name            Purpose
        String  convert         This stores the raw String version of what the user enters
        String  fileGameName    This stores the file name of the game the user wants to resume / to be checked for
        char    letter          This stores the most recent letter the user has entered
    */
    public void resumeGame ()
    {
        String fileGameName = "";

        while (option == 2)
        {
            while (true)    //Read in the game name or number
            {
                try //Read in slot to replace
                {
                    title ();   //Clears screen and outputs title
                    c.setColor (Color.white);
                    c.fillRect (40, 50, 600, 450);

                    for (int i = 0 ; i < 9 ; i++)
                    {
                        c.setCursor (i + 5, 10);
                        if (reference [i] != null)
                            c.print ((i + 1) + " " + reference [i]);
                        else
                            c.print ((i + 1) + " (empty)");
                    }

                    c.setColor (Color.black);
                    c.setFont (new Font ("Raleway-Light", Font.BOLD, 20));
                    c.drawString ("Resume a Game", 255, 200);

                    c.drawLine (255, 220, 280, 220);

                    c.setColor (Color.darkGray);
                    c.setFont (new Font ("Raleway-Light", Font.PLAIN, 12));
                    c.drawString ("Enter the ID of the game you wish to select (1-10), or -1 to exit: ", 255, 250);
                    c.setCursor (16, 33);


                    //Reads in whatever the user inputted
                    c.setColor (new Color (129, 169, 143));
                    c.setFont (new Font ("Raleway-Light", Font.PLAIN, 17));
                    String convert = "";
                    char letter = ' ';

                    while (letter != '\n' || convert.length () == 1)
                    {
                        if (letter != 8 && letter != '\n' && letter != '\t' && convert.length () <= 42)
                            convert += letter;
                        else if (letter == 8 && convert.length () >= 2)
                            convert = convert.substring (0, convert.length () - 1);
                        c.setColor (Color.white);
                        c.fillRect (245, 255, 395, 40);
                        c.setColor (new Color (129, 169, 143));
                        c.drawString (convert, 250, 280);
                        letter = c.getChar ();
                    }
                    gameNum = Integer.parseInt (convert.substring (1));

                    if (gameNum == -1)
                    {
                        move = 6;
                        option = -1;
                        return;
                    }
                    else if (gameNum > 10 || gameNum < 1) //Checks whether the game name is over 10 or below 1
                    {
                        throw new Exception (); //If it is, restart the input prompt
                    }
                    gameNum--;
                    break;

                }
                catch (Exception e)
                {
                    JOptionPane.showMessageDialog (null, "Please enter a game ID between 1 and 10.", "Error", JOptionPane.ERROR_MESSAGE);
                } //Throw the exception and show the error panel
            }

            while (true)
            {
                try //Read in slot to replace
                {
                    c.setColor (new Color (129, 169, 143));
                    c.fillRect (0, 0, 640, 500);

                    //Outputs the background
                    c.setColor (Color.white);
                    c.fillRect (0, 0, 40, 500);
                    c.fillRect (0, 0, 640, 50);

                    //Display the MONOPOLY title
                    c.setColor (Color.black);
                    c.setFont (new Font ("MonopolyTitle", Font.PLAIN, 20)); //Set the font for the title
                    c.drawString ("MONOPOLY\n", 265, 32); //Displays the title

                    c.setFont (new Font ("Raleway-Light", Font.PLAIN, 14));
                    c.setColor (Color.black);
                    for (int i = 0 ; i < 9 ; i++)
                    {
                        if (reference [i] != null)
                            c.drawString ((i + 1) + " " + reference [i], 90, 90 + 20 * i);
                        else
                            c.drawString ((i + 1) + "  (empty)", 90, 90 + 20 * i);
                    }

                    c.setColor (Color.darkGray);
                    c.setFont (new Font ("Raleway-Light", Font.BOLD, 20));
                    c.drawString ("Resume a Game", 255, 200);

                    c.drawLine (255, 220, 280, 220);

                    c.setFont (new Font ("Raleway-Light", Font.PLAIN, 12));
                    c.drawString ("Enter the name of game, -1 for Main Menu, 0 to resume: (<= 42 chars)", 255, 250);
                    c.setCursor (16, 33);



                    //Reads in whatever the user inputted
                    c.setColor (Color.white);
                    c.setFont (new Font ("Raleway-Light", Font.PLAIN, 17));
                    fileGameName = "";
                    char letter = ' ';

                    while (letter != '\n' || fileGameName.length () == 1)
                    {
                        if (letter != 8 && letter != '\n' && letter != '\t' && fileGameName.length () <= 42)
                            fileGameName += letter;
                        else if (letter == 8 && fileGameName.length () >= 2)
                            fileGameName = fileGameName.substring (0, fileGameName.length () - 1);
                        c.setColor (new Color (129, 169, 143));
                        c.fillRect (245, 250, 395, 40);
                        c.setColor (Color.white);
                        c.drawString (fileGameName, 250, 280);
                        letter = c.getChar ();
                    }
                    fileGameName = fileGameName.substring (1);


                    if (fileGameName.equals ("0"))
                        fileGameName = reference [gameNum];
                    else if (fileGameName.equals ("-1"))
                    {
                        option = -1;
                        move = 6;
                        return;
                    }
                }
                catch (Exception e)
                {
                    JOptionPane.showMessageDialog (null, "Please enter a game ID between 1 and 10.", "Error", JOptionPane.ERROR_MESSAGE);
                } //Throw the exception and show the error panel



                try
                {
                    BufferedReader br = new BufferedReader (new FileReader (fileGameName + ".txt"));
                    reference [gameNum] = br.readLine ();   //Name of the game
                    gameDataInt [1] [0] [0] = Integer.parseInt (br.readLine ());    //Amount of players
                    gameDataInt [1] [1] [0] = Integer.parseInt (br.readLine ());    //Player turn

                    gameDataInt [1] [2] [0] = Integer.parseInt (br.readLine ());    //Whether to move on to next players turn or not
                    gameDataInt [1] [3] [0] = Integer.parseInt (br.readLine ());    //Amount of players still playing
                    gameDataInt [1] [4] [0] = Integer.parseInt (br.readLine ());    //The roll from the left dice
                    gameDataInt [1] [4] [1] = Integer.parseInt (br.readLine ());    //The roll from the right dice

                    for (int i = 1 ; i <= gameDataInt [1] [0] [0] ; i++)
                    {
                        gameDataStr [1] [i] = br.readLine (); //Player Name
                        gameDataInt [2] [i] [0] = Integer.parseInt (br.readLine ()); //Model
                        gameDataInt [3] [i] [0] = Integer.parseInt (br.readLine ()); //Balance

                        gameDataInt [4] [i] [0] = Integer.parseInt (br.readLine ()); //Properties
                        for (int j = 1 ; j <= gameDataInt [4] [i] [0] ; j++)
                        {
                            gameDataInt [4] [i] [j] = Integer.parseInt (br.readLine ()); //Read in all the Property IDs
                        }

                        gameDataInt [5] [i] [0] = Integer.parseInt (br.readLine ()); //Cards
                        for (int j = 1 ; j <= gameDataInt [5] [i] [0] ; j++)
                        {
                            gameDataInt [5] [i] [j] = Integer.parseInt (br.readLine ()); //Read in all the Card IDs
                        }

                        gameDataInt [6] [i] [0] = Integer.parseInt (br.readLine ()); //Position on the Board
                    }
                    for (int i = 0 ; i < 40 ; i++)
                    {
                        gameDataInt [7] [i] [0] = Integer.parseInt (br.readLine ()); //Who owns a property
                        gameDataInt [7] [i] [1] = Integer.parseInt (br.readLine ()); //The degree to which they've upgraded it
                    }
                    for (int i = 0 ; i <= 4 ; i++)
                        gameDataInt [8] [i] [0] = Integer.parseInt (br.readLine ()); //If in jail, how many turns have elapsed
                    option = -1;
                    break;
                }
                catch (FileNotFoundException fnfe)
                {
                    JOptionPane.showMessageDialog (null, "This file could not be found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
                catch (NumberFormatException nfe)
                {
                    JOptionPane.showMessageDialog (null, "This file was not formatted correctly.", "Error", JOptionPane.ERROR_MESSAGE);
                }
                catch (IOException ioe)
                {
                    JOptionPane.showMessageDialog (null, "Something went wrong writing the file. Please retry.", "Error", JOptionPane.ERROR_MESSAGE);
                }
                catch (Exception e)
                {
                    JOptionPane.showMessageDialog (null, "Please enter a game ID between 1 and 10.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        option = 2;
    }

    
    

  
    /*
        exitGameMenu()
        ---
        This is the menu shown before the user exits the game, be it when they forfeit, pause the game, or win
        
        Type        Name        Purpose
        int         tmp         This holds the user's input after being asked whether they wish to continue or not
        String[]    namesLB     This will temporarily store all the usernames of the leaderboard, and if the current score is better than one, updates
                                the leaderboard
        int[]       scoresLB    Likewise, this temporarily stores all the scores on the leaderboard, which gets updated
    */
    private void exitGameMenu ()
    {
        title ();
        c.setColor (Color.white);
        c.setFont (new Font ("Calibri", Font.PLAIN, 30));
        if (gameDataInt [1] [3] [0] == 0)
        {
            c.drawString ("Congratulations " + gameDataStr [1] [gameDataInt [1] [1] [0]] + "!", 320 - 13 * (("Congratulations " + gameDataStr [1] [gameDataInt [1] [1] [0]] + "!").length () / 2), 195);
            c.setFont (new Font ("Calibri", 1, 30));
            c.drawString ("You have won the game!", 140, 235);
        }
        else if (gameDataInt [0] [gameDataInt [1] [1] [0]] [0] == -1)
            c.drawString ("Are you sure you would like to forfeit?", 70, 230);
        else
            c.drawString ("Are you sure you would like to exit the game?", 30, 230);

        c.drawLine (300, 253, 340, 253);

        c.setColor (Color.lightGray);
        c.setFont (new Font ("Raleway-Light", Font.PLAIN, 15));
        if (gameDataInt [1] [3] [0] != 0)
            c.drawString ("Any key to proceed, or 1 to return", 200, 280);
        else
            c.drawString ("Any key to proceed", 260, 280);
        int tmp = c.getChar ();

        if (tmp != '1' || gameDataInt [1] [3] [0] == 0) //If they wish to exit
        {
            if (gameDataInt [0] [gameDataInt [1] [1] [0]] [0] == -1 && gameDataInt [1] [3] [0] > 0)
            {
                move = 1;
                gameDataInt [1] [2] [0] = -1;
            }
            else
            {
                move = 6;
                option = -1;
            }

            try
            {
                BufferedReader br = new BufferedReader (new FileReader ("Leaderboards.txt"));
                String namesLB[] = new String [11];
                int scoresLB[] = new int [11];
                scoresLB [0] = Integer.parseInt (br.readLine ());
                for (int i = 1 ; i <= scoresLB [0] ; i++)
                {
                    namesLB [i] = br.readLine ();
                    scoresLB [i] = Integer.parseInt (br.readLine ());

                    if (scoresLB [i] <= gameDataInt [3] [gameDataInt [1] [1] [0]] [0])
                    {
                        if (i < 10)
                        {
                            namesLB [i + 1] = namesLB [i];
                            scoresLB [i + 1] = scoresLB [i];
                        }
                        namesLB [i] = gameDataStr [1] [gameDataInt [1] [1] [0]];
                        scoresLB [i] = gameDataInt [3] [gameDataInt [1] [1] [0]] [0];
                        i++;
                    }
                }
                scoresLB [0] = Math.min (scoresLB [0] + 1, 10);
                if (scoresLB [0] < 10 && (scoresLB [0] == 1 || scoresLB [scoresLB [0] - 1] > gameDataInt [3] [gameDataInt [1] [1] [0]] [0]))
                {
                    namesLB [scoresLB [0]] = gameDataStr [1] [gameDataInt [1] [1] [0]];
                    scoresLB [scoresLB [0]] = gameDataInt [3] [gameDataInt [1] [1] [0]] [0];
                }
                br.close ();

                PrintWriter pr = new PrintWriter (new FileWriter ("Leaderboards.txt"));

                pr.println (scoresLB [0]);
                for (int i = 1 ; i <= scoresLB [0] ; i++)
                {
                    pr.println (namesLB [i] + "\n" + scoresLB [i]);
                }

                pr.close ();
            }
            catch (IOException ioe)
            {
                JOptionPane.showMessageDialog (null, "An error occured saving game data. Please restart the game.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            catch (NumberFormatException nfe)
            {
                JOptionPane.showMessageDialog (null, "The leaderboards file may have been tampered or corrupted. Please restart the game.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            catch (Exception e)
            {
                JOptionPane.showMessageDialog (null, "Something went wrong. Please restart the game.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        else
        { //If they wish to go back, reset the move to display, change the user's status back to alive, and set option for playing the game, and add back one player
            move = 1;
            gameDataInt [0] [gameDataInt [1] [1] [0]] [0] = 0;
            gameDataInt [1] [2] [0] = 0;
            gameDataInt [1] [3] [0]++;
            option = 1;
        }
        saveScore ();
    }


    /*
        newCard()
        ---
        If the user is to receive a new card from Chance/Community chest, and apply its effects
        
        Type    Name        Purpose
        int     playerNum   Stores the player that the game is currently on
    */
    private void newCard ()
    {
        title ();
        move = 1; //Go back to game after

        int playerNum = gameDataInt [1] [1] [0];
        gameDataInt [5] [playerNum] [0]++;
        gameDataInt [5] [playerNum] [gameDataInt [5] [playerNum] [0]] = randomGen (15);

        switch (gameDataInt [5] [playerNum] [gameDataInt [5] [playerNum] [0]])
        {
            case 0: //Advance to go
                gameDataInt [6] [playerNum] [0] = 0;
                gameDataInt [3] [playerNum] [0] += 200;
                break;

            case 1: //Bank error +200
                gameDataInt [3] [playerNum] [0] += 200;
                break;

            case 2: //Doctors fee -50
                gameDataInt [3] [playerNum] [0] -= 50;
                break;

            case 3: //Sale of stock +50
                gameDataInt [3] [playerNum] [0] += 50;
                break;

            case 4: //Out of jail  free
                break;

            case 5: //Go to Jail
                gameDataInt [6] [playerNum] [0] = 10;
                gameDataInt [8] [playerNum] [0] = 3;
                break;

            case 6: //Holiday fund +100
                gameDataInt [6] [playerNum] [0] += 100;
                break;

            case 7: //Income  tax refund +20
                gameDataInt [6] [playerNum] [0] += 20;
                break;

            case 8: //Birthday +10  each player
                for (int i = 1 ; i <= gameDataInt [1] [0] [0] ; i++)
                {
                    if (gameDataInt [0] [i] [0] != -1 && i != playerNum)
                    {
                        gameDataInt [3] [i] [0] -= 10;
                        gameDataInt [3] [playerNum] [0] += 10;
                    }
                }
                break;

            case 9:    //Life insurance matures +100
                gameDataInt [3] [playerNum] [0] += 100;
                break;

            case 10:    //Hospital fees -50
                gameDataInt [3] [playerNum] [0] -= 50;
                break;

            case 11:    //School fees -50
                gameDataInt [3] [playerNum] [0] -= 50;
                break;

            case 12:    //Assessed for repairs
                for (int i = 1 ; i <= gameDataInt [4] [playerNum] [0] ; i++)
                {
                    gameDataInt [3] [playerNum] [0] -= 50 * gameDataInt [7] [gameDataInt [4] [playerNum] [i]] [0]; //Subtract from the balance the degree of the i'th property the player owns
                }
                break;

            case 13:    //Second  in beauty contest  +10
                gameDataInt [3] [playerNum] [0] += 10;
                break;

            case 14:    //Inherit +100
                gameDataInt [3] [playerNum] [0] += 100;
                break;

            default:
                break;
        }

        c.setColor (Color.white);
        c.setFont (new Font ("Raleway-Light", Font.BOLD, 30));
        c.drawString ("You have a new card!", 170, 170);


        c.setColor (Color.black);
        c.setFont (new Font ("Raleway-Light", Font.BOLD, 15));
        c.drawString (cardDeck [gameDataInt [5] [playerNum] [gameDataInt [5] [playerNum] [0]]], 320 - (7 * (cardDeck [gameDataInt [5] [playerNum] [gameDataInt [5] [playerNum] [0]]].length ()) / 2), 200);

        c.setColor (Color.white);
        c.drawString ("Any key to continue", 250, 400);

        if (gameDataInt [5] [playerNum] [gameDataInt [5] [playerNum] [0]] != 4)
        {
            gameDataInt [5] [playerNum] [gameDataInt [5] [playerNum] [0]] = 0;
            gameDataInt [5] [playerNum] [0]--;
        }
        //print out card


        pauseProgram ();

    }


    
    /*
        viewCardsProps()
        ---
        This shows all of a user's properties, their ID if they wish to sell, their level upgraded, and if they have any Get Out of Jail cards
        
        Type    Name        Purpose
        int     playerNum   This stores the current player's ID
        String  phrase      This will store the raw String the user enters while they type the full phrase
        int     action      This stores the converted integer of what the user entered in the variable phrase
        char    letter      Keeps the latest character entered
    */
    private void viewCardsProps ()
    {
        c.setColor (new Color (129, 169, 143));
        c.fillRect (0, 0, 640, 500);

        move = 1;
        int playerNum = gameDataInt [1] [1] [0];
        c.setFont (new Font ("Raleway-Light", Font.BOLD, 20));
        c.setColor (Color.white);
        c.drawString (gameDataStr [1] [playerNum], 50, 50);

        c.setFont (new Font ("Raleway-Light", Font.BOLD, 15));
        c.setColor (Color.black);
        c.drawString ("Level", 50, 80);
        c.drawString ("ID", 100, 80);
        c.drawString ("Property", 140, 80);
        c.drawString ("Cards", 350, 80);

        c.setFont (new Font ("Raleway-Light", Font.BOLD, 12));
        c.setColor (Color.darkGray);
        for (int i = 1 ; i <= gameDataInt [4] [playerNum] [0] ; i++)
        {
            c.drawString ("" + gameDataInt [4] [playerNum] [i], 100, 100 + (i - 1) * 14); //Draws the option  to remove
            c.drawString ("" + gameDataInt [7] [gameDataInt [4] [playerNum] [i]] [1], 50, 100 + (i - 1) * 14); //Draws the upgrade status of the property
            switch (gameDataInt [4] [playerNum] [i])
            {
                case 1:
                    c.drawString ("Mediterranean Avenue", 140, 100 + (i - 1) * 14);
                    break;

                case 3:
                    c.drawString ("Baltic Avenue", 140, 100 + (i - 1) * 14);
                    break;

                case 5:
                    c.drawString ("Reading Railroad", 140, 100 + (i - 1) * 14);
                    break;

                case 6:
                    c.drawString ("Oriental Avenue", 140, 100 + (i - 1) * 14);
                    break;

                case 8:
                    c.drawString ("Vermont Avenue", 140, 100 + (i - 1) * 14);
                    break;

                case 9:
                    c.drawString ("Connecticut Avenue", 140, 100 + (i - 1) * 14);
                    break;

                case 11:
                    c.drawString ("St. Charles Place", 140, 100 + (i - 1) * 14);
                    break;

                case 12:
                    c.drawString ("Electric Company", 140, 100 + (i - 1) * 14);
                    break;

                case 13:
                    c.drawString ("States Avenue", 140, 100 + (i - 1) * 14);
                    break;

                case 14:
                    c.drawString ("Virginia Avenue", 140, 100 + (i - 1) * 14);
                    break;

                case 15:
                    c.drawString ("Pennsylvania", 140, 100 + (i - 1) * 14);
                    break;

                case 16:
                    c.drawString ("St. James Place", 140, 100 + (i - 1) * 14);
                    break;

                case 18:
                    c.drawString ("Tennessee Avenue", 140, 100 + (i - 1) * 14);
                    break;

                case 19:
                    c.drawString ("New York Avenue", 140, 100 + (i - 1) * 14);
                    break;

                case 21:
                    c.drawString ("Kentucky Avenue", 140, 100 + (i - 1) * 14);
                    break;

                case 23:
                    c.drawString ("Indiana Avenue", 140, 100 + (i - 1) * 14);
                    break;

                case 24:
                    c.drawString ("Illinois Avenue", 140, 100 + (i - 1) * 14);
                    break;

                case 25:
                    c.drawString ("B&O Railroad", 140, 100 + (i - 1) * 14);
                    break;

                case 26:
                    c.drawString ("Atlantic Avenue", 140, 100 + (i - 1) * 14);
                    break;

                case 27:
                    c.drawString ("Ventnor Avenue", 140, 100 + (i - 1) * 14);
                    break;

                case 28:
                    c.drawString ("Water Works", 140, 100 + (i - 1) * 14);
                    break;

                case 29:
                    c.drawString ("Marvin Gardens", 140, 100 + (i - 1) * 14);
                    break;

                case 31:
                    c.drawString ("Pacific Avenue", 140, 100 + (i - 1) * 14);
                    break;

                case 32:
                    c.drawString ("North Carolina Avenue", 140, 100 + (i - 1) * 14);
                    break;

                case 34:
                    c.drawString ("Pennsylvania Avenue", 140, 100 + (i - 1) * 14);
                    break;

                case 35:
                    c.drawString ("Short Line", 140, 100 + (i - 1) * 14);
                    break;

                case 37:
                    c.drawString ("Park Place", 140, 100 + (i - 1) * 14);
                    break;

                case 39:
                    c.drawString ("Boardwalk", 140, 100 + (i - 1) * 14);
                    break;

                default:
                    break;
            }
        }

        for (int i = 1 ; i <= gameDataInt [5] [playerNum] [0] ; i++)
        {
            //The only card that does not have an immediate effect is "Get out of Jail"
            c.drawString ("Get out of Jail free", 350, 100 + (i - 1) * 14);
        }

        c.setColor (Color.white);
        c.drawString ("Enter the ID beside a property to sell, or anything", 300, 380);
        c.drawString ("else to return (less than/equal to 32 chars):", 300, 400);

        c.setCursor (25, 50);
        String phrase = "";
        char letter = ' ';
        c.setFont (new Font ("Raleway-Light", Font.PLAIN, 17));
        while (letter != '\n' || phrase.length () == 1)
        {
            if (letter != 8 && letter != '\n' && letter != '\t' && phrase.length () <= 32)
                phrase += letter;
            else if (letter == 8 && phrase.length () >= 2)
                phrase = phrase.substring (0, phrase.length () - 1);
            c.setColor (new Color (129, 169, 143));
            c.fillRect (280, 410, 360, 85);
            c.setColor (Color.white);
            c.drawString (phrase, 295, 430);
            letter = c.getChar ();
        }



        try
        {
            int action = Integer.parseInt (phrase.substring (1));

            boolean bol = false;
            for (int j = 1 ; j <= gameDataInt [4] [playerNum] [0] ; j++)
            {
                if (gameDataInt [4] [playerNum] [j] == action)
                {
                    bol = true;
                    for (int k = j + 1 ; k <= gameDataInt [4] [playerNum] [0] ; k++)
                        gameDataInt [4] [playerNum] [k - 1] = gameDataInt [4] [playerNum] [k];
                }
            }
            int sellValue = 0;

            if (bol)
            {
                switch (action)
                {
                    case 1:
                        //Mediterranean Ave. (1)
                        sellValue = (int) ((60.0 + (50 * (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1] - 1))) * 0.75);
                        break;

                    case 3:
                        //Baltic Ave. (3)
                        sellValue = (int) ((60.0 + (50 * (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1] - 1))) * 0.75);
                        break;

                    case 5:
                        //Reading Railroad (5)
                        sellValue = 150;
                        break;


                    case 6:
                        //Oriental Ave. (6)
                        sellValue = (int) ((100.0 + (50 * (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1] - 1))) * 0.75);
                        break;

                    case 8:
                        //Vermont Ave. (8)
                        sellValue = (int) ((100.0 + (50 * (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1] - 1))) * 0.75);
                        break;


                    case 9:
                        //Conneticut Ave. (9)
                        sellValue = (int) ((120.0 + (50 * (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1] - 1))) * 0.75);
                        break;

                    case 11:
                        //St. Charles (11)
                        sellValue = (int) ((140.0 + (100 * (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1] - 1))) * 0.75);
                        break;


                    case 12:
                        //Electric Company (12)
                        sellValue = 112;
                        break;


                    case 13:
                        //States Avenue (13)
                        sellValue = (int) ((140.0 + (100 * (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1] - 1))) * 0.75);
                        break;


                    case 14:
                        //Virginia Avenue (14)
                        sellValue = (int) ((160.0 + (100 * (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1] - 1))) * 0.75);
                        break;


                    case 15:
                        //Pennsylvania Railroad (15)
                        sellValue = 150;
                        break;

                    case 16:
                        //St. James Place (16)
                        sellValue = (int) ((180.0 + (100 * (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1] - 1))) * 0.75);
                        break;

                    case 18:
                        //Tennessee Avenue (18)
                        sellValue = (int) ((180.0 + (100 * (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1] - 1))) * 0.75);
                        break;


                    case 19:
                        //New York Avenue (19)
                        sellValue = (int) ((200.0 + (100 * (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1] - 1))) * 0.75);
                        break;

                    case 21:
                        //Kentucky Avenue (21)
                        sellValue = (int) ((220.0 + (150 * (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1] - 1))) * 0.75);
                        break;

                    case 23:
                        //Indiana Avenue (23)
                        sellValue = (int) ((220.0 + (150 * (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1] - 1))) * 0.75);
                        break;


                    case 24:
                        //Illinois Avenue (24)
                        sellValue = (int) ((240.0 + (150 * (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1] - 1))) * 0.75);
                        break;


                    case 25:
                        //Pennsylvania Railroad (25)
                        sellValue = 150;
                        break;


                    case 26:
                        //Atlantic Avenue (26)
                        sellValue = (int) ((260.0 + (150 * (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1] - 1))) * 0.75);
                        break;


                    case 27:
                        //Ventnor Avenue (27)
                        sellValue = (int) ((260.0 + (150 * (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1] - 1))) * 0.75);
                        break;


                    case 28:
                        //Electric Company (28)
                        sellValue = 112;
                        break;


                    case 29:
                        //Marvin Gardens (29)
                        sellValue = (int) ((280.0 + (150 * (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1] - 1))) * 0.75);
                        break;

                    case 31:
                        //Pacific Avenue (31)
                        sellValue = (int) ((300.0 + (200 * (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1] - 1))) * 0.75);
                        break;


                    case 32:
                        //North Carolina Avenue (32)
                        sellValue = (int) ((300.0 + (200 * (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1] - 1))) * 0.75);
                        break;

                    case 34:
                        //Pennsylvania Avenue (34)
                        sellValue = (int) ((320.0 + (90 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1])) * 0.75);
                        break;


                    case 35:
                        //Short Line (35)
                        sellValue = 150;
                        break;

                    case 37:
                        //Park Place (37)
                        sellValue = (int) ((350.0 + (200 * (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1] - 1))) * 0.75);
                        break;

                    case 39:
                        //Boardwalk (39)
                        sellValue = (int) ((400.0 + (200 * (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1] - 1))) * 0.75);
                        break;

                    default:
                        break;

                }
                gameDataInt [4] [playerNum] [gameDataInt [4] [playerNum] [0]] = 0;
                gameDataInt [7] [action] [0] = 0;
                gameDataInt [7] [action] [1] = 0;
                gameDataInt [4] [playerNum] [0]--;
                gameDataInt [3] [playerNum] [0] += sellValue;
            }
        }
        catch (NumberFormatException nfe)
        {
        }
        catch (Exception e)
        {
        }
    }


    
    /*
        saveScore()
        ---
        Reads in the game file, and then writes the array back with the updates
    */
    private void saveScore ()
    {
        try
        {
            PrintWriter pr = new PrintWriter (new FileWriter (reference [gameNum] + ".txt"));
            pr.println (reference [gameNum] + "\n" + gameDataInt [1] [0] [0] + "\n" + gameDataInt [1] [1] [0] + "\n" + gameDataInt [1] [2] [0] + "\n" + gameDataInt [1] [3] [0]);  //Game name, number of players, player turn, whether to move on to the next player or not, and players alive
            pr.println (gameDataInt [1] [4] [0] + "\n" + gameDataInt [1] [4] [1]);

            for (int i = 1 ; i <= gameDataInt [1] [0] [0] ; i++)
            {
                pr.println (gameDataStr [1] [i] + "\n" + gameDataInt [2] [i] [0]); //Name and Model
                pr.println (gameDataInt [3] [i] [0] + "\n" + gameDataInt [4] [i] [0]); //Balance and Amount of Properties

                for (int j = 0 ; j < gameDataInt [4] [i] [0] ; j++) //Properties owned
                    pr.println (gameDataInt [4] [i] [j]);

                pr.println (gameDataInt [5] [i] [0]);   //Amount of Cards
                for (int j = 0 ; j < gameDataInt [5] [i] [0] ; j++) //Cards owned
                    pr.println (gameDataInt [5] [i] [j]);

                pr.println (gameDataInt [6] [i] [0]);   //Current position on the board
            }
            for (int i = 0 ; i < 40 ; i++)
                pr.println (gameDataInt [7] [i] [0] + "\n" + gameDataInt [7] [i] [1]);  //Ownership of each property and upgraded level

            for (int i = 0 ; i <= 4 ; i++)
                pr.println (gameDataInt [8] [i] [0]);   //Turns elapsed in jail

            pr.println ("terminus");
            pr.close ();
            return;
        }
        catch (IOException ioe)
        {
            JOptionPane.showMessageDialog (null, "An error occured saving game data. Please restart the game.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog (null, "Something went wrong. Please restart the game.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    /*
        randomGen()
        ---
        Generates a random number between 0 and what the user enters
        
        Type    Name        Purpose
        int     upperLimit  Takes it in as the upper bound on what to generate
    */
    private int randomGen (int upperLimit)
    { //Generates a number between 0 and the upper limit
        return ((int) (Math.random () * upperLimit)); //returns it as an int
    }

    
    /*
        leaderboard()
        ---
        Displays the leaderboard file to the user in a table
        
        Type    Name        Purpose
        int     amt         Store the amount of entries in the leaderboards file
    */
    private void leaderboard ()
    {
        title ();

        c.setColor (Color.white);
        c.setFont (new Font ("Raleway-Light", Font.BOLD, 25));
        c.drawString ("Leaderboard", 50, 75);

        c.setFont (new Font ("Raleway-Light", Font.BOLD, 15));
        c.drawString ("Any key to return", 50, 480);

        c.setColor (Color.darkGray);
        c.drawString ("Rank", 50, 100);
        c.drawString ("Username", 100, 100);
        c.drawString ("Score (Balance)", 400, 100);

        c.setFont (new Font ("Raleway-Light", Font.PLAIN, 14));
        try
        {
            BufferedReader br = new BufferedReader (new FileReader ("Leaderboards.txt"));
            int amt = Integer.parseInt (br.readLine ());
            for (int i = 1 ; i <= amt ; i++)
            {
                c.drawString (i + ".", 50, 100 + (i * 20));
                c.drawString (br.readLine (), 100, 100 + (i * 20));
                c.drawString (br.readLine (), 400, 100 + (i * 20));
            }
            br.close ();

            if (amt == 0)
                c.drawString ("(No entries recorded)", 50, 120);
        }
        catch (IOException ioe)
        {
            JOptionPane.showMessageDialog (null, "An error occured displaying the leaderboards. Please restart.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog (null, "Something went wrong. Please restart.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        pauseProgram ();
        option = -1;
    }


    private void resetData ()
    {
        try
        {
            PrintWriter pr = new PrintWriter (new FileWriter ("Leaderboards.txt"));
            pr.close ();
            JOptionPane.showMessageDialog (null, "Leaderboard data has been erased", "Success!", JOptionPane.ERROR_MESSAGE);
        }
        catch (IOException ioe)
        {
            JOptionPane.showMessageDialog (null, "An error occured erasing the leaderboards. Please restart.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog (null, "Something went wrong. Please restart.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        option = -1;
    }


    /*
        mainMenu()
        ---
        Displays all options for the user
        
        Type    Name    Purpose
        File    lb      Check if the leaderboard file exists
    */
    public void mainMenu ()
    {
        try
        {
            File lb = new File ("Leaderboards.txt");  //Makes leaderboard file, in case the user wishes to see it
            if (lb.length () == 0)
            {
                PrintWriter lbpr = new PrintWriter (new FileWriter ("Leaderboards.txt"));
                lbpr.println (0); //If the leaderboard file isn't made, set it to 0 scores saved
                lbpr.close ();
            }
        }
        catch (IOException e)
        {
            JOptionPane.showMessageDialog (null, "An error occured saving game data. Please restart the game.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog (null, "Something went wrong. Please restart the game.", "Error", JOptionPane.ERROR_MESSAGE);
        }




        title ();

        c.setColor (Color.white);
        c.fillRect (40, 50, 600, 450);

        c.setFont (new Font ("Raleway-Light", 1, 15));
        c.setColor (Color.black);
        String indent = "     ";
        c.drawString ("Select an option:", 90, 120);  //Outputs the options menu

        c.setFont (new Font ("Raleway-Light", 0, 12));
        c.drawString (indent + "1 - New Game", 240, 100);
        c.drawString (indent + "2 - Resume", 240, 120);
        c.drawString (indent + "3 - Leaderboards", 240, 140);
        c.drawString (indent + "4 - Instructions", 240, 160);
        c.drawString (indent + "5 - Reset Data", 240, 180);
        c.drawString (indent + "6 - Exit\n\n", 240, 200);


        c.setColor (Color.lightGray);   //Draws the line under the title
        c.drawLine (230, 75, 230, 225);

        c.setColor (Color.black);
        c.drawString ("Input the number of the option you would like to select: ", 90, 260); //Prompts user for the option they would like
    }



    /*
        askData()
        ---
        Receives the user's input choice for the main menu
        
        Type    Name        Purpose
        String  convert     Stores the raw String of what the user types in letter by lettter
        char    letter      Stores the latest letter entered by the user
        int     temp        Stores the integer representation of what the user chose
    */
    public void askData ()
    {
        try
        {
            c.setFont (new Font ("Raleway-Light", Font.PLAIN, 17));
            c.setColor (Color.white);
            String convert = "";
            char letter = ' ';
            while (letter != '\n' || convert.length () == 1)
            {
                if (letter != 8 && convert.length () <= 42)
                    convert += letter;
                else if (letter == 8 && letter != '\n' && letter != '\t' && convert.length () >= 2)
                    convert = convert.substring (0, convert.length () - 1);

                c.setColor (Color.white);
                c.fillRect (70, 265, 570, 40);

                c.setColor (new Color (129, 169, 143));
                c.drawString (convert, 85, 290);
                letter = c.getChar ();
            }
            int temp = Integer.parseInt (convert.substring (1)); //Attempts to convert the users input into an integer

            if (temp < 1 || temp > 6)  //Checks whether the input is a valid option from 1-6
            {
                throw new Exception (); //If not, throw an exception to restart the input
            }
            option = temp;
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog (null, "Please enter a valid integer menu option from 1-6.", "Error", JOptionPane.ERROR_MESSAGE);
        } //Throw the exception and show the error panel
    }

    
    
    //Purpose: display the board, dice, and the main game
    /*
        display()
        ---
        Runs the main aspects of the game, and displays all graphics of the board and options for the user
        
        Type        Name        Purpose
        int         x           Used to store the top left corner's x of a tile
        int         y           Used to store the top left corner's y value
        int         playerNum   Stores the current player who's turn it is
        int         rent        Stores the rent value for Utilities and Railroads, depending how many they own
        int         forSale     The amount of money (unique per tile) the user has to pay for an upgrade, purchase, fine, etc
        int         sellValue   The amount of money the player would receive if they sold the current property
        int         size        The size of the model token to print (shrinks for JAIL)
        String      message     The action the user would have to take (buy, upgrade, or pay fine for option 1)
    */
    public void display ()
    {
        int forSale = -1, sellValue = -1, playerNum = gameDataInt [1] [1] [0];
        String message = "";

        if (gameDataInt [1] [2] [0] == -1)
        { //If we move on to the next player, loop over to the next one, and play the next player screen
            gameDataInt [1] [2] [0] = 1; //Stay on this player, as it is now a new player

            playerNum = gameDataInt [1] [1] [0] % gameDataInt [1] [0] [0] + 1; //Next player   ([1][1][0] is who's turn it is, while [1][0][0] is the amount of players)
            gameDataInt [1] [1] [0] = playerNum;


            if (gameDataInt [0] [playerNum] [0] != -1)
            { //If the current player hasn't forfeited or gone bankrupt
                nextPlayer (gameDataInt [1] [1] [0]); //Prompts the game to be given to the next player


                if (gameDataInt [1] [3] [0] == 1)
                {
                    gameDataInt [1] [3] [0]--;
                    option = -1;
                    move = 2;
                    return;
                }


                c.setColor (Color.white);
                c.fillRect (40, 50, 600, 450);

                c.setColor (new Color (129, 169, 143));
                c.fillRect (0, 0, 640, 500);

                c.setColor (Color.darkGray);
                c.fillRect (140, 20, 360, 340);
                c.setFont (new Font ("Raleway-Light", Font.PLAIN, 17));
                c.drawString ("Any key to roll the dice", 240, 390);

                pauseProgram ();

                for (int i = 0 ; i < 35 ; i++)
                {
                    c.setColor (new Color (129, 169, 143, (10 * i) / 5)); //Fades out the prompt to roll the dice
                    c.fillRect (230, 370, 190, 30);
                    try
                    {
                        Thread.sleep (4);
                    } //Add a short delay before continuing with the program
                    catch (Exception e)
                    {
                    }
                }


                for (int z = 0 ; z < 719 ; z++) //Rolls the dice 4 times, (4  * 180)
                {
                    c.setColor (Color.darkGray);
                    c.fillRect (140, 20, 360, 340);
                    for (int x = 0 ; x < 2 ; x++)   //Goes through both dice
                    {
                        if (z % 180 == 0)
                            gameDataInt [1] [4] [x] = randomGen (6) + 1; //Randomizes the next dice number

                        c.setColor (Color.white);   //This will display the appropriate dot pattern for each dice result
                        if (gameDataInt [1] [4] [x] == 1)
                        {
                            c.fillOval ((x * 180) + 215, 70 + (z % 180), 30, 30);
                        }
                        else if (gameDataInt [1] [4] [x] == 2)
                        {
                            c.fillOval ((x * 180) + 180, 30 + (z % 180), 30, 30);
                            c.fillOval ((x * 180) + 260, 110 + (z % 180), 30, 30);
                        }
                        else if (gameDataInt [1] [4] [x] == 3)
                        {
                            c.fillOval ((x * 180) + 180, 45 + (z % 180), 30, 30);
                            c.fillOval ((x * 180) + 225, 80 + (z % 180), 30, 30);
                            c.fillOval ((x * 180) + 270, 115 + (z % 180), 30, 30);
                        }
                        else if (gameDataInt [1] [4] [x] == 4)
                        {
                            c.fillOval ((x * 180) + 180, 30 + (z % 180), 30, 30);
                            c.fillOval ((x * 180) + 260, 110 + (z % 180), 30, 30);
                            c.fillOval ((x * 180) + 180, 110 + (z % 180), 30, 30);
                            c.fillOval ((x * 180) + 260, 30 + (z % 180), 30, 30);
                        }
                        else if (gameDataInt [1] [4] [x] == 5)
                        {
                            c.fillOval ((x * 180) + 180, 30 + (z % 180), 30, 30);
                            c.fillOval ((x * 180) + 260, 110 + (z % 180), 30, 30);
                            c.fillOval ((x * 180) + 180, 110 + (z % 180), 30, 30);
                            c.fillOval ((x * 180) + 260, 30 + (z % 180), 30, 30);
                            c.fillOval ((x * 180) + 215, 70 + (z % 180), 30, 30);
                        }
                        else
                        {
                            c.fillOval ((x * 180) + 180, 20 + (z % 180), 30, 30);
                            c.fillOval ((x * 180) + 260, 75 + (z % 180), 30, 30);
                            c.fillOval ((x * 180) + 180, 130 + (z % 180), 30, 30);
                            c.fillOval ((x * 180) + 260, 20 + (z % 180), 30, 30);
                            c.fillOval ((x * 180) + 180, 75 + (z % 180), 30, 30);
                            c.fillOval ((x * 180) + 260, 130 + (z % 180), 30, 30);
                        }
                    }

                    try
                    {
                        Thread.sleep (Math.max (2, (z - 660) / 4));
                    } //Add a short delay before continuing with the program
                    catch (Exception e)
                    {
                    }
                }

                //Add money from GO, as long as they aren't in JAIL
                if (gameDataInt [6] [playerNum] [0] > (gameDataInt [6] [playerNum] [0] + gameDataInt [1] [4] [0] + gameDataInt [1] [4] [1]) % 40 && gameDataInt [8] [playerNum] [0] == 0)
                    gameDataInt [3] [playerNum] [0] += 200;

                if (gameDataInt [8] [playerNum] [0] > 0 && gameDataInt [1] [4] [0] != gameDataInt [1] [4] [1]) //If you are in jail and didn't roll a double
                    gameDataInt [8] [playerNum] [0]--;
                else    //Normal spots or you rolled a double
                    gameDataInt [6] [playerNum] [0] = (gameDataInt [6] [playerNum] [0] + gameDataInt [1] [4] [0] + gameDataInt [1] [4] [1]) % 40;


                for (int i = 0 ; i < 35 ; i++)  //Fade in the text showing the result of the roll, and prompt to move to the board
                {
                    c.setColor (new Color (255, 255, 255, (10 * i) / 5));
                    c.setFont (new Font ("Raleway-Light", Font.PLAIN, 50));
                    c.drawString ((gameDataInt [1] [4] [0] + gameDataInt [1] [4] [1]) + "", 35, 290);

                    c.setColor (new Color (81, 81, 81, (10 * i) / 5));
                    c.setFont (new Font ("Raleway-Light", Font.PLAIN, 17));
                    c.drawString ("Any key to continue to board", 200, 420);

                    try
                    {
                        Thread.sleep (4);
                    } //Add a short delay before continuing with the program
                    catch (Exception e)
                    {
                    }
                }
                pauseProgram ();
            }
            title ();

            c.setColor (new Color (190, 230, 205)); //Entire Background
            c.fillRect (50, 70, 385, 385);

            c.setColor (new Color (160, 200, 175)); //Middle Square
            c.fillRect (85, 105, 315, 315);

            //Card borders
            for (int j = 1 ; j <= 10 ; j++)
                c.drawLine (50 + (35 * j), 78, 50 + (35 * j), 97);                          //Add the barriers between the top row of cards
            for (int j = 1 ; j <= 10 ; j++)
                c.drawLine (50 + (35 * j), 428, 50 + (35 * j), 447);                          //Add the barriers between the bottom row of cards
            for (int j = 1 ; j <= 10 ; j++)
                c.drawLine (58, 70 + (35 * j), 77, 70 + (35 * j));                          //Add the barriers between the left column of cards
            for (int j = 1 ; j <= 10 ; j++)
                c.drawLine (408, 70 + (35 * j), 427, 70 + (35 * j));                          //Add the barriers between the right column of cards









            c.setFont (new Font ("Futura", Font.BOLD, 16));
            for (int i = 0 ; i < 40 ; i++)
            {
                int x, y;   //Position of the top left corner of each block
                if (gameDataInt [7] [i] [0] != 0 && gameDataInt [7] [i] [1] > 0)
                {
                    switch (gameDataInt [2] [gameDataInt [7] [i] [0]] [0])
                    { //Gets the model number of the owner of a property
                        case 1:
                            c.setColor (new Color (140, 90, 60));           //Brown    (Set the color for the bars)
                            break;
                        case 2:
                            c.setColor (new Color (140, 180, 210));         //Light Blue
                            break;
                        case 3:
                            c.setColor (new Color (200, 75, 150));          //Pink
                            break;
                        case 4:
                            c.setColor (new Color (235, 150, 60));          //Orange
                            break;
                        case 5:
                            c.setColor (new Color (220, 55, 50));          //Red
                            break;
                        case 6:
                            c.setColor (new Color (255, 245, 75));          //Yellow
                            break;
                        case 7:
                            c.setColor (new Color (85, 175, 100));          //Green
                            break;
                        case 8:
                            c.setColor (new Color (50, 115, 180));          //Dark Blue
                            break;
                        default:
                            break;
                    }
                    if (i <= 10)
                    { //Set the top left corner of the square
                        x = 35 * (i - 1) + 85;
                        y = 70;
                    }
                    else if (i <= 20)
                    {
                        x = 400;
                        y = 35 * (i - 11) + 105;
                    }
                    else if (i <= 30)
                    {
                        x = 400 - (35 * (i - 20));
                        y = 420;
                    }
                    else
                    {
                        x = 50;
                        y = 420 - 35 * (i - 30);
                    }
                    if (gameDataInt [7] [i] [1] >= 1)
                        c.fillRect (x, y, 35, 3);                               //Draw the upgrade status bars
                    if (gameDataInt [7] [i] [1] >= 2)
                        c.fillRect (x + 32, y, 3, 35);
                    if (gameDataInt [7] [i] [1] >= 3)
                        c.fillRect (x, y + 32, 35, 3);
                    if (gameDataInt [7] [i] [1] >= 4)
                        c.fillRect (x, y, 3, 35);
                }
            }

            c.setColor (new Color (190, 230, 205)); //Right side box background
            c.fillRect (460, 70, 180, 384);

            c.setColor (Color.darkGray);

            c.setFont (new Font ("Futura", Font.BOLD, 17)); //Displays the current user's balance
            c.drawString ("Balance: ", 470, 445);
            c.setFont (new Font ("Futura", Font.PLAIN, 17));
            c.drawString ("$" + gameDataInt [3] [gameDataInt [1] [1] [0]] [0], 545, 445);



            switch (gameDataInt [6] [playerNum] [0])    //Charges rent, taxes, and changes the variable move if a new card is given (only done one time, therefore only should be done at beginning of users turn)
            {
                case 1:
                    //Mediterranean Avenue (1)

                    if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != 0 && gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != playerNum)
                        gameDataInt [3] [playerNum] [0] -= (2 + (30 * gameDataInt [7] [1] [1])); //Someone else owns this property, so you will be charged rent
                    break;

                case 2:
                    //Community Chest (2)
                    move = 3;
                    break;


                case 3:
                    //Baltic Ave. (3)
                    if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != 0 && gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != playerNum)
                        gameDataInt [3] [playerNum] [0] -= (4 + (30 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1]));
                    break;


                case 4:
                    //Income Tax (4)
                    c.setFont (new Font ("Futura", Font.PLAIN, 17));
                    gameDataInt [3] [playerNum] [0] -= 200;
                    break;


                case 5:
                    //Reading Railroad (5)
                    int rent = 25;  //Temporarily stores rent for calculations
                    for (int q = 0 ; q <= 3 ; q++)
                        if ((10 * q) + 5 != gameDataInt [6] [playerNum] [0] && gameDataInt [7] [(10 * q) + 5] [0] == gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0])
                            rent *= 2;
                    if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != 0 && gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != playerNum)
                        gameDataInt [3] [playerNum] [0] -= rent;
                    break;


                case 6:
                    //Oriental Ave. (6)
                    if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != 0 && gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != playerNum)
                        gameDataInt [3] [playerNum] [0] -= (6 + (40 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1]));
                    break;


                case 7:
                    //Chance (7)
                    move = 3;
                    break;


                case 8:
                    //Vermont Ave. (8)
                    if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != 0 && gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != playerNum)
                        gameDataInt [3] [playerNum] [0] -= (6 + (40 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1]));
                    break;


                case 9:
                    //Conneticut Ave. (9)
                    if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != 0 && gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != playerNum)
                        gameDataInt [3] [playerNum] [0] -= (8 + (40 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1]));
                    break;

                    //JAIL (10) (Nothing happens if you land on the JAIL square)

                case 11:
                    //St. Charles (11)
                    if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != 0 && gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != playerNum)
                        gameDataInt [3] [playerNum] [0] -= (10 + (50 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1]));
                    break;


                case 12:
                    //Electric Company (12)
                    rent = 4; //Multiplier for rent [Rent = (dice roll to get here) * 4 for one utility, 10 for both]
                    if (gameDataInt [7] [28] [0] == gameDataInt [7] [12] [0])
                        rent = 10;
                    rent *= (gameDataInt [1] [4] [0] + gameDataInt [1] [4] [1]);
                    if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != 0 && gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != playerNum)
                        gameDataInt [3] [playerNum] [0] -= rent;
                    break;


                case 13:
                    //States Avenue (13)
                    if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != 0 && gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != playerNum)
                        gameDataInt [3] [playerNum] [0] -= (10 + (50 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1]));
                    break;


                case 14:
                    //Virginia Avenue (14)
                    if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != 0 && gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != playerNum)
                        gameDataInt [3] [playerNum] [0] -= (12 + (50 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1]));
                    break;


                case 15:
                    //Pennsylvania Railroad (15)
                    rent = 25;  //Temporarily stores rent for calculations
                    for (int q = 0 ; q <= 3 ; q++)
                        if ((10 * q) + 5 != gameDataInt [6] [playerNum] [0] && gameDataInt [7] [(10 * q) + 5] [0] == gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0])
                            rent *= 2;
                    if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != 0 && gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != playerNum)
                        gameDataInt [3] [playerNum] [0] -= rent;
                    break;


                case 16:
                    //St. James Place (16)
                    if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != 0 && gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != playerNum)
                        gameDataInt [3] [playerNum] [0] -= (14 + (60 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1]));
                    break;


                case 17:
                    //Community Chest (17)
                    move = 3;
                    break;


                case 18:
                    //Tennessee Avenue (18)
                    if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != 0 && gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != playerNum)
                        gameDataInt [3] [playerNum] [0] -= (16 + (60 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1]));
                    break;


                case 19:
                    //New York Avenue (19)
                    if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != 0 && gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != playerNum)
                        gameDataInt [3] [playerNum] [0] -= (16 + (60 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1]));
                    break;

                    //Free Parking (20) (Nothing happens)

                case 21:
                    //Kentucky Avenue (21)
                    if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != 0 && gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != playerNum)
                        gameDataInt [3] [playerNum] [0] -= (18 + (70 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1]));
                    break;


                case 22:
                    //Chance (22)
                    move = 3;
                    break;


                case 23:
                    //Indiana Avenue (23)
                    if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != 0 && gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != playerNum)
                        gameDataInt [3] [playerNum] [0] -= (18 + (70 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1]));
                    break;


                case 24:
                    //Illinois Avenue (24)
                    if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != 0 && gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != playerNum)
                        gameDataInt [3] [playerNum] [0] -= (20 + (70 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1]));
                    break;


                case 25:
                    //Pennsylvania Railroad (25)
                    rent = 25;  //Temporarily stores rent for calculations
                    for (int q = 0 ; q <= 3 ; q++)
                        if ((10 * q) + 5 != gameDataInt [6] [playerNum] [0] && gameDataInt [7] [(10 * q) + 5] [0] == gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0])
                            rent *= 2;
                    if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != 0 && gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != playerNum)
                        gameDataInt [3] [playerNum] [0] -= rent;
                    break;


                case 26:
                    //Atlantic Avenue (26)
                    if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != 0 && gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != playerNum)
                        gameDataInt [3] [playerNum] [0] -= (22 + (80 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1]));
                    break;


                case 27:
                    //Ventnor Avenue (27)
                    if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != 0 && gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != playerNum)
                        gameDataInt [3] [playerNum] [0] -= (22 + (80 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1]));
                    break;


                case 28:
                    //Electric Company (28)
                    rent = 4; //Multiplier for rent [Rent = (dice roll to get here) * 4 for one utility, 10 for both]
                    if (gameDataInt [7] [28] [0] == gameDataInt [7] [12] [0])
                        rent = 10;
                    rent *= (gameDataInt [1] [4] [0] + gameDataInt [1] [4] [1]);
                    if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != 0 && gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != playerNum)
                        gameDataInt [3] [playerNum] [0] -= rent;
                    break;


                case 29:
                    //Marvin Gardens (29)
                    if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != 0 && gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != playerNum)
                        gameDataInt [3] [playerNum] [0] -= (24 + (80 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1]));
                    break;


                case 30:
                    //Go To Jail (30)
                    gameDataInt [8] [playerNum] [0] = 3;
                    break;


                case 31:
                    //Pacific Avenue (31)
                    if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != 0 && gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != playerNum)
                        gameDataInt [3] [playerNum] [0] -= (26 + (90 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1]));
                    break;


                case 32:
                    //North Carolina Avenue (32)
                    if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != 0 && gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != playerNum)
                        gameDataInt [3] [playerNum] [0] -= (26 + (90 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1]));
                    break;


                case 33:
                    //Community Chest (33)
                    move = 3;
                    break;


                case 34:
                    //Pennsylvania Avenue (34)
                    if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != 0 && gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != playerNum)
                        gameDataInt [3] [playerNum] [0] -= (28 + (90 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1]));
                    break;


                case 35:
                    //Short Line (35)
                    rent = 25;  //Temporarily stores rent for calculations
                    for (int q = 0 ; q <= 3 ; q++)
                        if ((10 * q) + 5 != gameDataInt [6] [playerNum] [0] && gameDataInt [7] [(10 * q) + 5] [0] == gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0])
                            rent *= 2;
                    if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != 0 && gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != playerNum)
                        gameDataInt [3] [playerNum] [0] -= rent;
                    break;


                case 36:
                    //Chance (36)
                    move = 3;
                    break;


                case 37:
                    //Park Place (37)
                    if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != 0 && gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != playerNum)
                        gameDataInt [3] [playerNum] [0] -= (35 + (100 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1]));
                    break;


                case 38:
                    //Luxury Tax (38)
                    gameDataInt [3] [playerNum] [0] -= 100;
                    break;


                case 39:
                    //Boardwalk (39)
                    c.setFont (new Font ("Futura", Font.BOLD, 17));
                    if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != 0 && gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != playerNum)
                        gameDataInt [3] [playerNum] [0] -= (50 + (100 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1]));
                    break;

                default:
                    break;
            }
        }




        if (gameDataInt [0] [playerNum] [0] != -1)   //As long as this player hasn't forfeited or lost
        {
            //Draw the board
            title ();

            c.setColor (new Color (190, 230, 205)); //Entire Background
            c.fillRect (50, 70, 385, 385);

            c.setColor (new Color (160, 200, 175)); //Middle Square
            c.fillRect (85, 105, 315, 315);

            //Card borders
            for (int j = 1 ; j <= 10 ; j++)
                c.drawLine (50 + (35 * j), 78, 50 + (35 * j), 97);                          //Add the barriers between the top row of cards
            for (int j = 1 ; j <= 10 ; j++)
                c.drawLine (50 + (35 * j), 428, 50 + (35 * j), 447);                          //Add the barriers between the bottom row of cards
            for (int j = 1 ; j <= 10 ; j++)
                c.drawLine (58, 70 + (35 * j), 77, 70 + (35 * j));                          //Add the barriers between the left column of cards
            for (int j = 1 ; j <= 10 ; j++)
                c.drawLine (408, 70 + (35 * j), 427, 70 + (35 * j));                          //Add the barriers between the right column of cards



            c.setFont (new Font ("Futura", Font.BOLD, 16));

            //Go
            c.setColor (Color.black);
            c.drawString ("GO", 54, 94);

            c.setColor (new Color (140, 90, 60));   //Brown
            c.drawString ("Md", 90, 94);    //Mediterranean Avenue
            c.drawString ("Ba", 162, 94);   //Baltic Avenue

            c.setColor (new Color (140, 180, 210)); //Light Blue
            c.drawString ("Ont", 264, 94);   //Oriental Avenue
            c.drawString ("Vm", 335, 94);   //Vermont Avenue
            c.drawString ("Cn", 372, 94);   //Connecticut Avenue

            c.setColor (new Color (200, 75, 150));  //Pink
            c.drawString ("StC", 402, 129);   //St. Charles Place
            c.drawString ("St", 407, 199);   //States Avenue
            c.drawString ("Vrn", 402, 234);   //Virginia Avenue

            c.setColor (new Color (235, 150, 60));  //Orange
            c.drawString ("StJ", 403, 304);   //St. James Place
            c.drawString ("Ten", 403, 374);   //Tennessee Avenue
            c.drawString ("NY", 406, 409);   //New York Avenue

            c.setColor (new Color (220, 55, 50));  //Red
            c.drawString ("Knt", 369, 443);   //Kentucky Avenue
            c.drawString ("Ind", 302, 443);   //Indiana Avenue
            c.drawString ("Ill", 273, 443);   //Illinois Avenue

            c.setColor (new Color (255, 245, 75));  //Yellow
            c.drawString ("Atl", 198, 443);   //Atlantic Avenue
            c.drawString ("Vnt", 160, 443);   //Ventnor Avenue
            c.drawString ("Mrv", 89, 443);   //Marvin Gardens

            c.setColor (new Color (85, 175, 100));  //Green
            c.drawString ("Pcf", 55, 409);   //Pacific Avenue
            c.drawString ("NCa", 50, 374);   //North Carolina Avenue
            c.drawString ("Pna", 52, 305);   //Pennsylvania Avenue

            c.setColor (new Color (50, 115, 180));  //Dark Blue
            c.drawString ("Bdk", 52, 129);   //Boardwalk
            c.drawString ("Prk", 55, 199);   //Park Place


            //Community Chest
            c.setColor (new Color (75, 175, 230));
            c.drawString ("CC", 125, 94);   //Top
            c.drawString ("CC", 55, 340);   //Left
            c.drawString ("CC", 404, 339); //Right

            //Railroads
            c.setColor (Color.black);
            c.drawString ("rR", 236, 94);   //Reading Railrod
            c.drawString ("rP", 408, 269);   //Pennsylvania Railroad
            c.drawString ("rBO", 228, 443);   //B & O  Railroad
            c.drawString ("rSL", 54, 270);   //Short Line

            //Chance
            c.setFont (new Font ("Futura", Font.BOLD, 20));
            c.setColor (new Color (200, 75, 150));
            c.drawString ("?", 307, 95);   //Top
            c.setColor (new Color (80, 175, 240));
            c.drawString ("?", 343, 445);   //Bottom
            c.setColor (new Color (235, 150, 60));
            c.drawString ("?", 63, 235);

            //Utilities
            c.setFont (new Font ("Futura", Font.BOLD, 16));
            c.setColor (Color.black);
            c.drawString ("EC", 405, 164);  //Right
            c.drawString ("WW", 122, 443);  //Bottom

            //Taxes
            c.setFont (new Font ("Futura", Font.PLAIN, 13));
            c.drawString ("TAX", 196, 93);  //Top
            c.drawString ("TAX", 53, 163);  //Left

            //Free Parking
            c.setFont (new Font ("Futura", Font.PLAIN, 11));
            c.drawString ("FREE", 406, 442);   //New York Avenue

            //Jail & toJail
            c.drawString ("toJAIL", 54, 441);
            c.setColor (new Color (235, 150, 60));
            c.fillRect (403, 85, 18, 18);

            c.setColor (Color.white);
            c.setFont (new Font ("Futura", Font.PLAIN, 9));
            c.drawString ("JAIL", 403, 97);



            for (int i = 0 ; i < 40 ; i++)
            {
                int x, y;   //Position of the top left corner of each block
                if (gameDataInt [7] [i] [0] != 0 && gameDataInt [0] [gameDataInt [7] [i] [0]] [0] != -1)
                { //As long as there is an owner who has not forfeited or lost
                    switch (gameDataInt [2] [gameDataInt [7] [i] [0]] [0])
                    { //Gets the model number of the owner of a property
                        case 1:
                            c.setColor (new Color (140, 90, 60));           //Brown    (Set the color for the bars)
                            break;
                        case 2:
                            c.setColor (new Color (140, 180, 210));         //Light Blue
                            break;
                        case 3:
                            c.setColor (new Color (200, 75, 150));          //Pink
                            break;
                        case 4:
                            c.setColor (new Color (235, 150, 60));          //Orange
                            break;
                        case 5:
                            c.setColor (new Color (220, 55, 50));          //Red
                            break;
                        case 6:
                            c.setColor (new Color (255, 245, 75));          //Yellow
                            break;
                        case 7:
                            c.setColor (new Color (85, 175, 100));          //Green
                            break;
                        case 8:
                            c.setColor (new Color (50, 115, 180));          //Dark Blue
                            break;
                        default:
                            break;
                    }
                    if (i <= 10)
                    { //Set the top left corner of the square
                        x = 35 * (i - 1) + 85;
                        y = 70;
                    }
                    else if (i <= 20)
                    {
                        x = 400;
                        y = 35 * (i - 11) + 105;
                    }
                    else if (i <= 30)
                    {
                        x = 400 - (35 * (i - 20));
                        y = 420;
                    }
                    else
                    {
                        x = 50;
                        y = 420 - 35 * (i - 30);
                    }
                    if (gameDataInt [7] [i] [1] >= 1)
                        c.fillRect (x, y, 35, 3);                               //Draw the upgrade status bars
                    if (gameDataInt [7] [i] [1] >= 2)
                        c.fillRect (x + 32, y, 3, 35);
                    if (gameDataInt [7] [i] [1] >= 3)
                        c.fillRect (x, y + 32, 35, 3);
                    if (gameDataInt [7] [i] [1] >= 4)
                        c.fillRect (x, y, 3, 35);
                }
            }

            c.setColor (new Color (190, 230, 205)); //Right side box background
            c.fillRect (460, 70, 180, 384);

            switch (gameDataInt [6] [playerNum] [0])    //This is used to display the right side panel  (Note: rent and other one time things such as drawing cards are done in the switch case in the above if statement used to check if this is a new player
            {
                    //gameDataInt[7][gameDataInt[6][playerNum][0]][0] is equal to the owner of the current property
                    //gameDataInt[6][playerNum][0] refers to what the current property is
                    //gameDataInt[7][i][0] refers to who owns the i'th property

                case 0:
                    c.setColor (Color.black); //GO (0)
                    c.setFont (new Font ("Futura", Font.BOLD, 17)); //Display the Tile Name and Price / Function
                    c.drawString ("GO", 470, 90);
                    c.setColor (Color.darkGray);
                    c.setFont (new Font ("Futura", Font.PLAIN, 17));
                    c.drawString ("Collect $200", 471, 118);
                    c.drawLine (472, 98, 490, 98);
                    break;


                case 1:
                    c.setColor (new Color (140, 90, 60));   //Med Ave. (1)
                    c.setFont (new Font ("Futura", Font.BOLD, 17));
                    c.drawString ("Mediterranean Ave.", 470, 95);
                    c.drawLine (472, 103, 490, 103);

                    c.setFont (new Font ("Futura", Font.PLAIN, 12));
                    if (gameDataInt [7] [1] [0] == 0)
                    { //The property is for sale
                        c.setFont (new Font ("Futura", Font.BOLD, 12));
                        c.drawString ("Price: -$60", 471, 120);

                        message = " to buy";
                        forSale = 60;
                    }
                    else if (gameDataInt [7] [1] [0] != playerNum) //Someone else owns this property, so you will be charged rent
                        c.drawString ("Rent: -$" + (2 + (30 * gameDataInt [7] [1] [1])), 471, 120);
                    else
                    { //You own this property, and depending what tier it is, possibly upgrade it
                        if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1] < 4)
                        {
                            c.drawString ("Upgrade: $50", 471, 122);
                            c.drawString ("Rent: +$" + (2 + (30 * gameDataInt [7] [1] [1])) + " (+$" + 30 + ")", 471, 140);

                            forSale = 50;
                            message = " to upgrade";
                        }
                        else
                            c.drawString ("Rent +$" + (2 + (30 * gameDataInt [7] [1] [1])), 471, 120);

                        sellValue = (int) ((60.0 + (50 * (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1] - 1))) * 0.75);
                    }
                    break;


                case 2:
                    c.setColor (new Color (75, 175, 230));  //Community Chest (2)
                    c.setFont (new Font ("Futura", Font.BOLD, 17));
                    c.drawString ("Community Chest", 470, 95);
                    c.setColor (Color.darkGray);
                    c.setFont (new Font ("Futura", Font.PLAIN, 12));
                    c.drawString ("Receive a card from the deck", 471, 123);
                    c.drawLine (472, 103, 490, 103);
                    break;


                case 3:
                    c.setColor (new Color (140, 90, 60));   //Baltic Ave. (3)
                    c.setFont (new Font ("Futura", Font.BOLD, 17));
                    c.drawString ("Baltic Ave.", 470, 95);
                    c.drawLine (472, 103, 490, 103);

                    c.setFont (new Font ("Futura", Font.PLAIN, 12));
                    if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] == 0)
                    {
                        c.setFont (new Font ("Futura", Font.BOLD, 12));
                        c.drawString ("Price: -$60", 471, 120);

                        message = " to buy";
                        forSale = 60;
                    }
                    else if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != playerNum)
                        c.drawString ("Rent: -$" + (4 + (30 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1])), 471, 120);
                    else
                    {
                        if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1] < 4)
                        {
                            c.drawString ("Upgrade: $50", 471, 122);
                            c.drawString ("Rent: +$" + (4 + (30 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1])) + " (+$" + 30 + ")", 471, 140);

                            forSale = 50;
                            message = " to upgrade";
                        }
                        else
                            c.drawString ("Rent +$" + (4 + (30 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1])), 471, 120);

                        sellValue = (int) ((60.0 + (50 * (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1] - 1))) * 0.75);
                    }
                    break;


                case 4:
                    c.setColor (Color.black);   //Income Tax (4)
                    c.setFont (new Font ("Futura", Font.PLAIN, 17));
                    c.drawString ("Income Tax", 470, 95);
                    c.drawLine (472, 103, 490, 103);
                    c.setColor (Color.darkGray);
                    c.setFont (new Font ("Futura", Font.PLAIN, 12));
                    c.drawString ("Deduct $200", 471, 120);
                    c.drawLine (472, 103, 490, 103);
                    break;


                case 5:
                    c.setColor (Color.black); //Reading Railroad (5)
                    c.setFont (new Font ("Futura", Font.BOLD, 17));
                    c.drawString ("Reading Railroad", 470, 95);
                    c.drawLine (472, 103, 490, 103);

                    c.setFont (new Font ("Futura", Font.PLAIN, 12));


                    int rent = 25;  //Temporarily stores rent for calculations
                    for (int q = 0 ; q <= 3 ; q++)
                        if ((10 * q) + 5 != gameDataInt [6] [playerNum] [0] && gameDataInt [7] [(10 * q) + 5] [0] == gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0])
                            rent *= 2;

                    if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] == 0)
                    {
                        c.setFont (new Font ("Futura", Font.BOLD, 12));
                        c.drawString ("Price: -$200", 471, 120);

                        message = " to buy";
                        forSale = 200;
                        sellValue = 150;
                    }
                    else if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != playerNum)
                        c.drawString ("Rent: -$" + rent, 471, 120);
                    else
                    {
                        c.drawString ("Rent +$" + rent, 471, 120);
                        sellValue = 150;
                    }
                    break;


                case 6:
                    c.setColor (new Color (140, 180, 210)); //Oriental Ave. (6)
                    c.setFont (new Font ("Futura", Font.BOLD, 17));
                    c.drawString ("Oriental Ave.", 470, 95);
                    c.drawLine (472, 103, 490, 103);

                    c.setFont (new Font ("Futura", Font.PLAIN, 12));
                    if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] == 0)
                    {
                        c.setFont (new Font ("Futura", Font.BOLD, 12));
                        c.drawString ("Price: -$100", 471, 120);

                        message = " to buy";
                        forSale = 100;
                    }
                    else if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != playerNum)
                        c.drawString ("Rent: -$" + (6 + (40 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1])), 471, 120);
                    else
                    {
                        if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1] < 4)
                        {
                            c.drawString ("Upgrade: $50", 471, 122);
                            c.drawString ("Rent: +$" + (6 + (40 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1])) + " (+$" + 40 + ")", 471, 140);

                            forSale = 50;
                            message = " to upgrade";
                        }
                        else
                            c.drawString ("Rent +$" + (6 + (40 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1])), 471, 120);

                        sellValue = (int) ((100.0 + (50 * (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1] - 1))) * 0.75);
                    }
                    break;


                case 7:
                    c.setColor (new Color (200, 75, 150));  //Chance (7)
                    c.setFont (new Font ("Futura", Font.PLAIN, 17));
                    c.drawString ("Chance", 470, 95);
                    c.drawLine (472, 103, 490, 103);
                    c.setColor (Color.darkGray);
                    c.setFont (new Font ("Futura", Font.PLAIN, 12));
                    c.drawString ("Receive a card from the deck", 471, 120);
                    c.drawLine (472, 103, 490, 103);
                    break;


                case 8:
                    c.setColor (new Color (140, 180, 210)); //Vermont Ave. (8)
                    c.setFont (new Font ("Futura", Font.BOLD, 17));
                    c.drawString ("Vermont Ave.", 470, 95);
                    c.drawLine (472, 103, 490, 103);

                    c.setFont (new Font ("Futura", Font.PLAIN, 12));
                    if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] == 0)
                    {
                        c.setFont (new Font ("Futura", Font.BOLD, 12));
                        c.drawString ("Price: -$100", 471, 120);

                        message = " to buy";
                        forSale = 100;
                    }
                    else if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != playerNum)
                        c.drawString ("Rent: -$" + (6 + (40 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1])), 471, 120);
                    else
                    {
                        if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1] < 4)
                        {
                            c.drawString ("Upgrade: $50", 471, 122);
                            c.drawString ("Rent: +$" + (6 + (40 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1])) + " (+$" + 40 + ")", 471, 140);

                            forSale = 50;
                            message = " to upgrade";
                        }
                        else
                            c.drawString ("Rent +$" + (6 + (40 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1])), 471, 120);

                        sellValue = (int) ((100.0 + (50 * (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1] - 1))) * 0.75);
                    }
                    break;


                case 9:
                    c.setColor (new Color (140, 180, 210)); //Conneticut Ave. (9)
                    c.setFont (new Font ("Futura", Font.BOLD, 17));
                    c.drawString ("Connecticut Ave.", 470, 95);
                    c.drawLine (472, 103, 490, 103);

                    c.setFont (new Font ("Futura", Font.PLAIN, 12));
                    if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] == 0)
                    {
                        c.setFont (new Font ("Futura", Font.BOLD, 12));
                        c.drawString ("Price: -$120", 471, 120);

                        message = " to buy";
                        forSale = 120;
                    }
                    else if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != playerNum)
                        c.drawString ("Rent: -$" + (8 + (40 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1])), 471, 120);
                    else
                    {
                        if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1] < 4)
                        {
                            c.drawString ("Upgrade: $50", 471, 122);
                            c.drawString ("Rent: +$" + (8 + (40 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1])) + " (+$" + 40 + ")", 471, 140);

                            forSale = 50;
                            message = " to upgrade";
                        }
                        else
                            c.drawString ("Rent +$" + (8 + (40 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1])), 471, 120);

                        sellValue = (int) ((120.0 + (50 * (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1] - 1))) * 0.75);
                    }
                    break;


                case 10:
                    c.setColor (Color.black); //JAIL (10)
                    c.setFont (new Font ("Futura", Font.BOLD, 20));
                    c.drawString ("JAIL", 470, 95);

                    c.setColor (Color.darkGray);
                    c.setFont (new Font ("Futura", Font.PLAIN, 12));
                    if (gameDataInt [8] [playerNum] [0] > 0)
                    {
                        c.drawString ("To get out, pay $50, roll two of", 471, 124);
                        c.drawString ("a kind, or use a chance card. ", 471, 141);
                        c.drawString ("After 3 turns without rolling a", 471, 158);
                        c.drawString ("double or using a card, you", 471, 175);
                        c.drawString ("must pay $50.", 471, 192);

                        message = " to pay the fee";
                        forSale = 50;
                    }
                    else
                    {
                        c.drawString ("Just visiting", 471, 124);
                    }
                    c.drawLine (472, 104, 490, 104);
                    break;


                case 11:
                    c.setColor (new Color (200, 75, 150)); //St. Charles (11)
                    c.setFont (new Font ("Futura", Font.BOLD, 17));
                    c.drawString ("St. Charles Place", 470, 95);
                    c.drawLine (472, 103, 490, 103);

                    c.setFont (new Font ("Futura", Font.PLAIN, 12));
                    if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] == 0)
                    {
                        c.setFont (new Font ("Futura", Font.BOLD, 12));
                        c.drawString ("Price: -$140", 471, 120);

                        message = " to buy";
                        forSale = 140;
                    }
                    else if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != playerNum)
                        c.drawString ("Rent: -$" + (10 + (50 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1])), 471, 120);
                    else
                    {
                        if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1] < 4)
                        {
                            c.drawString ("Upgrade: $100", 471, 122);
                            c.drawString ("Rent: +$" + (10 + (50 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1])) + " (+$" + 50 + ")", 471, 140);

                            forSale = 100;
                            message = " to upgrade";
                        }
                        else
                            c.drawString ("Rent +$" + (10 + (50 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1])), 471, 120);

                        sellValue = (int) ((140.0 + (100 * (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1] - 1))) * 0.75);
                    }
                    break;


                case 12:
                    c.setColor (Color.black); //Electric Company (12)
                    c.setFont (new Font ("Futura", Font.BOLD, 17));
                    c.drawString ("Electric Company", 470, 95);
                    c.drawLine (472, 103, 490, 103);

                    rent = 4; //Multiplier for rent [Rent = (dice roll to get here) * 4 for one utility, 10 for both]
                    if (gameDataInt [7] [28] [0] == gameDataInt [7] [12] [0])
                        rent = 10;
                    c.setFont (new Font ("Futura", Font.PLAIN, 12));
                    if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] == 0)
                    {
                        c.setFont (new Font ("Futura", Font.BOLD, 12));
                        c.drawString ("Price: -$150", 471, 120);

                        message = " to buy";
                        forSale = 150;
                        sellValue = 112;
                    }
                    else if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != playerNum)
                        c.drawString ("Rent: -$(" + rent + "*" + (gameDataInt [1] [4] [0] + gameDataInt [1] [4] [1]) + ") = " + rent * (gameDataInt [1] [4] [0] + gameDataInt [1] [4] [1]), 471, 120);
                    else
                    {
                        c.drawString ("Rent +$(" + rent + "*dice roll)", 471, 120);
                        sellValue = 112;
                    }
                    break;


                case 13:
                    c.setColor (new Color (200, 75, 150)); //States Avenue (13)
                    c.setFont (new Font ("Futura", Font.BOLD, 17));
                    c.drawString ("States Avenue", 470, 95);
                    c.drawLine (472, 103, 490, 103);

                    c.setFont (new Font ("Futura", Font.PLAIN, 12));
                    if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] == 0)
                    {
                        c.setFont (new Font ("Futura", Font.BOLD, 12));
                        c.drawString ("Price: -$140", 471, 120);

                        message = " to buy";
                        forSale = 140;
                    }
                    else if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != playerNum)
                        c.drawString ("Rent: -$" + (10 + (50 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1])), 471, 120);
                    else
                    {
                        if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1] < 4)
                        {
                            c.drawString ("Upgrade: $100", 471, 122);
                            c.drawString ("Rent: +$" + (10 + (50 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1])) + " (+$" + 50 + ")", 471, 140);

                            forSale = 100;
                            message = " to upgrade";
                        }
                        else
                            c.drawString ("Rent +$" + (10 + (50 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1])), 471, 120);

                        sellValue = (int) ((140.0 + (100 * (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1] - 1))) * 0.75);
                    }
                    break;


                case 14:
                    c.setColor (new Color (200, 75, 150)); //Virginia Avenue (14)
                    c.setFont (new Font ("Futura", Font.BOLD, 17));
                    c.drawString ("Virginia Avenue", 470, 95);
                    c.drawLine (472, 103, 490, 103);

                    c.setFont (new Font ("Futura", Font.PLAIN, 12));
                    if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] == 0)
                    {
                        c.setFont (new Font ("Futura", Font.BOLD, 12));
                        c.drawString ("Price: -$160", 471, 120);

                        message = " to buy";
                        forSale = 160;
                    }
                    else if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != playerNum)
                        c.drawString ("Rent: -$" + (12 + (50 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1])), 471, 120);
                    else
                    {
                        if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1] < 4)
                        {
                            c.drawString ("Upgrade: $100", 471, 122);
                            c.drawString ("Rent: +$" + (12 + (50 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1])) + " (+$" + 50 + ")", 471, 140);

                            forSale = 100;
                            message = " to upgrade";
                        }
                        else
                            c.drawString ("Rent +$" + (12 + (50 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1])), 471, 120);

                        sellValue = (int) ((160.0 + (100 * (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1] - 1))) * 0.75);
                    }
                    break;


                case 15:
                    c.setColor (Color.black); //Pennsylvania Railroad (15)
                    c.setFont (new Font ("Futura", Font.BOLD, 15));
                    c.drawString ("Pennsylvania Railroad", 470, 95);
                    c.drawLine (472, 103, 490, 103);

                    c.setFont (new Font ("Futura", Font.PLAIN, 12));


                    rent = 25;  //Temporarily stores rent for calculations
                    for (int q = 0 ; q <= 3 ; q++)
                        if ((10 * q) + 5 != gameDataInt [6] [playerNum] [0] && gameDataInt [7] [(10 * q) + 5] [0] == gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0])
                            rent *= 2;

                    if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] == 0)
                    {
                        c.setFont (new Font ("Futura", Font.BOLD, 12));
                        c.drawString ("Price: -$200", 471, 120);

                        message = " to buy";
                        forSale = 200;
                        sellValue = 150;
                    }
                    else if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != playerNum)
                        c.drawString ("Rent: -$" + rent, 471, 120);
                    else
                    {
                        c.drawString ("Rent +$" + rent, 471, 120);
                        sellValue = 150;
                    }
                    break;


                case 16:
                    c.setColor (new Color (235, 150, 60));  //St. James Place (16)
                    c.setFont (new Font ("Futura", Font.BOLD, 17));
                    c.drawString ("St. James Place", 470, 95);
                    c.drawLine (472, 103, 490, 103);

                    c.setFont (new Font ("Futura", Font.PLAIN, 12));
                    if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] == 0)
                    {
                        c.setFont (new Font ("Futura", Font.BOLD, 12));
                        c.drawString ("Price: -$180", 471, 120);

                        message = " to buy";
                        forSale = 180;
                    }
                    else if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != playerNum)
                        c.drawString ("Rent: -$" + (14 + (60 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1])), 471, 120);
                    else
                    {
                        if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1] < 4)
                        {
                            c.drawString ("Upgrade: $100", 471, 122);
                            c.drawString ("Rent: +$" + (14 + (60 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1])) + " (+$" + 60 + ")", 471, 140);

                            forSale = 100;
                            message = " to upgrade";
                        }
                        else
                            c.drawString ("Rent +$" + (14 + (60 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1])), 471, 120);

                        sellValue = (int) ((180.0 + (100 * (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1] - 1))) * 0.75);
                    }
                    break;


                case 17:
                    c.setColor (new Color (75, 175, 230));  //Community Chest (17)
                    c.setFont (new Font ("Futura", Font.BOLD, 17));
                    c.drawString ("Community Chest", 470, 95);
                    c.setColor (Color.darkGray);
                    c.setFont (new Font ("Futura", Font.PLAIN, 12));
                    c.drawString ("Receive a card from the deck", 471, 123);
                    c.drawLine (472, 103, 490, 103);
                    break;


                case 18:
                    c.setColor (new Color (235, 150, 60));  //Tennessee Avenue (18)
                    c.setFont (new Font ("Futura", Font.BOLD, 17));
                    c.drawString ("Tennessee Avenue", 470, 95);
                    c.drawLine (472, 103, 490, 103);

                    c.setFont (new Font ("Futura", Font.PLAIN, 12));
                    if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] == 0)
                    {
                        c.setFont (new Font ("Futura", Font.BOLD, 12));
                        c.drawString ("Price: -$180", 471, 120);

                        message = " to buy";
                        forSale = 180;
                    }
                    else if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != playerNum)
                        c.drawString ("Rent: -$" + (16 + (60 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1])), 471, 120);
                    else
                    {
                        if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1] < 4)
                        {
                            c.drawString ("Upgrade: $100", 471, 122);
                            c.drawString ("Rent: +$" + (16 + (60 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1])) + " (+$" + 60 + ")", 471, 140);

                            forSale = 100;
                            message = " to upgrade";
                        }
                        else
                            c.drawString ("Rent +$" + (16 + (60 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1])), 471, 120);

                        sellValue = (int) ((180.0 + (100 * (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1] - 1))) * 0.75);
                    }
                    break;


                case 19:
                    c.setColor (new Color (235, 150, 60));  //New York Avenue (19)
                    c.setFont (new Font ("Futura", Font.BOLD, 17));
                    c.drawString ("New York Avenue", 470, 95);
                    c.drawLine (472, 103, 490, 103);

                    c.setFont (new Font ("Futura", Font.PLAIN, 12));
                    if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] == 0)
                    {
                        c.setFont (new Font ("Futura", Font.BOLD, 12));
                        c.drawString ("Price: -$200", 471, 120);

                        message = " to buy";
                        forSale = 200;
                    }
                    else if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != playerNum)
                        c.drawString ("Rent: -$" + (16 + (60 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1])), 471, 120);
                    else
                    {
                        if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1] < 4)
                        {
                            c.drawString ("Upgrade: $100", 471, 122);
                            c.drawString ("Rent: +$" + (16 + (60 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1])) + " (+$" + 60 + ")", 471, 140);

                            forSale = 100;
                            message = " to upgrade";
                        }
                        else
                            c.drawString ("Rent +$" + (16 + (60 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1])), 471, 120);

                        sellValue = (int) ((200.0 + (100 * (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1] - 1))) * 0.75);
                    }
                    break;


                case 20:
                    c.setColor (Color.black); //Free Parking (20)
                    c.setFont (new Font ("Futura", Font.BOLD, 20));
                    c.drawString ("Free Parking", 470, 95);

                    c.setColor (Color.darkGray);
                    c.drawLine (472, 104, 490, 104);
                    break;


                case 21:
                    c.setColor (new Color (220, 55, 50));   //Kentucky Avenue (21)
                    c.setFont (new Font ("Futura", Font.BOLD, 17));
                    c.drawString ("Kentucky Avenue", 470, 95);
                    c.drawLine (472, 103, 490, 103);

                    c.setFont (new Font ("Futura", Font.PLAIN, 12));
                    if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] == 0)
                    {
                        c.setFont (new Font ("Futura", Font.BOLD, 12));
                        c.drawString ("Price: -$220", 471, 120);

                        message = " to buy";
                        forSale = 220;
                    }
                    else if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != playerNum)
                        c.drawString ("Rent: -$" + (18 + (70 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1])), 471, 120);
                    else
                    {
                        if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1] < 4)
                        {
                            c.drawString ("Upgrade: $150", 471, 122);
                            c.drawString ("Rent: +$" + (18 + (70 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1])) + " (+$" + 70 + ")", 471, 140);

                            forSale = 150;
                            message = " to upgrade";
                        }
                        else
                            c.drawString ("Rent +$" + (18 + (70 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1])), 471, 120);

                        sellValue = (int) ((220.0 + (150 * (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1] - 1))) * 0.75);
                    }
                    break;


                case 22:
                    c.setColor (new Color (80, 175, 240)); //Chance (22)
                    c.setFont (new Font ("Futura", Font.PLAIN, 17));
                    c.drawString ("Chance", 470, 95);
                    c.drawLine (472, 103, 490, 103);
                    c.setColor (Color.darkGray);
                    c.setFont (new Font ("Futura", Font.PLAIN, 12));
                    c.drawString ("Receive a card from the deck", 471, 120);
                    c.drawLine (472, 103, 490, 103);
                    break;


                case 23:
                    c.setColor (new Color (220, 55, 50));   //Indiana Avenue (23)
                    c.setFont (new Font ("Futura", Font.BOLD, 17));
                    c.drawString ("Indiana Avenue", 470, 95);
                    c.drawLine (472, 103, 490, 103);

                    c.setFont (new Font ("Futura", Font.PLAIN, 12));
                    if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] == 0)
                    {
                        c.setFont (new Font ("Futura", Font.BOLD, 12));
                        c.drawString ("Price: -$220", 471, 120);

                        message = " to buy";
                        forSale = 220;
                    }
                    else if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != playerNum)
                        c.drawString ("Rent: -$" + (18 + (70 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1])), 471, 120);
                    else
                    {
                        if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1] < 4)
                        {
                            c.drawString ("Upgrade: $150", 471, 122);
                            c.drawString ("Rent: +$" + (18 + (70 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1])) + " (+$" + 70 + ")", 471, 140);

                            forSale = 150;
                            message = " to upgrade";
                        }
                        else
                            c.drawString ("Rent +$" + (18 + (70 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1])), 471, 120);

                        sellValue = (int) ((220.0 + (150 * (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1] - 1))) * 0.75);
                    }
                    break;


                case 24:
                    c.setColor (new Color (220, 55, 50));   //Illinois Avenue (24)
                    c.setFont (new Font ("Futura", Font.BOLD, 17));
                    c.drawString ("Illinois Avenue", 470, 95);
                    c.drawLine (472, 103, 490, 103);

                    c.setFont (new Font ("Futura", Font.PLAIN, 12));
                    if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] == 0)
                    {
                        c.setFont (new Font ("Futura", Font.BOLD, 12));
                        c.drawString ("Price: -$240", 471, 120);

                        message = " to buy";
                        forSale = 240;
                    }
                    else if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != playerNum)
                        c.drawString ("Rent: -$" + (20 + (70 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1])), 471, 120);
                    else
                    {
                        if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1] < 4)
                        {
                            c.drawString ("Upgrade: $150", 471, 122);
                            c.drawString ("Rent: +$" + (20 + (70 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1])) + " (+$" + 70 + ")", 471, 140);

                            forSale = 150;
                            message = " to upgrade";
                        }
                        else
                            c.drawString ("Rent +$" + (20 + (70 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1])), 471, 120);

                        sellValue = (int) ((240.0 + (150 * (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1] - 1))) * 0.75);
                    }
                    break;


                case 25:
                    c.setColor (Color.black); //Pennsylvania Railroad (25)
                    c.setFont (new Font ("Futura", Font.BOLD, 15));
                    c.drawString ("B&O Railroad", 470, 95);
                    c.drawLine (472, 103, 490, 103);

                    c.setFont (new Font ("Futura", Font.PLAIN, 12));


                    rent = 25;  //Temporarily stores rent for calculations
                    for (int q = 0 ; q <= 3 ; q++)
                        if ((10 * q) + 5 != gameDataInt [6] [playerNum] [0] && gameDataInt [7] [(10 * q) + 5] [0] == gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0])
                            rent *= 2;

                    if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] == 0)
                    {
                        c.setFont (new Font ("Futura", Font.BOLD, 12));
                        c.drawString ("Price: -$200", 471, 120);

                        message = " to buy";
                        forSale = 200;
                        sellValue = 150;
                    }
                    else if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != playerNum)
                        c.drawString ("Rent: -$" + rent, 471, 120);
                    else
                    {
                        c.drawString ("Rent +$" + rent, 471, 120);
                        sellValue = 150;
                    }
                    break;


                case 26:
                    c.setColor (new Color (255, 245, 75));   //Atlantic Avenue (26)
                    c.setFont (new Font ("Futura", Font.BOLD, 17));
                    c.drawString ("Atlantic Avenue", 470, 95);
                    c.drawLine (472, 103, 490, 103);

                    c.setFont (new Font ("Futura", Font.PLAIN, 12));
                    if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] == 0)
                    {
                        c.setFont (new Font ("Futura", Font.BOLD, 12));
                        c.drawString ("Price: -$260", 471, 120);

                        message = " to buy";
                        forSale = 260;
                    }
                    else if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != playerNum)
                        c.drawString ("Rent: -$" + (22 + (80 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1])), 471, 120);
                    else
                    {
                        if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1] < 4)
                        {
                            c.drawString ("Upgrade: $150", 471, 122);
                            c.drawString ("Rent: +$" + (22 + (80 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1])) + " (+$" + 80 + ")", 471, 140);

                            forSale = 150;
                            message = " to upgrade";
                        }
                        else
                            c.drawString ("Rent +$" + (22 + (80 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1])), 471, 120);

                        sellValue = (int) ((260.0 + (150 * (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1] - 1))) * 0.75);
                    }
                    break;


                case 27:
                    c.setColor (new Color (255, 245, 75));   //Ventnor Avenue (27)
                    c.setFont (new Font ("Futura", Font.BOLD, 17));
                    c.drawString ("Ventnor Avenue", 470, 95);
                    c.drawLine (472, 103, 490, 103);

                    c.setFont (new Font ("Futura", Font.PLAIN, 12));
                    if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] == 0)
                    {
                        c.setFont (new Font ("Futura", Font.BOLD, 12));
                        c.drawString ("Price: -$260", 471, 120);

                        message = " to buy";
                        forSale = 260;
                    }
                    else if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != playerNum)
                        c.drawString ("Rent: -$" + (22 + (80 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1])), 471, 120);
                    else
                    {
                        if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1] < 4)
                        {
                            c.drawString ("Upgrade: $150", 471, 122);
                            c.drawString ("Rent: +$" + (22 + (80 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1])) + " (+$" + 80 + ")", 471, 140);

                            forSale = 150;
                            message = " to upgrade";
                        }
                        else
                            c.drawString ("Rent +$" + (22 + (80 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1])), 471, 120);

                        sellValue = (int) ((260.0 + (150 * (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1] - 1))) * 0.75);
                    }
                    break;


                case 28:
                    c.setColor (Color.black); //Electric Company (28)
                    c.setFont (new Font ("Futura", Font.BOLD, 17));
                    c.drawString ("Water Works", 470, 95);
                    c.drawLine (472, 103, 490, 103);

                    rent = 4; //Multiplier for rent [Rent = (dice roll to get here) * 4 for one utility, 10 for both]
                    if (gameDataInt [7] [28] [0] == gameDataInt [7] [12] [0])
                        rent = 10;
                    c.setFont (new Font ("Futura", Font.PLAIN, 12));
                    if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] == 0)
                    {
                        c.setFont (new Font ("Futura", Font.BOLD, 12));
                        c.drawString ("Price: -$150", 471, 120);

                        message = " to buy";
                        forSale = 150;
                        sellValue = 112;
                    }
                    else if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != playerNum)
                        c.drawString ("Rent: -$(" + rent + "*" + (gameDataInt [1] [4] [0] + gameDataInt [1] [4] [1]) + ") = " + rent * (gameDataInt [1] [4] [0] + gameDataInt [1] [4] [1]), 471, 120);
                    else
                    {
                        c.drawString ("Rent +$(" + rent + "*dice roll)", 471, 120);
                        sellValue = 112;
                    }
                    break;


                case 29:
                    c.setColor (new Color (255, 245, 75));   //Marvin Gardens (29)
                    c.setFont (new Font ("Futura", Font.BOLD, 17));
                    c.drawString ("Marvin Gardens", 470, 95);
                    c.drawLine (472, 103, 490, 103);

                    c.setFont (new Font ("Futura", Font.PLAIN, 12));
                    if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] == 0)
                    {
                        c.setFont (new Font ("Futura", Font.BOLD, 12));
                        c.drawString ("Price: -$280", 471, 120);

                        message = " to buy";
                        forSale = 280;
                    }
                    else if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != playerNum)
                        c.drawString ("Rent: -$" + (24 + (80 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1])), 471, 120);
                    else
                    {
                        if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1] < 4)
                        {
                            c.drawString ("Upgrade: $150", 471, 122);
                            c.drawString ("Rent: +$" + (24 + (80 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1])) + " (+$" + 80 + ")", 471, 140);

                            forSale = 150;
                            message = " to upgrade";
                        }
                        else
                            c.drawString ("Rent +$" + (24 + (80 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1])), 471, 120);

                        sellValue = (int) ((280.0 + (150 * (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1] - 1))) * 0.75);
                    }
                    break;


                case 30:
                    c.setColor (Color.black); //Go To Jail (30)
                    c.setFont (new Font ("Futura", Font.BOLD, 20));
                    c.drawString ("Go To Jail", 470, 95);

                    c.setColor (Color.darkGray);
                    c.setFont (new Font ("Futura", Font.PLAIN, 12));
                    c.drawString ("Go to Jail. Do not pass Go. Do", 471, 124);
                    c.drawString ("not collect $200.", 471, 141);
                    c.drawLine (472, 104, 490, 104);
                    break;


                case 31:
                    c.setColor (new Color (85, 175, 100));    //Pacific Avenue (31)
                    c.setFont (new Font ("Futura", Font.BOLD, 17));
                    c.drawString ("Pacific Avenue", 470, 95);
                    c.drawLine (472, 103, 490, 103);

                    c.setFont (new Font ("Futura", Font.PLAIN, 12));
                    if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] == 0)
                    {
                        c.setFont (new Font ("Futura", Font.BOLD, 12));
                        c.drawString ("Price: -$300", 471, 120);

                        message = " to buy";
                        forSale = 300;
                    }
                    else if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != playerNum)
                        c.drawString ("Rent: -$" + (26 + (90 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1])), 471, 120);
                    else
                    {
                        if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1] < 4)
                        {
                            c.drawString ("Upgrade: $200", 471, 122);
                            c.drawString ("Rent: +$" + (26 + (90 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1])) + " (+$" + 90 + ")", 471, 140);

                            forSale = 200;
                            message = " to upgrade";
                        }
                        else
                            c.drawString ("Rent +$" + (26 + (90 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1])), 471, 120);

                        sellValue = (int) ((300.0 + (200 * (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1] - 1))) * 0.75);
                    }
                    break;


                case 32:
                    c.setColor (new Color (85, 175, 100));    //North Carolina Avenue (32)
                    c.setFont (new Font ("Futura", Font.BOLD, 15));
                    c.drawString ("North Carolina Avenue", 470, 95);
                    c.drawLine (472, 103, 490, 103);

                    c.setFont (new Font ("Futura", Font.PLAIN, 12));
                    if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] == 0)
                    {
                        c.setFont (new Font ("Futura", Font.BOLD, 12));
                        c.drawString ("Price: -$300", 471, 120);

                        message = " to buy";
                        forSale = 300;
                    }
                    else if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != playerNum)
                        c.drawString ("Rent: -$" + (26 + (90 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1])), 471, 120);
                    else
                    {
                        if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1] < 4)
                        {
                            c.drawString ("Upgrade: $200", 471, 122);
                            c.drawString ("Rent: +$" + (26 + (90 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1])) + " (+$" + 90 + ")", 471, 140);

                            forSale = 200;
                            message = " to upgrade";
                        }
                        else
                            c.drawString ("Rent +$" + (26 + (90 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1])), 471, 120);

                        sellValue = (int) ((300.0 + (200 * (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1] - 1))) * 0.75);
                    }
                    break;


                case 33:
                    c.setColor (new Color (75, 175, 230));  //Community Chest (33)
                    c.setFont (new Font ("Futura", Font.BOLD, 17));
                    c.drawString ("Community Chest", 470, 95);
                    c.setColor (Color.darkGray);
                    c.setFont (new Font ("Futura", Font.PLAIN, 12));
                    c.drawString ("Receive a card from the deck", 471, 123);
                    c.drawLine (472, 103, 490, 103);
                    break;


                case 34:
                    c.setColor (new Color (85, 175, 100));    //Pennsylvania Avenue (34)
                    c.setFont (new Font ("Futura", Font.BOLD, 16));
                    c.drawString ("Pennsylvania Avenue", 470, 95);
                    c.drawLine (472, 103, 490, 103);

                    c.setFont (new Font ("Futura", Font.PLAIN, 12));
                    if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] == 0)
                    {
                        c.setFont (new Font ("Futura", Font.BOLD, 12));
                        c.drawString ("Price: -$320", 471, 120);

                        message = " to buy";
                        forSale = 320;
                    }
                    else if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != playerNum)
                        c.drawString ("Rent: -$" + (28 + (90 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1])), 471, 120);
                    else
                    {
                        if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1] < 4)
                        {
                            c.drawString ("Upgrade: $200", 471, 122);
                            c.drawString ("Rent: +$" + (28 + (90 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1])) + " (+$" + 90 + ")", 471, 140);

                            forSale = 200;
                            message = " to upgrade";
                        }
                        else
                            c.drawString ("Rent +$" + (320 + (200 * (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1] - 1))), 471, 120);

                        sellValue = (int) ((320.0 + (90 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1])) * 0.75);
                    }
                    break;


                case 35:
                    c.setColor (Color.black); //Short Line (35)
                    c.setFont (new Font ("Futura", Font.BOLD, 15));
                    c.drawString ("Short Line", 470, 95);
                    c.drawLine (472, 103, 490, 103);

                    c.setFont (new Font ("Futura", Font.PLAIN, 12));


                    rent = 25;  //Temporarily stores rent for calculations
                    for (int q = 0 ; q <= 3 ; q++)
                        if ((10 * q) + 5 != gameDataInt [6] [playerNum] [0] && gameDataInt [7] [(10 * q) + 5] [0] == gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0])
                            rent *= 2;

                    if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] == 0)
                    {
                        c.setFont (new Font ("Futura", Font.BOLD, 12));
                        c.drawString ("Price: -$200", 471, 120);

                        message = " to buy";
                        forSale = 200;
                        sellValue = 150;
                    }
                    else if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != playerNum)
                        c.drawString ("Rent: -$" + rent, 471, 120);
                    else
                    {
                        c.drawString ("Rent +$" + rent, 471, 120);
                        sellValue = 150;
                    }
                    break;


                case 36:
                    c.setColor (new Color (235, 150, 60)); //Chance (36)
                    c.setFont (new Font ("Futura", Font.PLAIN, 17));
                    c.drawString ("Chance", 470, 95);
                    c.drawLine (472, 103, 490, 103);
                    c.setColor (Color.darkGray);
                    c.setFont (new Font ("Futura", Font.PLAIN, 12));
                    c.drawString ("Receive a card from the deck", 471, 120);
                    c.drawLine (472, 103, 490, 103);
                    break;


                case 37:
                    c.setColor (new Color (50, 115, 180));  //Park Place (37)
                    c.setFont (new Font ("Futura", Font.BOLD, 17));
                    c.drawString ("Park Place", 470, 95);
                    c.drawLine (472, 103, 490, 103);

                    c.setFont (new Font ("Futura", Font.PLAIN, 12));
                    if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] == 0)
                    {
                        c.setFont (new Font ("Futura", Font.BOLD, 12));
                        c.drawString ("Price: -$350", 471, 120);

                        message = " to buy";
                        forSale = 350;
                    }
                    else if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != playerNum)
                        c.drawString ("Rent: -$" + (35 + (100 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1])), 471, 120);
                    else
                    {
                        if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1] < 4)
                        {
                            c.drawString ("Upgrade: $200", 471, 122);
                            c.drawString ("Rent: +$" + (35 + (100 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1])) + " (+$" + 100 + ")", 471, 140);

                            forSale = 200;
                            message = " to upgrade";
                        }
                        else
                            c.drawString ("Rent +$" + (35 + (10 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1])), 471, 120);

                        sellValue = (int) ((350.0 + (200 * (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1] - 1))) * 0.75);
                    }
                    break;


                case 38:
                    c.setColor (Color.black);   //Luxury Tax (38)
                    c.setFont (new Font ("Futura", Font.PLAIN, 17));
                    c.drawString ("Luxury Tax", 470, 95);
                    c.drawLine (472, 103, 490, 103);
                    c.setColor (Color.darkGray);
                    c.setFont (new Font ("Futura", Font.PLAIN, 12));
                    c.drawString ("Deduct $100", 471, 120);
                    c.drawLine (472, 103, 490, 103);
                    break;


                case 39:
                    c.setColor (new Color (50, 115, 180));  //Boardwalk (39)
                    c.setFont (new Font ("Futura", Font.BOLD, 17));
                    c.drawString ("Boardwalk", 470, 95);
                    c.drawLine (472, 103, 490, 103);

                    c.setFont (new Font ("Futura", Font.PLAIN, 12));
                    if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] == 0)
                    {
                        c.setFont (new Font ("Futura", Font.BOLD, 12));
                        c.drawString ("Price: -$400", 471, 120);

                        message = " to buy";
                        forSale = 400;
                    }
                    else if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] != playerNum)
                        c.drawString ("Rent: -$" + (50 + (100 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1])), 471, 120);
                    else
                    {
                        if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1] < 4)
                        {
                            c.drawString ("Upgrade: $200", 471, 122);
                            c.drawString ("Rent: +$" + (50 + (100 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1])) + " (+$" + 100 + ")", 471, 140);

                            forSale = 200;
                            message = " to upgrade";
                        }
                        else
                            c.drawString ("Rent +$" + (50 + (10 * gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1])), 471, 120);

                        sellValue = (int) ((400.0 + (200 * (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1] - 1))) * 0.75);
                    }
                    break;

                default:
                    break;
            }




            c.setColor (Color.darkGray);

            c.setFont (new Font ("Futura", Font.BOLD, 17)); //Displays the current user's balance
            c.drawString ("Balance: ", 470, 445);

            c.setFont (new Font ("Futura", Font.PLAIN, 17));
            c.drawString ("$" + gameDataInt [3] [gameDataInt [1] [1] [0]] [0], 545, 445);

            c.setFont (new Font ("Futura", Font.PLAIN, 12));
            c.drawString ("Any other key to finish turn", 470, 425);



            int size = 0;
            for (int i = 1 ; i <= gameDataInt [1] [0] [0] ; i++)    //Drawing the model tokens on the board
            {
                if (gameDataInt [0] [i] [0] != -1)
                {
                    int x, y;                                                                       //Position of the top left corner of each token in relation to the block

                    if (gameDataInt [6] [i] [0] == 10 && gameDataInt [8] [i] [0] == 0)   //If you're at jail, and are JUST VISITING
                    {
                        x = 403 + (i - 1) * 8;
                        y = 74;
                        size = 6;
                    }
                    else if (gameDataInt [6] [i] [0] == 10)   //If you're at the jail square , and are IN JAIL
                    {
                        x = 402 + ((i - 1) % 2) * 9 + 1;
                        y = 70 + (i / 3) * 9 + 16;
                        size = 7;
                    }
                    else if (gameDataInt [6] [i] [0] <= 10) //Set the top left corner of the square if you aren't on the JAIL square
                    {
                        x = 54 + 35 * (gameDataInt [6] [i] [0]) + (18 * ((i - 1) % 2));
                        y = 74 + (i / 3) * 18;
                        size = 10;
                    }
                    else if (gameDataInt [6] [i] [0] <= 20)
                    {
                        x = 404 + (18 * ((i - 1) % 2));
                        y = 74 + 35 * (gameDataInt [6] [i] [0] - 10) + ((i / 3) * 18);
                        size = 10;
                    }
                    else if (gameDataInt [6] [i] [0] <= 30)
                    {
                        x = 404 - (35 * (gameDataInt [6] [i] [0] - 20)) + (18 * ((i - 1) % 2));
                        y = 423 + (i / 3) * 18;
                        size = 10;
                    }
                    else
                    {
                        x = 50 + 4 + (18 * ((i - 1) % 2));
                        y = 424 - 35 * (gameDataInt [6] [i] [0] - 30) + (i / 3) * 18;
                        size = 10;
                    }


                    c.setColor (Color.lightGray);
                    c.fillOval (x, y, size, size);


                    switch (gameDataInt [2] [i] [0])
                    { //Gets the model number of the owner of a property
                        case 1:
                            c.setColor (new Color (140, 90, 60));           //Brown             Set the color for the model tokens
                            break;
                        case 2:
                            c.setColor (new Color (140, 180, 210));         //Light Blue
                            break;
                        case 3:
                            c.setColor (new Color (200, 75, 150));          //Pink
                            break;
                        case 4:
                            c.setColor (new Color (235, 150, 60));          //Orange
                            break;
                        case 5:
                            c.setColor (new Color (220, 55, 50));          //Red
                            break;
                        case 6:
                            c.setColor (new Color (255, 245, 75));          //Yellow
                            break;
                        case 7:
                            c.setColor (new Color (85, 175, 100));          //Green
                            break;
                        case 8:
                            c.setColor (new Color (50, 115, 180));          //Dark Blue
                            break;
                        default:
                            c.setColor (Color.black);
                            break;
                    }
                    c.fillOval (x + 1, y + 1, size - 2, size - 2);

                    if (i == playerNum)
                    {
                        c.setFont (new Font ("Avenir", 0, 20));
                        c.drawString (gameDataStr [1] [playerNum].toUpperCase (), 135, 310);
                        c.setFont (new Font ("Calibri", 1, 20));
                        c.drawString (i + ". ", 110, 310);
                        c.fillRect (95, 290, 8, 70);

                        c.setFont (new Font ("FUTURA", 0, 40));
                        c.setColor (Color.darkGray);
                        c.drawString ("$" + gameDataInt [3] [playerNum] [0], 110, 350);
                    }
                }
            }





            //User Options
            c.setFont (new Font ("Futura", 1, 12));
            c.setColor (Color.darkGray);

            if (gameDataInt [6] [playerNum] [0] == 0 || gameDataInt [6] [playerNum] [0] == 2 || gameDataInt [6] [playerNum] [0] == 4 || gameDataInt [6] [playerNum] [0] == 7 || gameDataInt [6] [playerNum] [0] == 12 || gameDataInt [6] [playerNum] [0] == 17 || gameDataInt [6] [playerNum] [0] == 20 || gameDataInt [6] [playerNum] [0] == 22 || gameDataInt [6] [playerNum] [0] == 28 || gameDataInt [6] [playerNum] [0] == 33 || gameDataInt [6] [playerNum] [0] == 36 || gameDataInt [6] [playerNum] [0] == 38)
            { //Cases for Chance, Community Chest, Taxes, Utilities, and GO (1 line extra)
                if (forSale > 0 && gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] == 0)
                { //If there is the user wishes to buy a property or upgrade their property
                    c.setFont (new Font ("Futura", 1, 12));
                    c.drawString ("Press 1", 471, 140);
                    c.drawString ("Press 2", 471, 157);
                    c.drawString ("Press 3", 471, 191);
                    c.drawString ("Press 4", 471, 208);

                    c.setFont (new Font ("Futura", 0, 12));
                    c.drawString (message, 523, 140);
                    c.drawString ("to view / edit your", 523, 157);
                    c.drawString ("cards and properties", 471, 174);
                    c.drawString ("to forfeit", 523, 191);
                    c.drawString ("to save & exit", 523, 208);
                }
                else if (forSale > 0 && gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1] < 4)
                { //If there is an extra line for rent charges
                    c.setFont (new Font ("Futura", 1, 12));
                    c.drawString ("Press 1", 471, 157);
                    c.drawString ("Press 2", 471, 174);
                    c.drawString ("Press 3", 471, 208);
                    c.drawString ("Press 4", 471, 225);
                    c.drawString ("Press 5", 471, 242);

                    c.setFont (new Font ("Futura", 0, 12));
                    c.drawString (message, 523, 157);
                    c.drawString ("to view / edit your", 523, 174);
                    c.drawString ("cards and properties", 471, 191);
                    c.drawString ("to forfeit", 523, 208);
                    c.drawString ("to save & exit", 523, 225);
                    c.drawString ("to sell this property", 523, 242);
                }
                else
                {
                    c.setFont (new Font ("Futura", 1, 12));
                    c.drawString ("Press 2", 471, 140);
                    c.drawString ("Press 3", 471, 174);
                    c.drawString ("Press 4", 471, 191);

                    c.setFont (new Font ("Futura", 0, 12));
                    c.drawString ("to view / edit your", 523, 140);
                    c.drawString ("cards and properties", 471, 157);
                    c.drawString ("to forfeit", 523, 174);
                    c.drawString ("to save & exit", 523, 191);
                }
            }
            else if (gameDataInt [6] [playerNum] [0] == 10)
            { //Specific case for JAIL (5 lines extra)
                if (gameDataInt [8] [playerNum] [0] > 0)
                {
                    c.setFont (new Font ("Futura", 1, 12));
                    c.drawString ("Press 1", 471, 220);
                    c.drawString ("Press 2", 471, 237);
                    c.drawString ("Press 3", 471, 271);
                    c.drawString ("Press 4", 471, 288);
                    c.drawString ("Press 6", 471, 305);

                    c.setFont (new Font ("Futura", 0, 12));
                    c.drawString (message, 523, 220);
                    c.drawString ("to view / edit your", 523, 237);
                    c.drawString ("cards and properties", 523, 254);
                    c.drawString ("to forfeit", 523, 271);
                    c.drawString ("to save & exit", 523, 288);
                    c.drawString ("to use a \"Get", 523, 305);
                    c.drawString ("out of Jail\" card", 523, 322);
                }
                else
                {
                    c.setFont (new Font ("Futura", 1, 12));
                    c.drawString ("Press 2", 471, 141);
                    c.drawString ("Press 3", 471, 175);
                    c.drawString ("Press 4", 471, 192);

                    c.setFont (new Font ("Futura", 0, 12));
                    c.drawString ("to view / edit your", 523, 141);
                    c.drawString ("cards and properties", 471, 158);
                    c.drawString ("to forfeit", 523, 175);
                    c.drawString ("to save & exit", 523, 192);
                }
            }
            else if (gameDataInt [6] [playerNum] [0] == 30)
            { //Specific case for Go to JAIL (2 lines extra)
                c.setFont (new Font ("Futura", 1, 12));
                c.drawString ("Press 2", 471, 175);
                c.drawString ("Press 3", 471, 209);
                c.drawString ("Press 4", 471, 226);

                c.setFont (new Font ("Futura", 0, 12));
                c.drawString ("to view / edit your", 523, 175);
                c.drawString ("cards and properties", 471, 192);
                c.drawString ("to forfeit", 523, 209);
                c.drawString ("to save & exit", 523, 226);
            }
            else
            {
                if (forSale > 0 && gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] == 0)
                {
                    c.setFont (new Font ("Futura", 1, 12));
                    c.drawString ("Press 1", 471, 140);
                    c.drawString ("Press 2", 471, 157);
                    c.drawString ("Press 3", 471, 191);
                    c.drawString ("Press 4", 471, 208);

                    c.setFont (new Font ("Futura", 0, 12));
                    c.drawString (message, 523, 140);
                    c.drawString ("to view / edit your", 523, 157);
                    c.drawString ("cards and properties", 471, 174);
                    c.drawString ("to forfeit", 523, 191);
                    c.drawString ("to save & exit", 523, 208);
                }
                else if (forSale > 0 && gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1] < 4)
                {
                    c.setFont (new Font ("Futura", 1, 12));
                    c.drawString ("Press 1", 471, 157);
                    c.drawString ("Press 2", 471, 174);
                    c.drawString ("Press 3", 471, 208);
                    c.drawString ("Press 4", 471, 225);
                    c.drawString ("Press 5", 471, 242);

                    c.setFont (new Font ("Futura", 0, 12));
                    c.drawString (message, 523, 157);
                    c.drawString ("to view / edit your", 523, 174);
                    c.drawString ("cards and properties", 471, 191);
                    c.drawString ("to forfeit", 523, 208);
                    c.drawString ("to save & exit", 523, 225);
                    c.drawString ("to sell this property", 523, 242);
                }
                else
                {
                    c.setFont (new Font ("Futura", 1, 12));
                    c.drawString ("Press 2", 471, 140);
                    c.drawString ("Press 3", 471, 174);
                    c.drawString ("Press 4", 471, 191);
                    c.drawString ("Press 5", 471, 208);

                    c.setFont (new Font ("Futura", 0, 12));
                    c.drawString ("to view / edit your", 523, 140);
                    c.drawString ("cards and properties", 471, 157);
                    c.drawString ("to forfeit", 523, 174);
                    c.drawString ("to save & exit", 523, 191);
                    c.drawString ("to sell this property", 523, 208);
                }
            }






            if (move != 3)
            {
                char userMove = c.getChar ();  //Take in the character version of the users input

                if (userMove == '1' && forSale > 0)
                { //If the user can and wishes to buy/upgrade something
                    if (gameDataInt [6] [playerNum] [0] == 10)
                    { //Case for paying JAIL feee
                        gameDataInt [8] [playerNum] [0] = 0;
                        gameDataInt [3] [playerNum] [0] -= 50;
                    }
                    else if (gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] == 0)
                    { //If the property that the user is on is to be bought
                        gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] = playerNum; //Set the owner of the property the user is on to the current player
                        gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1] = 1;
                        gameDataInt [4] [playerNum] [gameDataInt [4] [playerNum] [0] + 1] = gameDataInt [6] [playerNum] [0]; //To the players ownership add a property at the (amountofproperties+1)th index that says the property owned is the current tile/property

                        gameDataInt [4] [playerNum] [0]++;  //Add one to the amount of properties a player has
                    }
                    else
                    { //If the property is to be upgraded
                        gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1]++;
                    }
                    gameDataInt [3] [playerNum] [0] -= forSale; //Subtract the amount owed for the property / upgrade / fee


                    //Set the borders for railways and utilities
                    if (gameDataInt [6] [playerNum] [0] % 10 == 5 || gameDataInt [6] [playerNum] [0] == 12 || gameDataInt [6] [playerNum] [0] == 28)
                    {
                        gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1] = 4;
                    }
                }
                else if (userMove == '2')   //Shows cards
                {
                    move = 4;
                }
                else if (userMove == '3')   //Forfeit the game
                {
                    gameDataInt [0] [playerNum] [0] = -1;
                    gameDataInt [1] [3] [0]--;
                    move = 2;

                }
                else if (userMove == '4')   //Pause and save the game
                {
                    move = 2;
                    option = -1;
                }
                else if (userMove == '5')   //Sell this property
                {
                    for (int j = 1 ; j <= gameDataInt [4] [playerNum] [0] ; j++)
                    {
                        if (gameDataInt [4] [playerNum] [j] == gameDataInt [6] [playerNum] [0])
                        {
                            for (int k = j + 1 ; k <= gameDataInt [4] [playerNum] [0] ; k++)
                                gameDataInt [4] [playerNum] [k - 1] = gameDataInt [4] [playerNum] [k];
                        }
                    }
                    gameDataInt [4] [playerNum] [gameDataInt [4] [playerNum] [0]] = 0;
                    gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [0] = 0;
                    gameDataInt [7] [gameDataInt [6] [playerNum] [0]] [1] = 0;
                    gameDataInt [4] [playerNum] [0]--;
                    gameDataInt [3] [playerNum] [0] += sellValue;
                }
                else if (userMove == '6' && gameDataInt [8] [playerNum] [0] > 0 && gameDataInt [6] [playerNum] [0] == 10)
                {
                    gameDataInt [8] [playerNum] [0] = 0;
                }
                //Bankruptcy check
                else if (gameDataInt [3] [playerNum] [0] < 0 && gameDataInt [4] [playerNum] [0] != 0)
                {
                    JOptionPane.showMessageDialog (null, "You must sell a property in order to keep playing", "Bankrupt", JOptionPane.ERROR_MESSAGE);
                    gameDataInt [1] [2] [0] = 0;
                }
                else if (gameDataInt [3] [playerNum] [0] < 0 && gameDataInt [4] [playerNum] [0] == 0)
                {
                    JOptionPane.showMessageDialog (null, "You have gone bankrupt. Thank you for playing!", "Error", JOptionPane.ERROR_MESSAGE);
                    gameDataInt [1] [2] [0] = -1;
                    gameDataInt [0] [playerNum] [0] = -1;
                }
                else
                { //next player
                    gameDataInt [1] [2] [0] = -1;
                }




                if (gameDataInt [6] [playerNum] [0] == 30)
                { //If you have to move to jail
                    gameDataInt [6] [playerNum] [0] = 10;
                    gameDataInt [8] [playerNum] [0] = 3;
                }
            }
        }
        else    //If the user has forfeited or lost
        {
            gameDataInt [1] [2] [0] = -1;
        }
    }


    /*
        nextPlayer()
        ---
        Shows a screen for the user to pass the game to the next player
        
        Type    Name        Purpose
        int     playerNum   stores who will play
    */
    
    private void nextPlayer (int playerNum)
    {
        title ();


        for (int i = 0 ; i < 20 ; i++)  //Fade in the prompt
        {
            c.setColor (new Color (255, 255, 255, (10 * i) / 5));
            c.setFont (new Font ("Calibri", Font.PLAIN, 30));
            c.drawString ("Pass on the game to " + gameDataStr [1] [playerNum] + " (" + playerNum + ")", 20 + ((20 - gameDataStr [1] [playerNum].length ())) * 8, 230); //The x parameter will center the phrase on screen depending on player name length

            c.drawLine (300, 253, 340, 253);

            c.setColor (new Color (220, 220, 220, (10 * i) / 5));
            c.setFont (new Font ("Raleway-Light", Font.PLAIN, 15));
            c.drawString ("Any key to continue", 255, 280);


            try
            {
                Thread.sleep (20);
            } //Add a short delay before continuing with the program
            catch (Exception e)
            {
            }
        }

        pauseProgram ();
    }

    
    /*
        pauseProgram()
        ---
        Pauses the program for one keypress
    */
    public void pauseProgram ()
    {
        c.getChar ();   //Waits until the user enters any key press
    }

    
    /*
        goodbye()
        --
        Gives a goodbye message to the user when they have to exit
    */
    public void goodbye ()
    {
        title ();   //Calls for the console to be erased and title to be drawn

        c.setColor (Color.white);
        c.setFont (new Font ("Raleway-Light", 1, 25));
        c.drawString ("Thank you for playing Monopoly!", 130, 200);    //Displays exit message

        c.setFont (new Font ("Raleway-Light", 0, 15));
        c.drawString ("Press any key to exit", 260, 235);

        c.setColor (Color.darkGray);
        c.drawString ("by Justin Lu", 280, 480);

        c.setColor (Color.lightGray);
        c.drawLine (310, 215, 330, 215);

        pauseProgram ();    //Waits for user input
        System.exit (0);    //Before exiting the program
    }



    /*
        main()
        ---
        Controls method execution
        
        Type        Name    Purpose
        Monopoly    mp      Start the instance of the class
    */
    public static void main (String[] args)
    {
        Monopoly mp = new Monopoly ();      //Creates a new instance of the current class
        mp.splashScreen ();                 //Calls the program to draw the introductory splash screen

        while (mp.option != 6)  //Keeps looping through various menu options
        {
            if (mp.option == -1)
            {
                mp.mainMenu ();
                mp.askData ();
            }
            else if (mp.option == 1 || mp.option == 2)  //If the user wants to play a game of some sort
            {
                if (mp.option == 1)
                    mp.newGame ();                             //Start a new game
                else
                    mp.resumeGame ();                       //Resume a previously started game

                while (mp.move != 6)    //While the user doesn't want to exit
                {
                    if (mp.move == 1)
                        mp.display ();                                     //If the user is on the main display of the board
                    else if (mp.move == 2)
                        mp.exitGameMenu ();                                //If the user has clicked to leave the game, and a menu has to appear
                    else if (mp.move == 3)
                        mp.newCard ();                                     //If the use1r has obtained a new chance or community chest card, show a special pop up with result
                    else if (mp.move == 4)
                        mp.viewCardsProps ();                              //Lay out all the cards and/or properties a user has

                    mp.saveScore ();        //Save the results of the turn;
                }
                mp.move = 1;
            }
            else if (mp.option == 3)
                mp.leaderboard ();                            //Show the top 10 saved scores
            else if (mp.option == 4)
                mp.instructions ();                            //Show the instructions of how to use the program, and how to play Monopoly
            else
                mp.resetData ();                            //Reset all game related data
        }
        mp.goodbye ();      //Show the user a nice goodbye screen
    }
}
