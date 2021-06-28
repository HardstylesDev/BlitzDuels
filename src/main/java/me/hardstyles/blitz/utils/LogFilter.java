package me.hardstyles.blitz.utils;


import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.apache.logging.log4j.message.Message;
import org.bukkit.Bukkit;

import java.util.logging.Filter;
import java.util.logging.LogRecord;

public class LogFilter implements Filter {


    @Override
    public boolean isLoggable(LogRecord record) {
        Bukkit.broadcastMessage(record.getMessage());
        if(record.getMessage().contains("shit")){
            return false;
        }
        return true;
    }
}
