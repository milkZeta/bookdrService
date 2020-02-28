package com.bookdeer.bookdeer.entity;

import java.sql.Blob;

public class BookCover {
    private Integer bookId;
    private Object bookCover;

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public Object getBookCover() {
        return bookCover;
    }

    public void setBookCover(Object bookCover) {
        this.bookCover = bookCover;
    }
}
