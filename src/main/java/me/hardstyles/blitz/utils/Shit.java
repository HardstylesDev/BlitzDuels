package me.hardstyles.blitz.utils;

import me.hardstyles.blitz.Core;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;

public class Shit {
    Field logField;
    public Shit(Core core){
        try{
            logField = java.util.logging.Logger.class.getDeclaredField("filter");
        }catch (NoSuchFieldException e){

        }
    }
}
