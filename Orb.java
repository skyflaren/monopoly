/*

Justin Lu
Ms.Krasteva
1/9/20


Orb | Create the orbs that will run up the left side and down the right
*/


import java.awt.*;  // Imports the awt Java library
import hsa.Console; // Imports the Console class used for display
import java.lang.*; // Imports the library used for the Thread class


public class Orb extends Thread

{
    private Console c;  //Console that the main console will be transferred to
    private int inc;    //Whether to go down or up


    public Orb (Console con, int incrementor)  //Constructor which is where the console is handed over
    {
        c = con;
        inc = incrementor;
    }


    //If inc is 1, then draw the left side going up
    //Otherwise, draw the right side going down
    public void draw ()
    {
        for (int i = 0 ; i < 1000 ; i++) //Amount of seconds to wait is 1000 * 2 = 2000ms
        {
            synchronized (c)
            {
                c.setColor (Color.darkGray);
                c.fillRect (0, 0, 55, 500);
                c.fillRect (585, 0, 55, 500);
                
                for (int j = 0 ; j < 10 ; j++)
                {
                    c.setColor (new Color (244, 195, 128));
                    if (inc == 1)
                    {
                        c.fillOval (5, i - 508 + (50 * j), 44, 44);
                        c.setColor (new Color (248, 251, 209));
                        c.fillOval (34, i - 497 + (50 * j), 8, 8);
                    }
                    else
                    {
                        c.fillOval (591, 950 - (50 * j) - i, 44, 44);
                        c.setColor (new Color (248, 251, 209));
                        c.fillOval (620, 958 - (50 * j) - i, 8, 8);
                    }
                }
            }
            try
            {
                Thread.sleep (2);
            }
            catch (Exception e)
            {
            }

        }
    }


    public void run ()  //Starting method which will call the method that draws the robin
    {
        draw ();
    }
}
