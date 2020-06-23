package com.gtwm.sound;

import org.jfugue.pattern.Pattern;
import org.jfugue.player.Player;

public class SinglingPlayer implements Runnable {

    private Pattern pattern;
    private Player player;

    public void setPattern (Pattern myPattern, Player myPlayer) {
        pattern = myPattern;
        player = myPlayer;
    }

    @Override
    public void run() {
        try {
            player.play(pattern);
            player.getManagedPlayer().finish();
        } catch (Exception e) {

        }
    }

    public void stopMusic(Thread threadPlayer) {
        threadPlayer.interrupt();
        threadPlayer = null;
        player.getManagedPlayer().finish();
    }
}
