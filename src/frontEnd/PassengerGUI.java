// Created by satyaki on 2017-04-03
package frontEnd;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PassengerGUI
{
    public PassengerGUI()
    {
        JFrame passengerFrame = new JFrame();
        passengerFrame.setTitle("Access: Passenger");
        passengerFrame.setLayout(new BorderLayout());

        JTextField searchField = new JTextField(10);
        JRadioButton dateSearch = new JRadioButton("Date");
        JRadioButton sourceSearch = new JRadioButton("Source");
        JRadioButton destSearch = new JRadioButton("Destination");
        sourceSearch.setEnabled(true);

        ButtonGroup group = new ButtonGroup();
        group.add(destSearch);
        group.add(sourceSearch);
        group.add(dateSearch);


        JLabel label = new JLabel();
        //aaaa

    }

    public static void main(String[] args){ PassengerGUI pass = new PassengerGUI(); }
}
