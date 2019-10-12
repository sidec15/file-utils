package com.sdc.file.structures;

import javax.swing.JTextArea;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

import com.sdc.file.utils.Util;

/**
 * A Log4J appender that empties into a text area.
 */
public class JTextAreaAppender extends AppenderSkeleton {

    // --------------------------------------------------------------------------
    // Fields
    // --------------------------------------------------------------------------

    /**
     * Text area that logging statements are directed to.
     */
    private JTextArea textArea;

    // --------------------------------------------------------------------------
    // Constructors
    // --------------------------------------------------------------------------

    public JTextAreaAppender() {

    }

    // --------------------------------------------------------------------------
    // Public
    // --------------------------------------------------------------------------
    /**
     * @return the textArea_
     */
    public JTextArea getTextArea() {

        return textArea;
    }

    /**
     * @param textArea
     *            the textArea_ to set
     */
    public void setTextArea(JTextArea textArea) {

        this.textArea = textArea;
    }

    // --------------------------------------------------------------------------
    // Overrides org.apache.log4j.AppenderSkeleton
    // --------------------------------------------------------------------------

    /**
     * @see org.apache.log4j.AppenderSkeleton#append(org.apache.log4j.spi.LoggingEvent)
     */
    public void append(LoggingEvent event) {

        textArea.append(this.layout.format(event));

        if (layout.ignoresThrowable()) {
            String[] s = event.getThrowableStrRep();
            if (s != null)
                for (int i = 0; i < s.length; i++)
                    textArea.append(s[i]+Util.NEWLINE);
        }
    }

    /**
     * @see org.apache.log4j.Appender#requiresLayout()
     */
    public boolean requiresLayout() {

        return false;
    }

    /**
     * @see org.apache.log4j.Appender#close()
     */
    public void close() {

    }

}
