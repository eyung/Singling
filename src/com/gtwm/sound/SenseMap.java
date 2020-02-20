package com.gtwm.sound;

import com.univocity.parsers.annotations.Parsed;
import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

import java.io.*;
import java.util.List;

public class SenseMap {

    enum Classification {
        n, v, a
    }

    static class Mapping {
        @Parsed(index = 0)
        String wordKey;

        @Parsed(index = 1)
        Classification wordClass;

        @Parsed(index = 2)
        double wordValue;

        public Mapping() {

        }

        public Mapping(String x, Classification y, double z) {
            this.wordKey = x;
            this.wordClass = y;
            this.wordValue = z;
        }

        public void setKey(String thisKey) {
            wordKey = thisKey;
        }

        public String getWordKey() {
            return wordKey;
        }

        public void setClass(Classification thisClass) {
            wordClass = thisClass;
        }

        public Classification getWordClass() {
            return wordClass;
        }

        public void setValue(double thisValue) {
            wordValue = thisValue;
        }

        public double getWordValue() {
            return wordValue;
        }

        public String toString() {
            return "Word{" +
                    "key=" + wordKey +
                    "class=" + wordClass +
                    "value=" + wordValue +
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

        //or (SenseMap.Mapping bean : beans){
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