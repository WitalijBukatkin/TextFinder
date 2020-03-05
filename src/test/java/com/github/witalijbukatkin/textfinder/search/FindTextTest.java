/*
 * Copyright (c) 2020. Witalij Bukatkin
 * Github profile: https://github.com/witalijbukatkin
 */

package com.github.witalijbukatkin.textfinder.search;

import com.github.witalijbukatkin.textfinder.bean.Part;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;

class FindTextTest {
    private FindText findText = new FindText(null);

    @Test
    void patternEqualsData() {
        ArrayList<Part> parts = new ArrayList<>();

        String data = " False";
        String pattern = " False";
        int offset = 0;

        findText.findOne(parts, offset, data, pattern, 0);

        assertIterableEquals(
                List.of(new Part(0, offset, 5, offset)),  parts);
    }

    @Test
    void fullString(){
        ArrayList<Part> parts = new ArrayList<>();

        String data = " False or False";
        String pattern = "False";
        int offset = 0;

        findText.findOne(parts, offset, data, pattern, 0);

        List<Part> expected = List.of(
                new Part(1, offset, 5, offset),
                new Part(10, offset, 14, offset)
        );

        assertIterableEquals(expected,  parts);
    }

    @Test
    void partString(){
        ArrayList<Part> parts = new ArrayList<>();

        String data1 = " False or F";
        String data2 = "alse or False";

        int offset1 = 0;
        int offset2 = data1.length();

        String pattern = "False";

        findText.findOne(parts, offset1, data1, pattern, 0);

        assertIterableEquals(List.of(
                new Part(1, offset1, 5, offset1),
                new Part(10, offset1)),  parts);

        findText.findOne(parts, offset2, data2, pattern, offset2 - 10);

        assertIterableEquals(List.of(
                new Part(1, offset1, 5, offset1),
                new Part(10, offset1, 3, offset2),
                new Part(8, offset2, 12, offset2)),  parts);
    }

    @Test
    void veryLongPattern(){
        ArrayList<Part> parts = new ArrayList<>();

        String data1 = "Text Fa";
        String data2 = "lseFal";
        String data3 = "se Text";

        int offset1 = 0;
        int offset2 = data1.length();
        int offset3 = offset2 + data2.length();

        String pattern = " FalseFalse";

        findText.findOne(parts, offset1, data1, pattern, 0);

        assertIterableEquals(List.of(new Part(4, offset1)),  parts);

        findText.findOne(parts, offset2, data2, pattern, offset2 - 4);

        assertIterableEquals(List.of(new Part(4, offset1)),  parts);

        findText.findOne(parts, offset3, data3, pattern, offset3 - 4);

        assertIterableEquals(List.of(new Part(4, offset1, 1, offset3)),  parts);
    }
}
