/**
 * OS.java
 */
package com.sdc.file.structures;


/**
 * Operating System type
 * @author simone
 * Nov 10, 2015
 */
public enum OS {

    WINDOWS,
    UNIX,
    MAC,
    SOLARIS,
    UNKNOWN;
    
    public static boolean isWindows() {

        return (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0);

    }

    public static boolean isMac() {

        return (System.getProperty("os.name").toLowerCase().indexOf("mac") >= 0);

    }

    public static boolean isUnix() {
        String OS = System.getProperty("os.name").toLowerCase();
        return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 );
        
    }

    public static boolean isSolaris() {

        return (System.getProperty("os.name").toLowerCase().indexOf("sunos") >= 0);

    }
    
}
