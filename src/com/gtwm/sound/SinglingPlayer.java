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

    private Pattern pattern;
    private Player player;

    // Initialise Parsers
    private StaccatoParser parser = new StaccatoParser();
    private TemporalPLP plp = new TemporalPLP();

    // Initalise Parser Listeners
    private DiagnosticParserListener dpl = new DiagnosticParserListener();
    private LyricParserListener lpl = new LyricParserListener();

    public void setPattern (Pattern myPattern, Player myPlayer) {
        pattern = myPattern;
        player = myPlayer;
    }

    @Override
    public void run() {
        try {
            parser.addParserListener(plp);
            parser.parse(pattern);

            // Output diagnostic data to console
            plp.addParserListener(dpl);

            // Highlight lyrics as music is played
            plp.addParserListener(lpl);

            //player.play(pattern);
            player.delayPlay(1000, pattern);

            // Start temporal parsing
            plp.parse();

            //player.getManagedPlayer().finish();

        } catch (Exception e) {
        }
    }

    public void stop() {
        lpl.stop();
    }

    public void resume() {
        lpl.resume();
    }
}

class LyricParserListener extends ParserListenerAdapter {
    int offset=0;
    private volatile boolean paused = false;

    @Override
    public void onLyricParsed(String lyric) {
        synchronized(this) {
            while (paused) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        highlightWord(lyric);
    }

    public void stop() {
        synchronized(this) {
            this.paused = true;
            notifyAll();
        }
    }

    public void resume() {
        synchronized(this) {
            this.paused = false;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            notifyAll();
        }
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