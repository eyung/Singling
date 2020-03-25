package com.gtwm.sound;

import java.util.stream.Stream;

public class Lexname {

    static class FileNumber {

        String fileNum;

        public FileNumber() {

        }

        public FileNumber(String x) {
            this.fileNum = x;
        }

        public void setFileNum(String thisNum) {
            fileNum = thisNum;
        }

        public String getFileNum() {
            return fileNum;
        }

        public String toString() {
            return "File number: " + fileNum;
        }

        public double[] toDouble(double[] x) {
            x = Stream.of(fileNum.split(" "))
                    .mapToDouble (Double::parseDouble)
                    .toArray();
            return x;
        }
    }
}
