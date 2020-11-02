/**
* Compsci 230 (2019 Semester 1) - Assignment 2
* NAME: Alexander Khouri
* STUDENT ID#: 6402238
* UPI: akho225
*/

import java.util.*;
import java.awt.*;
import javax.swing.*;

public class PacketGraph extends JPanel {
    private int xMax;  // Largest time value
    private int yMax;  // Largest packet data value
    private int xTick; // Increment values for labelling
    private int yTick;
    private int xScale; // UNUSED (implement for very large time scales)
    private int yScale; // Tick * 1000^(3*Scale) = absolute axis-value
    private int X_LENGTH; // Length of axis in pixels
    private int Y_LENGTH;
    private Point orig;   // (0,0) on graph
    private int[] data;
    private static final int[] tickVals = {1, 2, 5, 10, 20, 50, 100}; // For x-axis
    private static final String[] suffixes = {"", "k", "M", "B"};

    public PacketGraph(int x, int y, int width, int height) {
        setBounds(x, y, width, height);
        xMax = 600;
        yMax = 0;
        xTick = 0; // Max time = 100 * 24 = 2400 seconds (40 minutes)
        yTick = 0;
        X_LENGTH = 9*getWidth()/10;     // Resizing reference variables,
        Y_LENGTH = 2*getHeight()/3;     // with pixels rounded up
        orig = new Point(getWidth()/20, 8*getHeight()/10);
        data = null;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        X_LENGTH = 9*getWidth()/10;
        Y_LENGTH = 7*getHeight()/10;
        orig.x = getWidth()/19;
        orig.y = 4*getHeight()/5;
        Graphics2D g2 = (Graphics2D) g;
        drawAxes(g2);
        if (data != null) {
            g2.setColor(Color.RED);
            int xPos;
            int yPos;
            Point prev = new Point(orig);
            for (int i = 0; i < xMax; i++) {
                xPos = orig.x + (int) ((i+1) * ((float)X_LENGTH / xMax));
                yPos = orig.y - (int) (data[i] * ((float)Y_LENGTH / yMax));
                g2.fillOval(xPos-1, yPos-1, 3, 3);
                g2.drawLine(prev.x, prev.y, xPos, yPos);
                prev = new Point(xPos, yPos);
            }
        }
    }

   /**
    * Draws the axis lines, labels, and ticks for the graph. This involves
    * calculating the scale for each axis, and formatting the y-axis labels
    * to include magnitude suffixes (e.g. 'k' = 1000). The y-axis is scaled
    * using an algorithm which cycles through values that have a left-most
    * digit of either 1, 2, or 5 (e.g. 10, 20, 50, 100, 200, 500...).
    *
    * @param  g2  Graphics2D object used to draw the graph's axes.
    */
    public void drawAxes(Graphics2D g2) {
        g2.setColor(Color.BLACK);
        g2.drawLine(orig.x, orig.y, orig.x + X_LENGTH, orig.y); // x-axis
        g2.drawLine(orig.x, orig.y, orig.x, orig.y - Y_LENGTH); // y-axis
        g2.drawString("Volume [bytes]", orig.x-40, orig.y-Y_LENGTH-9);
        g2.drawString("Time [s]", orig.x+(X_LENGTH/2), orig.y+40);
        if (xMax != 0) {    // Draw x-axis
            for (int i = 0; i < tickVals.length; i++) {
                if (xMax / tickVals[i] >= 8 && xMax / tickVals[i] <= 24) {
                    xTick = tickVals[i];
                    break;
                }
            }
            int tickPos;
            int labelPos = 3;
            for (int t = 0; t <= xMax; t += xTick) {
                tickPos = (int) Math.ceil((double) t * X_LENGTH / xMax);
                if (t >= 10) labelPos = 7 * log(10, t);
                g2.drawLine(orig.x + tickPos, orig.y, orig.x + tickPos, orig.y+5);
                g2.drawString(""+t, orig.x + tickPos - labelPos, orig.y+20);
            }
        }
        if (yMax != 0) {    // Draw y-axis
            int magnitude;
            int i = 1;
            while (i < yMax) {
                if (yMax / i >= 4 && yMax / i <= 10) {
                    yTick = i;
                    break;
                }
                magnitude = (int) Math.pow(10, log(10, i)); // e.g. 100 if i=372
                switch (i / magnitude) {
                    case 1:
                        i += magnitude;
                        break;
                    case 2:
                        i += 3 * magnitude;
                        break;
                    default: // (i / magnitude) should only ever be 5 here
                        i += 5 * magnitude;
                }
            }
            String label;
            int tickPos;
            int labelPos;
            int xOffset;
            for (int t = 0; t <= yMax; t += yTick) {
                label = getLabel(t);
                xOffset = (label.contains(".")) ? 5 : 9; // Adjusts for non-monospacing
                tickPos = (int) Math.ceil((double) t * Y_LENGTH / yMax);
                labelPos = 7 * label.length(); // Offsets x based on # of chars
                g2.drawLine(orig.x, orig.y - tickPos, orig.x-5, orig.y - tickPos);
                g2.drawString(label,
                            orig.x - labelPos - xOffset, orig.y - tickPos + 5);
            }
        }
    }

   /**
    * Imports data from an external class (PacketWindow) to populate data
    * fields that are used to draw the graph. xMax is used to scale the
    * x-axis, yMax is used to scale the y-axis, and the `data` array is
    * used to populate labels on the x-axis.
    *
    * @param  data  Array of packet data (each index = time period in seconds).
    * @param  xMax  Highest time period (in seconds) during which data was
    *               transmitted. This is used to scale the graph's x-axis.
    */
    public void importData(int[] data, int xMax) {
        this.xMax = xMax;
        this.yMax = data[data.length-1];
        this.data = data;
    }

    private static String getLabel(double tick) {
        int suffix = 0;
        if (tick < 1000) return "" + (int) tick + suffixes[suffix];
        while (tick >= 1000) { // Reduce large values & append suffix
            tick /= 1000;
            suffix++;
        }
		if (tick < 10.0) return "" + tick + suffixes[suffix]; // e.g. 1.2k
		else return "" + (int) tick + suffixes[suffix]; // e.g. 120k, 12k
    }

    private static int log(int b, int n) {
		if (b == 10) return (int) Math.log10(n);
		else return (int) (Math.log(n) / Math.log(b));
    }
}