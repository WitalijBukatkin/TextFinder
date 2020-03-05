/*
 * Copyright (c) 2020. Witalij Bukatkin
 * Github profile: https://github.com/witalijbukatkin
 */

package com.github.witalijbukatkin.textfinder.bean;

import java.io.Serializable;
import java.util.Objects;

public class Part implements Serializable {
    private Integer indexStart;
    private Integer pageStart;

    private Integer indexEnd;
    private Integer pageEnd;

    public Part(Integer indexStart, Integer pageStart, Integer indexEnd, Integer pageEnd) {
        this.indexStart = indexStart;
        this.pageStart = pageStart;
        this.indexEnd = indexEnd;
        this.pageEnd = pageEnd;
    }

    public Part(Integer indexStart, Integer pageStart){
        this(indexStart, pageStart, null, null);
    }

    public Integer getIndexStart() {
        return indexStart;
    }

    public Integer getPageStart() {
        return pageStart;
    }

    public Integer getIndexEnd() {
        return indexEnd;
    }

    public Part setIndexEnd(Integer indexEnd) {
        this.indexEnd = indexEnd;
        return this;
    }

    public Integer getPageEnd() {
        return pageEnd;
    }

    public Part setPageEnd(Integer pageEnd) {
        this.pageEnd = pageEnd;
        return this;
    }

    @Override
    public String toString() {
        return "Part{" +
                "indexStart=" + indexStart +
                ", pageStart=" + pageStart +
                ", indexEnd=" + indexEnd +
                ", pageEnd=" + pageEnd +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Part part = (Part) o;
        return Objects.equals(indexStart, part.indexStart) &&
                Objects.equals(pageStart, part.pageStart) &&
                Objects.equals(indexEnd, part.indexEnd) &&
                Objects.equals(pageEnd, part.pageEnd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(indexStart, pageStart, indexEnd, pageEnd);
    }
}
