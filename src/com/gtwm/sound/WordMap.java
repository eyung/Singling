package com.gtwm.sound;

import com.univocity.parsers.annotations.Parsed;
import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

import java.io.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WordMap {

    enum Type { n, v, a, r, m, p, s, pn, ar  }

    static class Mapping {
        @Parsed(index = 0)
        String wordKey;

        @Parsed(index = 1)
        String wordType;

        @Parsed(index = 2)
        String wordValue;

        @Parsed(index = 3)
        String wordSentimentPos;

        @Parsed(index = 4)
        String wordSentimentNeg;

        public Mapping() {

        }

        public Mapping(String x, String y, String z, String a, String b) {
            this.wordKey = x;
            this.wordType = y;
            this.wordValue = z;
            this.wordSentimentPos = a;
            this.wordSentimentNeg = b;
        }

        public void setKey(String thisKey) {
            wordKey = thisKey;
        }

        public String getKey() { return wordKey; }

        public void setType(String thisClass) { wordType = thisClass; }

        public String getType() { return wordType; }

        public void addType(String thisClass) {
            wordType = wordType + "," + thisClass;

            String[] strArr = wordType.split(",");
            Set<String> set = new HashSet<String>(Arrays.asList(strArr));

            String[] result = new String[set.size()];
            set.toArray(result);

            StringBuilder res = new StringBuilder();
            for (int i = 0; i < result.length; i++) {
                String string = result[i];
                if(i==result.length-1)
                    res.append(string);
                else
                    res.append(string).append(",");
            }

            wordType = res.toString();
        }

        public void setValue(String thisValue) { wordValue = thisValue; }

        public String getValue() { return wordValue; }

        public void addValue(String thisValue) {
            wordValue = wordValue + "," + thisValue;

            String[] strArr = wordValue.split(",");
            Set<String> set = new HashSet<String>(Arrays.asList(strArr));

            String[] result = new String[set.size()];
            set.toArray(result);

            StringBuilder res = new StringBuilder();
            for (int i = 0; i < result.length; i++) {
                String string = result[i];
                if(i==result.length-1)
                    res.append(string);
                else
                    res.append(string).append(",");
            }

            wordValue = res.toString();
        }

        public void setSentimentPos(String thisSentimentPos) {
            wordSentimentPos = thisSentimentPos;
        }

        public String getSentimentPos() { return wordSentimentPos; }

        public void setSentimentNeg(String thisSentimentNeg) {
            wordSentimentNeg = thisSentimentNeg;
        }

        public String getSentimentNeg() { return wordSentimentNeg; }

        public String toString() {
            return "Word{" +
                    " key=" + wordKey +
                    " type=" + wordType +
                    " value=" + wordValue +
                    " sentimentpos=" + wordSentimentPos +
                    " sentimentneg=" + wordSentimentNeg +
                    '}'+"\n";
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (obj.getClass() != getClass()) {
                return false;
            }
            Mapping map = (Mapping) obj;
            if (this.wordKey.equals(map.wordKey)) {
                return true;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return wordKey.hashCode();
        }

    }

}

class csvparser {

    public List<WordMap.Mapping> csvtoSenseMap(final String filePath) throws IOException {

        // BeanListProcessor converts each parsed row to an instance of a given class, then stores each instance into a list.
        BeanListProcessor<WordMap.Mapping> rowProcessor = new BeanListProcessor<WordMap.Mapping>(WordMap.Mapping.class);

        CsvParserSettings parserSettings = new CsvParserSettings();
        parserSettings.getFormat().setLineSeparator("\n");
        parserSettings.setProcessor(rowProcessor);
        parserSettings.setHeaderExtractionEnabled(false);
        parserSettings.getFormat().setComment('|');
        parserSettings.setNullValue("NULL");

        CsvParser parser = new CsvParser(parserSettings);
        parser.parse(getReader(filePath));

        // The BeanListProcessor provides a list of objects extracted from the input.
        List<WordMap.Mapping> beans = rowProcessor.getBeans();

        //for (SenseMap.Mapping bean : beans) {
        //  System.out.println(bean.getKey() + ", " + bean.getType() + ", " + bean.getValue());
        //}

        return beans;
    }

    public Reader getReader(String absolutePath) {
        try {
            return new InputStreamReader(new FileInputStream(new File(absolutePath)), "UTF-8");
        } catch (UnsupportedEncodingException | FileNotFoundException e) {
            throw new IllegalStateException("Unable to read input", e);
        }
    }
}