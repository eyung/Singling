package com.gtwm.sound;

import org.jfugue.devtools.DiagnosticParserListener;
import org.jfugue.parser.ParserListenerAdapter;
import org.jfugue.pattern.Pattern;
import org.jfugue.player.ManagedPlayerListener;
import org.jfugue.player.Player;
import org.jfugue.temporal.TemporalPLP;
import org.staccato.StaccatoParser;

import javax.sound.midi.Sequence;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.util.concurrent.Semaphore;

public class SinglingPlayer implements Runnable {

    private volatile boolean running = true;
    private volatile boolean paused = false;

    private Pattern pattern;
    private Player player;

    private StaccatoParser parser = new StaccatoParser();
    private TemporalPLP plp = new TemporalPLP();

    public void setPattern (Pattern myPattern, Player myPlayer) {
        pattern = myPattern;
        player = myPlayer;
    }

    @Override
    public void run() {
        try {
            parser.addParserListener(plp);
            parser.parse(pattern);

            DiagnosticParserListener dpl = new DiagnosticParserListener();
            plp.addParserListener(dpl);

            LyricParserListener lpl = new LyricParserListener();
            plp.addParserListener(lpl);

            //player.play(pattern);
            player.delayPlay(1000, pattern);
            plp.parse();

            //player.getManagedPlayer().finish();

        } catch (Exception e) {
        }

    }

    public void stopMusic(Thread threadPlayer) {
        threadPlayer.interrupt();
        threadPlayer = null;
        player.getManagedPlayer().finish();
    }

    public void stop() {
        try {
            plp.wait();
        } catch (Exception e) {
        }
    }

    public void resume() {
        try {
            plp.notify();
        } catch (Exception e) {
        }
    }
}

class LyricParserListener extends ParserListenerAdapter {
    //String thisLyric;
    int offset=0;

    @Override
    public void onLyricParsed(String lyric) {
        //thisLyric = lyric;
        highlightWord(lyric);
    }

    private void highlightWord(String lyric) {
        int docLength = Main.textModel.getDocument().getLength();
        try {
            String textToSearch = Main.textModel.getDocument().getText(0, docLength);
            //System.out.println("Highlight: " + lyric + " | Offset: " + offset);
            offset = textToSearch.toLowerCase().indexOf(lyric.toLowerCase(), offset-lyric.length());
            if (offset != -1) {
                Highlighter hl = Main.textModel.getHighlighter();
                hl.removeAllHighlights();
                hl.addHighlight(offset, offset+lyric.length(), DefaultHighlighter.DefaultPainter);
                offset += lyric.length();
            }
        } catch (Exception e) {}
    }
}