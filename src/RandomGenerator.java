import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.text.DecimalFormat;

public class RandomGenerator
{
    static String[] cities = {"London, England", "Tokyo, Japan", "Paris, France", "Berlin, Germany", "Seoul, South Korea", "Moscow, Russia", "Madrid, Spain",
            "Hong Kong, China", "Calgary, Canada", "Vancouver, Canada", "Athens, Greece", "Johannesburg, South Africa", "Los Angeles, USA", "Florence, Italy", "Sydney, Australia",
            "Amsterdam, Netherlands", "New Delhi, India", "Cancun, Mexico", "Rio De Janeiro, Brazil", "Ottawa, Canada", "Reykjavik, Iceland", "New York, USA", "Dubai, UAE",
            "Barcelona, Spain", "Bangkok, Thailand"};
	static String[] dates = {"2017-04-01", "2017-04-02", "2017-04-03", "2017-04-04", "2017-04-05", "2017-04-06", "2017-04-07", "2017-04-08", "2017-04-09", "2017-04-10"};
	static String[] times = {"00:00", "02:15", "04:30", "06:45", "09:00", "11:15", "13:30", "15:45", "18:00", "20:15", "22:30", "23:45"};
	static String[] duration = {"01:30", "03:10", "04:20", "06:50", "07:15", "09:10", "12:00", "14:15", "02:15", "07:40", "05:40", "11:30"};

    public static int randomInt(int lo, int hi)
    {
        Random r = new Random();
        int d = r.nextInt(hi - lo + 1) + lo;
        return d;
    }

    public static double randomDouble(int lo, int hi)
    {
        Random r = new Random();
        double randomValue = lo + (hi - lo)*r.nextDouble();
        return randomValue;
    }

    public static Boolean checkFNum(ArrayList<Integer> list, int n)
    {
        Boolean valid = true;
        for(int i = 0; i < list.size(); i++)
        {
            if(n == list.get(i))
            {
                valid = false;
                break;
            }
        }
        return valid;
    }

    public static void main(String[] args)
    {
        DecimalFormat df = new DecimalFormat(".##");
        ArrayList<Integer> fList = new ArrayList<>();
        PrintWriter write = null;
        try{
            write = new PrintWriter("flightCatalog1.txt");
        }catch(FileNotFoundException e)
        {e.printStackTrace();}

        for(int i = 0; i < 5; i++)
        {
            int fnum = randomInt(1000000, 9999999);
            int srcI = randomInt(0, 24);
            int destI = randomInt(0, 24);
            int dateI = randomInt(0, 9);
            int timeI = randomInt(0, 11);
            int durI = randomInt(0, 11);
            int seats = randomInt(1, 2);
            double price = randomDouble(200, 2001);
            price = Double.parseDouble(df.format(price));

            while(destI == srcI)
                destI = randomInt(0, 24);

            while(!checkFNum(fList, fnum))
                fnum = randomInt(100000, 999999);

            fList.add(fnum);

			String line = fnum + ";" + cities[srcI] + ";" + cities[destI] + ";" + dates[dateI] + ";" + times[timeI] + ";" + duration[durI] + ";" + seats + ";" + seats + ";" + price;
			write.println(line);
        }
        write.close();
    }
}
