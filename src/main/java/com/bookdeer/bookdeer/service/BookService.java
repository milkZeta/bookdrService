package com.bookdeer.bookdeer.service;

import com.bookdeer.bookdeer.entity.Book;
import com.bookdeer.bookdeer.entity.BookCover;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Blob;
import java.util.List;
import java.util.Map;

public interface BookService {

    /**
     * 列出书籍列表
     * @return
     */
    List<Book> queryBook();

    /**
     * 根据Id列出书的详情
     * @return
     */
    Book queryBookMaxId();

    /**
     * 插入书的详细信息
     * @param book
     * @return
     */
    Integer addBook(Book book);
    /**
     * 插入书的详细信息
     * @param book
     * @return
     */
    boolean insertCover(MultipartFile image, Book book, String imagePath);

    /**
     * 更新书的信息
     * @param book
     * @return
     */
    boolean updateBook(Book book);

    /**
     * 删除书的信息
     * @param bookId
     * @return
     */
    boolean deleteBook(int bookId);

    //region Mine
    Map<String,List<Book>> queryMineBookList(String owner);
    boolean deleteMineBookList(String owner,String book_id);
    //endregion

    //region index
    List<Book> convertBlobList(List<Book> bookList);

    //将传入的blob转换为Base64
    String blobConvertToBase64(Blob blob);
    //将个人用书转为公共资源
    boolean changeCategory(String bookId);
    //
    byte[] downloadResource(Integer bookId);
    //endregion

    //region search
    /**
     * 根据查询条件查询书籍信息
     * @return
     */
    List<Book> searchBook(String searchType,String searchInput);
    //endegion

    //读取文件并返回字符串
    List<String> fileConvertToBase64Str(Integer bookId);
}
