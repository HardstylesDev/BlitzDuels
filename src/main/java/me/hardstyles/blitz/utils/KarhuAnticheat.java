package me.hardstyles.blitz.utils;

import me.hardstyles.blitz.Core;

import me.liwk.karhu.api.KarhuAPI;
import me.liwk.karhu.api.event.KarhuEvent;
import me.liwk.karhu.api.event.KarhuListener;
import me.liwk.karhu.api.event.impl.KarhuAlertEvent;
import me.liwk.karhu.api.event.impl.KarhuInitEvent;

public class KarhuAnticheat implements KarhuListener {
    final private Core core;
    public KarhuAnticheat(Core core){
        this.core = core;
        KarhuAPI.getEventRegistry().addListener(this);
    }


    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    private boolean enabled;

    @Override
    public void onEvent(KarhuEvent event) {
        if(event instanceof KarhuInitEvent){
            this.allowJoins();
            this.enabled = false;
            return;
        }
        if (event instanceof KarhuAlertEvent) {

        }
    }

    private void allowJoins(){
     //   Jedis jedisResource = BlitzSG.getInstance().getJedisPool().getResource();
     //   jedisResource.set("canJoin", "true");
     //   jedisResource.close();
    }
}




