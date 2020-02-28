package com.bookdeer.bookdeer.Dao;

import com.bookdeer.bookdeer.entity.Book;
import com.bookdeer.bookdeer.entity.BookCover;

import java.util.List;
import java.util.Map;

public interface BookDao {
    /**
     * 列出书籍列表
     * @return
     */
    Book queryBookById(Integer bookId);

    /**
     * 列出书籍列表
     * @return
     */
    List<Book> queryBook();

    /**
     * 根据最大的bookId
     * @return
     */
    Book queryBookMaxId();

    /**
     * 获取书籍封面信息
     * @param bookId
     * @return
     */
    BookCover queryBookCoverById(Integer bookId);

    /**
     * 获取书籍封面信息
     * @param bookId
     * @return
     */
    int  insertBookCover(Integer bookId);

    /**
     * 插入书的详细信息
     * @param book
     * @return
     */
    int insertBook(Book book);

    /**
     * 插入书的保存路径
     * @param book
     * @return
     */
    int insertBookFile(Book book);


    /**
     * 更新书的信息
     * @param book
     * @return
     */
    int updateBook(Book book);

    /**
     * 删除书的信息
     * @param bookId
     * @return
     */
    int deleteBook(int bookId);

    /**
     * 查询首字母及对应的书籍信息
     * @param owner
     * @return
     */
    List<Book>  queryMineBookList(String owner);

    boolean changeCategory(String bookId);
    boolean deleteMineBookList(String owner,String bookId);

    //region search
    /**
     * 列出书籍列表
     * @return
     */
    List<Book> searchBook(String searchType,String searchInput);
    //endregion

}
