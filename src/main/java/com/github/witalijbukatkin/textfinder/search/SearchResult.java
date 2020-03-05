/*
 * Copyright (c) 2020. Witalij Bukatkin
 * Github profile: https://github.com/witalijbukatkin
 */

package com.github.witalijbukatkin.textfinder.search;

@FunctionalInterface
public interface SearchResult {
    void apply(String fileName, int countFound);
}
