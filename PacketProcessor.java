/**
* Compsci 230 (2019 Semester 1) - Assignment 2
* NAME: Alexander Khouri
* STUDENT ID#: 6402238
* UPI: akho225
*/

import java.util.*;
import java.io.*;

public class PacketProcessor {
    static final int TIME = 1;
    static final int SOURCEIP = 2;
    static final int DESTIP = 4;
    static final int PACKETSIZE = 7;

   /**
    * Converts an unordered HashSet of IP addresses into a sorted array of
    * IP addresses, with the addresses sorted properly based on the
    * absolute value of each octet (e.g. 10.0.0.5 comes before 10.0.0.11).
    * 
    * @param  IPs  Unordered HashSet containing IP addresses.
    * @return      Array of sorted IP addresses.
    */
    public static String[] getSortedIPs(Set<String> IPs) {
        String[] sortedIPs = new String[IPs.size()];
        sortedIPs = IPs.toArray(sortedIPs);
        Arrays.sort(sortedIPs, new Comparator<String>() {
            public int compare(String IP1, String IP2) {
                String[] IP1octets = IP1.split("\\.");
                String[] IP2octets = IP2.split("\\.");
                String paddedIP1 = String.format("%03d%03d%03d%03d",
                                Integer.parseInt(IP1octets[0]),
                                Integer.parseInt(IP1octets[1]),
                                Integer.parseInt(IP1octets[2]),
                                Integer.parseInt(IP1octets[3]));
                String paddedIP2 = String.format("%03d%03d%03d%03d",
                                Integer.parseInt(IP2octets[0]),
                                Integer.parseInt(IP2octets[1]),
                                Integer.parseInt(IP2octets[2]),
                                Integer.parseInt(IP2octets[3]));
                return paddedIP1.compareTo(paddedIP2);
            }
        });
        return sortedIPs;
    }

   /**
    * Converts the IP packet data from an input File object into two
    * HashMaps - one for source IP addresses and another for destination
    * IP addresses. A sorted array of these IP addresses is displayed by
    * the combo box, and the values get passed into the HashMaps to
    * retrieve packet data for the graph.
    * <p>
    * A string value of "0" is inserted between consecutive tab characters,
    * so that each line can be split at those characters and analysed as
    * a grid with consistent formatting.
    *
    * @param  inputFile   Object representation of the selected text file.
    * @param  sourceData  HashMap with IP addresses as keys and an array
    *                     of the data sent from those addresses over time
    *                     (each index corresponds to one second in time).
    * @param  destData    HashMap with IP addresses as keys and an array
    *                     of the data sent to those addresses over time
    *                     (each index corresponds to one second in time).
    * @return             Maximum time period (in seconds) during which
    *                     data was sent to/from any of the IP addresses in
    *                     either of the HashMaps. This is used to scale the
    *                     x-axis of the graph.
    */
    public static int processFile(File inputFile,
                                HashMap<String, int[]> sourceData,
                                HashMap<String, int[]> destData) {
        double maxTime = 0.0; // Rounded to int at return statement
        String[] line;
        String lineStr = "";
        try {
            BufferedReader file = new BufferedReader(new FileReader(inputFile));
            int letter1 = file.read();
            int letter2 = file.read();
            while (letter1 != -1) {
                if (letter1 == 10) {
                    line = lineStr.split("\t");
                    if (Double.parseDouble(line[TIME]) > maxTime) {
                        maxTime = Double.parseDouble(line[TIME]);
                    }
                    addLineData(line, sourceData, SOURCEIP);
                    addLineData(line, destData, DESTIP);
                    lineStr = "";
                } else {
                    lineStr += (char) letter1;
                    if (letter1 == 9 && (letter2 == 9 || letter2 == 10)) {
                        lineStr += '0';
                    }
                }
                letter1 = letter2;
                letter2 = file.read();
            }
            file.close();
        }
        catch (Exception e) {
            System.out.println("ERROR WHILE READING INPUT FILE:\n" + e);
        }
        return (int) maxTime;
    }

   /**
    * Reads the packet data from a line and adds it to the corresponding IP
    * address in the HashMap. Each line contains two IP addresses (source and 
    * destination), so the `IP` parameter is used to distinguish between them.
    *
    * @param  line  Array of one line from the file, split at tab characters.
    * @param  data  HashMap of packet data for each IP (data is added here).
    * @param  IP    Column number for IP address (based on constant values).
    */
    public static void addLineData(String[] line,
                                    HashMap<String, int[]> data, int IP) {
        String key = line[IP]; // IP can be source or destination
        if (!line[SOURCEIP].equals("0") && !line[DESTIP].equals("0")) {
            if (data.keySet().contains(key) == false) {
                data.put(key, new int[4096]); // Max time period (seconds)
            }
            int time = (int) Double.parseDouble(line[TIME]);
            int[] times = data.get(key);
            times[time] += Integer.parseInt(line[PACKETSIZE]);
            if (times[time] > times[times.length-1]) {
                times[times.length-1] = times[time];
            } // The last index in the IP's array will be the maximum
        }     // amount of data transmitted during any second (i.e. yMax)
    }
}