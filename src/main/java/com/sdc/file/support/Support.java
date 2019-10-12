/**
 * Support.java
 */
package com.sdc.file.support;

import java.io.IOException;
import java.util.Random;

import com.sdc.file.structures.COORDINATES_TYPE;

/**
 * @author Simone De Cristofaro
 * @created 16/nov/2012
 */
public class Support {

	public static final double radiantsOfOneDegree=0.01745329251994;
	public static final double earthRadius=6371005.076123;
	
	
	/**
	 * @param min Lower bound (includec)
	 * @param max Upper bound (included)
	 * @return a random integer according to the specified bounds
	 */
	public static int getRandomInt(int min,int max) {
		return (int) ((max+1-min)* Math.random())+min;
	}
	
	/**
	 * @param min Lower bound (includec)
	 * @param max Upper bound (included)
	 * @return a random double according to the specified bounds
	 */
	public static double getRandomDouble(double min,double max) {
		double random = new Random().nextDouble();
		return min + (random * (max - min));
	}

	/**
	 * Return a formatted string of the elapsed time between <code>start</code> and <code>stop</code>
	 * @param start Initial instant in millisecond
	 * @param stop Final instant in millisecond
	 * @return format type: <i>nDays d nHours h nMinutes m nSeconds s nMilliSeconds ms</i>; e.g "1 d 2 h 40 m 30 s 100 ms"
	 */
	public static String getElapsedTime(long start,long stop) {
		return formatTimer( stop-start);
	}
	
	
	/**
	 * @param l Instant to format in milliseconds
	 * @return a formatted string: <i>nDays d nHours h nMinutes m nSeconds s nMilliSeconds ms</i>; e.g "1 d 2 h 40 m 30 s 100 ms"
	 */
	public static String formatTimer(long l) {
		int day=0;
		int hour=0;
		int minute=0;
		int second=0;
		int milliSecond=0;
		
		day=(int) (l/86400000);
		hour=(int) ((l-day*86400000)/3600000);
		minute=(int) ((l-day*86400000-hour*3600000)/60000);
		second=(int) ((l-day*86400000-hour*3600000-minute*60000)/1000);
		milliSecond=(int) (l-day*86400000-hour*3600000-minute*60000-second*1000);
		StringBuilder sb=new StringBuilder();
		if (day>0) sb.append(day).append("d ");
		if (hour>0) sb.append(hour).append("h ");
		if (minute>0) sb.append(minute).append("m ");
		if (second>0) sb.append(second).append("s ");
		if (milliSecond>0 || sb.length()==0) sb.append(milliSecond).append("ms");
		return sb.toString();
	}
	
	
	/**
	 * @param x1 X coordinate of the first point
	 * @param y1 Y coordinate of the first point
	 * @param x2 X coordinate of the second point
	 * @param y2 Y coordinate of the second point
	 * @param cType 
	 * @return the distance between the two points, considering coordinate type (degrees|metres)
	 */
	public static double getDistance(double x1,double y1,double x2,double y2, COORDINATES_TYPE cType) {
        double sqrabx, sqraby;
        double leng;

       double xFactor=cType==COORDINATES_TYPE.DEGREE?Math.cos(y1*radiantsOfOneDegree) : 1 ;
       sqrabx=(x1-x2)*xFactor;
       sqraby=y1-y2;
       leng=Math.sqrt(sqrabx*sqrabx+sqraby*sqraby);
       
       if(cType==COORDINATES_TYPE.DEGREE)
    	   leng*=radiantsOfOneDegree*earthRadius;
       
       return leng;
	}
	
	public static void main(String[]args) throws IOException, InterruptedException {
		long l=0L;
		int n=10;
		int min=60000*10;
		int max=86400000*4;
		System.out.println("l = "+l+" --> " + formatTimer(l));
		for(int i=0;i<n;i++) {
			l+=getRandomInt(min, max);
			System.out.println("l = "+l+" --> " + formatTimer(l));
			Thread.sleep(300);
		}

		System.in.read();
	}
	
	
}
