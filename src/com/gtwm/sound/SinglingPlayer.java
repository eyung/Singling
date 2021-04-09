package com.gtwm.sound;

import org.jfugue.devtools.DiagnosticParserListener;
import org.jfugue.parser.ParserListenerAdapter;
import org.jfugue.pattern.Pattern;
import org.jfugue.player.Player;
import org.jfugue.temporal.TemporalPLP;
import org.staccato.StaccatoParser;

import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;

public class SinglingPlayer implements Runnable {

    private Pattern pattern;
    private Player player;
    private double delay;

    // Initialise Parsers
    private StaccatoParser parser = new StaccatoParser();
    private TemporalPLP plp = new TemporalPLP();

    // Initialise Parser Listeners
    private DiagnosticParserListener dpl = new DiagnosticParserListener();
    private LyricParserListener lpl = new LyricParserListener();
    private MarkerParserListener mpl = new MarkerParserListener();

    public void setPattern (Pattern myPattern, Player myPlayer, double myDelay) {
        pattern = myPattern;
        player = myPlayer;
        delay = myDelay;
    }

    @Override
    public void run() {
        try {
            parser.addParserListener(plp);
            parser.parse(pattern);

            // Output diagnostic data to console
            //plp.addParserListener(dpl);

            // Highlight lyrics as music is played
            plp.addParserListener(lpl);

            // Output marker data to console
            plp.addParserListener(mpl);

            //player.play(pattern);
            //player.delayPlay(1000, pattern);
            player.delayPlay((long) delay*1000, pattern);

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
            //System.out.println(lyric);
            doHighlight(lyric);
            sendNLPOutput(lyric);
        }
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

    private void doHighlight(String lyric) {
        int docLength = Main.textModel.getDocument().getLength();
        try {
            String textToSearch = Main.textModel.getDocument().getText(0, docLength);
            //System.out.println("Highlight: " + lyric + " | Offset: " + offset);
            offset = textToSearch.toLowerCase().indexOf(lyric.toLowerCase(), offset-lyric.length());
            if (offset != -1) {
                Highlighter hl = Main.textModel.getHighlighter();
                hl.removeAllHighlights();
                hl.addHighlight(offset, offset+lyric.length(), new DefaultHighlighter.DefaultHighlightPainter(Color.CYAN));
                offset += lyric.length() + 1;
            }
        } catch (Exception e) {}
    }

    private void sendNLPOutput(String lyric) {
        try {
            DialogNLPConsole.textModel.append(lyric + ": ");
        } catch (Exception e) {}
    }
}

class MarkerParserListener extends ParserListenerAdapter {
    private volatile boolean paused = false;

    @Override
    public void onMarkerParsed(String marker) {
        synchronized(this) {
            while (paused) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //System.out.println(marker);
            sendNLPOutput(marker);
        }
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

    private void sendNLPOutput(String marker) {
        try {
            Main.consoleTextModel.append(marker + "\n");
        } catch (Exception e) {}
    }
}