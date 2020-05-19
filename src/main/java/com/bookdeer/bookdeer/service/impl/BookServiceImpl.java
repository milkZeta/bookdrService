package com.bookdeer.bookdeer.service.impl;

import com.bookdeer.bookdeer.Dao.BookDao;
import com.bookdeer.bookdeer.entity.Book;
import com.bookdeer.bookdeer.entity.BookCover;
import com.bookdeer.bookdeer.service.BookService;
import com.mchange.util.Base64Encoder;
import oracle.sql.BLOB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.Date;

@Service
public class BookServiceImpl implements BookService {
    @Autowired
    public BookDao bookDao;
    @Override
    public List<Book> queryBook() {
        List<Book>  bookList=bookDao.queryBook();
        return bookDao.queryBook();
    }

    @Override
    public Book queryBookMaxId() {
        return bookDao.queryBookMaxId();
    }


    @Transactional
    @Override
    public Integer addBook(Book book) {
        if(book.getBookName()!=null) {
            Date date = new Date();
            book.setCreateTime(date);

            try{
                //插入书的存储路径(传入保存路径和书名)
                Book book1=bookDao.queryBookMaxId();
                int bookId=0;
                if(book1==null)
                    bookId=1;
                else bookId=book1.getBookId()+1;
                book.setBookId(bookId);
                boolean saveFlag=saveBookFile(book);
                int effctRow=bookDao.insertBook(book);
                if(effctRow>0){
                    return bookId;
                }else{
                    throw new RuntimeException("插入书籍信息失败！");
                }
            }catch(Exception e)
            {
                throw  new RuntimeException("插入书籍信息失败"+e.getMessage());
            }
        }else{
            throw new RuntimeException("书籍名不能为空!");
        }
    }

    @Transactional
    public boolean insertCover(MultipartFile image, Book book, String imagePath){
        InputStream fis = null;
        BookCover bookCover;
        OutputStream ops = null;
        int insertFlag=bookDao.insertBookCover(book.getBookId());
        if(insertFlag>0) {
            try {
                bookCover = bookDao.queryBookCoverById(book.getBookId());
                Blob blob = (Blob)bookCover.getBookCover();
                byte[] data = null;
                fis = new FileInputStream(imagePath);
                ops=blob.setBinaryStream(0);
                data = FileCopyUtils.copyToByteArray(fis);
                ops.write(data);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }finally {
                try {
                    ops.flush();
                    ops.close();
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;

    }

    public boolean saveBookFile(Book book) {
        boolean flag=false;
        try {
            if (book.getBookId() != null && book.getBookFilePath() != null) {

                int effectedNum = bookDao.insertBookFile(book);
                flag=true;
            }
           }
            catch (Exception e){
                throw new RuntimeException("保存书籍路径信息失败:" + e.toString());
            }
        return flag;
    }

    @Transactional
    @Override
    public boolean updateBook(Book book) {
        // 空值判断，主要是areaId不为空
        if (book.getBookId() != null && book.getBookId() > 0) {
            // 设置默认值
            book.setUpdateTime(new Date());
            try {
                // 更新书籍信息
                int effectedNum = bookDao.updateBook(book);
                if (effectedNum > 0) {
                    return true;
                } else {
                    throw new RuntimeException("更新书籍信息失败!");
                }
            } catch (Exception e) {
                throw new RuntimeException("更新书籍信息失败:" + e.toString());
            }
        } else {
            throw new RuntimeException("书籍信息不能为空！");
        }
    }

    @Transactional
    @Override
    public boolean deleteBook(int bookId) {
        if (bookId > 0) {
            try {
                // 删除区域信息
                int effectedNum = bookDao.deleteBook(bookId);
                if (effectedNum > 0) {
                    return true;
                } else {
                    throw new RuntimeException("删除书籍信息失败!");
                }
            } catch (Exception e) {
                throw new RuntimeException("删除书籍信息失败:" + e.toString());
            }
        } else {
            throw new RuntimeException("书籍Id不能为空！");
        }
    }
    //将封面数据读取出来，点击书的名字时会显示
    public List<Book> convertBlobList(List<Book> list){
        for(int i=0;i<list.size();i++){
            Blob blob=(Blob)list.get(i).getBookCover();
            String coverStr=blobConvertToBase64(blob);
            list.get(i).setBookCover(coverStr);
        }
        return list;
    }
    public String blobConvertToBase64(Blob blob){
        String result="";
        if(blob!=null) {
            try {
                InputStream inputStream = blob.getBinaryStream();
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                byte[] buffer = new byte[100];
                int n = 0;
                while (-1 != (n = inputStream.read(buffer))) {
                    output.write(buffer, 0, n);
                }
                Base64.Encoder encoder = Base64.getEncoder();
                result=encoder.encodeToString(output.toByteArray());
                output.close();
            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    //读取文件并返回字符串
    @Override
    public List<String> fileConvertToBase64Str(Integer bookId){
        List<String> result=readResource(bookId);
//        for(int i=0;i<infoStr.length;i++){
//            result+=infoStr[i];
//        }
        return result;
    }

    //返回首字母与Book变量
    public Map<String,List<Book>> queryMineBookList(String owner){
        Map<String,List<Book>> bookMap=new HashMap<>();
        try {
            List<Book> bookList=bookDao.queryMineBookList(owner);
            List<Book> bookBasket= new ArrayList<>();
            //将同一个首字母的书放到一个List里面
            for (int i = 0; i < bookList.size(); i++) {
                if (i != 0 && !bookList.get(i).getFirstWord().equals(bookList.get(i - 1).getFirstWord())) {
                    //将封面字段转换base64字符串
                    bookBasket=convertBlobList(bookBasket);
                    bookMap.put(bookList.get(i-1).getFirstWord(), bookBasket);
                    bookBasket = new ArrayList<>();
                    bookBasket.add(bookList.get(i));
                } else {
                    bookBasket.add(bookList.get(i));
                }
            }
            bookBasket=convertBlobList(bookBasket);
            if(bookList.size()!=0){
                bookMap.put(bookList.get(bookList.size() - 1).getFirstWord(), bookBasket);
            }

        }catch(Exception e){
            throw new RuntimeException("获取个人书籍失败:" + e.toString());
        }
        return bookMap;
    }
    public boolean deleteMineBookList(String owner,String book_id){
        return bookDao.deleteMineBookList(owner,book_id);
    }
    //修改catogary字段的值
    public boolean changeCategory(String bookId){
        return bookDao.changeCategory(bookId);
}

    //下载服务器资源要用另存为的方式，之前的方式读取不完全
    public byte[] downloadResource(Integer bookId){
        String filePath=bookDao.queryBookById(bookId).getBookFilePath();

        InputStream inputStream = null;
        byte[] buffer = new byte[300];
        List<byte[]> bufferList=new ArrayList<byte[]>();
        try {
            inputStream = new FileInputStream(filePath);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            int n = 0;
            int count= inputStream.read(buffer);
            while (-1 != (n=inputStream.read(buffer))) {
                output.write(buffer, 0, n);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }


    public List<String> readResource(Integer bookId){
        String filePath=bookDao.queryBookById(bookId).getBookFilePath();
        filePath=filePath.replace(' ','/');
        InputStream inputStream = null;
        int pageCount=1500;
        String s_gbk="";
        String s_utf8="";
        String s_iso88591="";
        //将全部资源划分成块，一次存取一定的数量，前台读取完后在到后台读取
        //读到第几块
        int startBlock=1;
        //一块有多少数量
        int readCount=20;//一次读取的数量
        byte[] buffer = new byte[pageCount];
        String[] buffer1 = new String[readCount];
        List<String> bufferStr=new ArrayList<>() ;
        try {
            inputStream = new FileInputStream(filePath);
            int n = 0;
            while (-1 != (n=inputStream.read(buffer))) {
                s_gbk = new String(buffer,"GBK");
                s_utf8 = new String(buffer,"UTF-8");
                s_iso88591 = new String(buffer,"ISO8859-1");
                bufferStr.add(s_utf8);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bufferStr;
    }

    //region search
    @Override
    public List<Book> searchBook(String searchType,String searchInput) {
        searchInput="%"+searchInput+"%";
        if(searchType.equals("书名")){
            searchType="name";
        }else{
            searchType="author";
        }
        return bookDao.searchBook(searchType,searchInput);
    }
    //endregion
}
