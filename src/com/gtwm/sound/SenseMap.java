package com.gtwm.sound;

import com.univocity.parsers.annotations.Parsed;
import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

import java.io.*;
import java.util.List;

public class SenseMap {

    enum Type { n, v, a }

    static class Mapping {
        @Parsed(index = 0)
        String wordKey;

        @Parsed(index = 1)
        Type wordType;

        @Parsed(index = 2)
        double wordValue;

        public Mapping() {

        }

        public Mapping(String x, Type y, double z) {
            this.wordKey = x;
            this.wordType = y;
            this.wordValue = z;
        }

        public void setKey(String thisKey) {
            wordKey = thisKey;
        }

        public String getKey() {
            return wordKey;
        }

        public void setType(Type thisClass) {
            wordType = thisClass;
        }

        public Type getType() {
            return wordType;
        }

        public void setValue(double thisValue) {
            wordValue = thisValue;
        }

        public double getValue() {
            return wordValue;
        }

        public String toString() {
            return "Word{" +
                    " key=" + wordKey +
                    " type=" + wordType +
                    " value=" + wordValue +
                    '}';
        }
    }

}

class csvparser {

    public List<SenseMap.Mapping> csvtoSenseMap(final String filePath) throws IOException {

        // BeanListProcessor converts each parsed row to an instance of a given class, then stores each instance into a list.
        BeanListProcessor<SenseMap.Mapping> rowProcessor = new BeanListProcessor<SenseMap.Mapping>(SenseMap.Mapping.class);

        CsvParserSettings parserSettings = new CsvParserSettings();
        parserSettings.getFormat().setLineSeparator("\n");
        parserSettings.setProcessor(rowProcessor);
        parserSettings.setHeaderExtractionEnabled(true);

        CsvParser parser = new CsvParser(parserSettings);
        parser.parse(getReader(filePath));

        // The BeanListProcessor provides a list of objects extracted from the input.
        List<SenseMap.Mapping> beans = rowProcessor.getBeans();

        //For (SenseMap.Mapping bean : beans){
        //    System.out.println(bean.getWordKey() + ", " + bean.getWordClass() + ", " + bean.getWordValue());
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