/*

Justin Lu
1/12/20
ICS3U


SplashScreen | Creates the SplashScreen for the user
*/

import java.awt.*;  // Imports the awt Java library
import hsa.Console; // Imports the Console class used for display
import java.lang.*; // Imports the library used for the Thread class

public class SplashScreen extends Thread
{
    private Console c;  //Console that the main console will be transferred to

    public SplashScreen (Console con)  //Constructor which is where the console is handed over
    {
        c = con;
    }


    //Execute the SplashScreen animation
    //left, right   |   Instance of the Orb class used for the left and right bar sides
    public void draw ()
    {
        synchronized (c)
        {
            for (int i = 1600 ; i > 0 ; i--)    //Draw the radial gradient
            {
                c.setColor (new Color (209 - (i / 10), 226 - (i / 14), 205 - (i / 13)));
                c.fillOval (320 - (i / 2), 250 - (i / 4), i, i / 2);
            }
            
            for (int i = 0; i < 530; i++)   //Draw the pointed downward gradient
            {
                c.setColor (new Color (209 - (i / 10), 226 - (i / 14), 205 - (i / 13)));
                c.fillRect (320 - (i / 2), 530- (i / 4) - i, i, i);
            }
            c.setColor (Color.darkGray);
            c.fillRect (0, 0, 55, 500);
            c.fillRect (585, 0, 55, 500);
        }

        Orb left = new Orb (c, 1);  //Start the orb animation on the left and right
        Orb right = new Orb (c, -1);
        left.start ();
        right.start ();

        for (int i = 0 ; i < 75 ; i++)
        {
            synchronized (c)
            {
                c.setColor (Color.black);
                c.fillRoundRect (292 - (i * 3), 345 - i, (i * 6) + 41, i + 28, (i / 11), (i / 11));

                c.setColor (Color.red);
                c.fillRoundRect (296 - (i * 3), 349 - i, (i * 6) + 33, i + 20, (i / 10), (i / 10));

                c.setColor (Color.black);
                c.setFont (new Font ("Raleway-Light", Font.BOLD, i));
                c.drawString ("MONOPOLY", 308 - (i * 3), 350);

                c.setColor (Color.white);
                c.setFont (new Font ("Raleway-Light", Font.BOLD, i));
                c.drawString ("MONOPOLY", 310 - (i * 3), 348);
            }
            try
            {
                Thread.sleep (2);
            } //Add a short delay before continuing with the program
            catch (Exception e)
            {
            }
        }

        for (int j = 0 ; j < 3 ; j++)   //Draws the three buildings
        {
            for (int i = 0 ; i < 75 ; i++)
            {
                synchronized(c){
                if(j == 0){
                    c.setColor(new Color(156, 188, 164));
                    c.fillRect(55, 100, 530, 250);
                    
                    int x[] = {290-i, 290-((int)(i*2/3)), 283, 280};
                    int y[] = {220-i,  320, 315, 230-((int)(i*3/2))};
                    c.setColor(Color.white);
                    c.fillPolygon(x, y, 4);
                }
                else{
                    c.setColor(new Color(156, 188, 164));
                    c.fillRect(55, 100, 530, 250);
                    
                    int x[] = {215, 240, 283, 280};
                    int y[] = {145,  320, 315, 118};
                    c.setColor(Color.white);
                    c.fillPolygon(x, y, 4);
                }
                
                if(j == 1){
                    int x[] = {350+i, 350+((int)(i*2/3)), 357, 360};    //400
                    int y[] = {220-i, 320, 315, 230-((int)(i*3/2))};
                    c.setColor(Color.lightGray);
                    c.fillPolygon(x, y, 4);
                }
                else if(j > 1){
                    int x[] = {425, 400, 357, 360};
                    int y[] = {130, 320, 320, 135};
                    c.setColor(Color.lightGray);
                    c.fillPolygon(x, y, 4);
                }
                
                if(j == 2){
                    int x[] = {345-i, 355-i, 285+i, 295+i};
                    int y[] = {145, 320, 320, 142};
                    c.setColor(Color.darkGray);
                    c.fillPolygon(x, y, 4);
                }
                
                
                c.setColor (Color.black);   //Redraw the title
                c.fillRoundRect (67, 270, 491, 98, 6, 6);

                c.setColor (Color.red);
                c.fillRoundRect (71, 274, 483, 95, 7, 7);

                c.setColor (Color.black);
                c.setFont (new Font ("Raleway-Light", Font.BOLD, 75));
                c.drawString ("MONOPOLY", 83, 350);

                c.setColor (Color.white);
                c.setFont (new Font ("Raleway-Light", Font.BOLD, 75));
                c.drawString ("MONOPOLY", 85, 348);
                }
            }
        }

        try
        {
            left.join ();
            right.join ();
            Thread.sleep (200);
        }
        catch (Exception e)
        {
        }
    }


    public void run ()  //Starting method which will call the method that draws the robin
    {
        draw ();
    }
}
