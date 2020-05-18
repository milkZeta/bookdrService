package com.bookdeer.bookdeer.Dao;

import com.bookdeer.bookdeer.entity.Book;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;
import static org.junit.Assert.assertEquals;


@RunWith(SpringRunner.class)
@SpringBootTest
public class BookDaoTest {
    @Autowired
    private BookDao bookDao;
    @Test
    public void queryBook() {
        List<Book> bookList=bookDao.queryBook();
    }

    @Test
    public void queryBookId() {
        Book book=bookDao.queryBookMaxId();
        assertEquals("解忧杂货店",book.getBookName());
    }

    @Test
    public void insertBook() {
        Book book = new Book();
        book.setBookId(3);
        book.setBookName("少有人走的路");
        book.setBookDesc("励志开导");
        book.setCreateBy("陈一卿");
        book.setUpdateBy("陈一卿");
        Date date=new Date();
        book.setUpdateTime(date);
        book.setCreateTime(date);
        int effectRow=bookDao.insertBook(book);

    }

    @Test
    public void updateBook() {
        Book book =new Book();
        Date date =new Date();
        book.setUpdateTime(date);
        book.setBookId(1);
        int effectRow=bookDao.updateBook(book);
    }

    @Test
    public void deleteBook() {
        int effectRow=bookDao.deleteBook(1);
    }
}