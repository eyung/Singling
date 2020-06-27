package com.gtwm.sound;

import org.jfugue.devtools.DiagnosticParserListener;
import org.jfugue.parser.ParserListenerAdapter;
import org.jfugue.pattern.Pattern;
import org.jfugue.player.Player;
import org.jfugue.temporal.TemporalPLP;
import org.staccato.StaccatoParser;

import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

public class SinglingPlayer implements Runnable {

    private Pattern pattern;
    private Player player;

    StaccatoParser parser = new StaccatoParser();
    TemporalPLP plp = new TemporalPLP();

    public void setPattern (Pattern myPattern, Player myPlayer) {
        pattern = myPattern;
        player = myPlayer;
    }

    @Override
    public void run() {
        try {
            parser.addParserListener(plp);
            parser.parse(pattern);

            //DiagnosticParserListener dpl = new DiagnosticParserListener();
            //plp.addParserListener(dpl);

            LyricParserListener lpl = new LyricParserListener();
            plp.addParserListener(lpl);

            //player.play(pattern);
            player.delayPlay(1000, pattern);
            plp.parse();

            //player.getManagedPlayer().finish();

        } catch (Exception e) {}
    }

    public void stopMusic(Thread threadPlayer) {
        threadPlayer.interrupt();
        threadPlayer = null;
        player.getManagedPlayer().finish();
    }
}

class LyricParserListener extends ParserListenerAdapter {
    //String thisLyric;
    int offset;

    @Override
    public void onLyricParsed(String lyric) {
        //thisLyric = lyric;
        highlightWord(lyric);
    }

    private void highlightWord(String lyric) {
        int docLength = Main.textModel.getDocument().getLength();
        try {
            String textToSearch = Main.textModel.getDocument().getText(0, docLength);
            System.out.println("Highlight: " + lyric + " | Offset: " + offset);
            offset = textToSearch.toLowerCase().indexOf(lyric.toLowerCase(), offset);
            if (offset != -1) {
                Highlighter hl = Main.textModel.getHighlighter();
                hl.removeAllHighlights();
                hl.addHighlight(offset, offset+lyric.length(), DefaultHighlighter.DefaultPainter);
                offset += lyric.length();
            }
        } catch (Exception e) {}
    }
}