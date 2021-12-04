/*

https://stackoverflow.com/questions/14800597/get-system-uptime-in-java

In Windows, you can execute the net stats srv command, and in Unix, you can execute the uptime command. Each output must be parsed to acquire the uptime. This method automatically executes the necessary command by detecting the user's operating system.

Update info for Windows:  net statistics workstation

*/
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


public class SystemUptime
{

   public static long getSystemUptime() throws Exception {
       long uptime = -1;
       String os = System.getProperty("os.name").toLowerCase();
       if (os.contains("win")) {
           //Process uptimeProc = Runtime.getRuntime().exec("net stats srv");
           Process uptimeProc = Runtime.getRuntime().exec("net statistics workstation");
           BufferedReader in = new BufferedReader(new InputStreamReader(uptimeProc.getInputStream()));
           String line;
           while ((line = in.readLine()) != null) {
               if (line.startsWith("Statistics since")) {
                   //SimpleDateFormat format = new SimpleDateFormat("'Statistics since' MM/dd/yyyy hh:mm:ss a");
                   SimpleDateFormat format = new SimpleDateFormat("'Statistics since' dd/MM/yyyy hh:mm:ss a");
                   Date boottime = format.parse(line);
                   uptime = System.currentTimeMillis() - boottime.getTime();
                   break;
               }
           }
       } else if (os.contains("mac") || os.contains("nix") || os.contains("nux") || os.contains("aix")) {
           Process uptimeProc = Runtime.getRuntime().exec("uptime");
           BufferedReader in = new BufferedReader(new InputStreamReader(uptimeProc.getInputStream()));
           String line = in.readLine();
           if (line != null) {
               Pattern parse = Pattern.compile("((\\d+) days,)? (\\d+):(\\d+)");
               Matcher matcher = parse.matcher(line);
               if (matcher.find()) {
                   String _days = matcher.group(2);
                   String _hours = matcher.group(3);
                   String _minutes = matcher.group(4);
                   int days = _days != null ? Integer.parseInt(_days) : 0;
                   int hours = _hours != null ? Integer.parseInt(_hours) : 0;
                   int minutes = _minutes != null ? Integer.parseInt(_minutes) : 0;
                   uptime = (minutes * 60000) + (hours * 60000 * 60) + (days * 6000 * 60 * 24);
               }
           }
       }
       return uptime;
   }


   public static void main (String[] args)
   {
      try
      {
         long millisec = getSystemUptime();

         /*
         long sec  = millisec / 1000;

         long min  = (millisec - sec * 1000) / 1000 / 60;

         long hour = (millisec - (sec + min * 60) * 1000 ) / 1000 / 60 / 60;

         long day  = (millisec - (sec + min * 60 + hour * 60 * 60) * 1000 ) / 1000 / 60 / 60 / 24;
         */

         double days = 1.0 * millisec / 1000 / 60 / 60 / 24;


         System.out.println ("System uptime: " + String.format ("%.2f", days) + " days.");

      }
      catch (Exception err)
      {
         err.printStackTrace();
      }
   }
}