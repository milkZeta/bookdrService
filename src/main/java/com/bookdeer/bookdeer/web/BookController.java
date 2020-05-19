package com.bookdeer.bookdeer.web;

import com.bookdeer.bookdeer.entity.Book;
import com.bookdeer.bookdeer.entity.WeChatUserInfo;
import com.bookdeer.bookdeer.service.BookService;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/superadmin")
public class BookController {
    @Autowired
    private BookService bookService;

    @RequestMapping(value = "/listbook", method = RequestMethod.GET)
    private Map<String,Object> listBook(){
        Map<String,Object> modelMap=new HashMap<String,Object>();
        List<Book> list=bookService.queryBook();
        list=bookService.convertBlobList(list);
        modelMap.put("bookList",list);
        return modelMap;
    }

    /**
     * 通过书籍Id获取书籍信息
     *
     * @return
     */
    @RequestMapping(value = "/getbookbyid", method = RequestMethod.GET)
    private Map<String, Object> getBookById(Integer bookId) {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        // 获取书籍信息
        Book book = bookService.queryBookMaxId();
        modelMap.put("book", book);
        return modelMap;
    }

    /**
     * 添加书籍信息()
     *
     * @param bookStr
     * @param request
     * @return
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    @RequestMapping(value = "/addbook")
//    private Map<String, Object> addBook(HttpServletRequest request, @RequestParam(value = "file", required = false) MultipartFile file)
    private  Integer  addBook(HttpServletRequest request, @RequestParam(value = "file", required = false) MultipartFile file)
            throws JsonParseException, JsonMappingException, IOException {
//        Map<String, Object> modelMap = new HashMap<String, Object>();
        String book_name = request.getParameter("book_name");
        String book_auth = request.getParameter("book_auth");
        String book_publish = request.getParameter("book_publish");
        String book_desc = request.getParameter("book_desc");
        String owner = request.getParameter("owner");
        boolean flag=false;
        Integer bookId=-1;
        Book book=new Book();
        book.setBookName(book_name);
        book.setBookAuth(book_auth);
        book.setBookPublish(book_publish);
        book.setBookDesc(book_desc);
        book.setOwner(owner);
        book.setCreateBy(owner);
        book.setCategory("P");

        if(!file.isEmpty()) {
            String fileName = file.getOriginalFilename();
            String path = null;
            String type = null;
            type = fileName.indexOf(".") != -1 ? fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()) : null;
            if (type != null) {
                if ("MOBI".equals(type.toUpperCase())||"TXT".equals(type.toUpperCase())
                        ||"PDF".equals(type.toUpperCase())||"XLS".equals(type.toUpperCase())
                        ||"XLSX".equals(type.toUpperCase())) {
                    String trueFileName = String.valueOf(System.currentTimeMillis()) + fileName;
                    String realPath = request.getSession().getServletContext().getRealPath("/");
                    path = realPath + "/" + trueFileName;
//                    path=path.replace('/',' ');
                    if (!new File(path).getParentFile().exists()) {
                        new File(path).getParentFile().mkdirs();
                    }
                    try {
                        file.transferTo(new File(path)); // 保存文件
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    flag = bookService.insertBookDetails(book, path);
                }


                }
            }
        // 添加书籍信息
        if(flag) {
            bookId = bookService.addBook(book);
        }
        else return null;
        return bookId;
    }

    //region
    /**
     * 添加书籍信息()
     *
     * @param bookStr
     * @param request
     * @return
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    /*因为直接存储到服务器路径下比较慢
    @RequestMapping(value = "/addbook")
//    private Map<String, Object> addBook(HttpServletRequest request, @RequestParam(value = "file", required = false) MultipartFile file)
    private Integer addBook(HttpServletRequest request, @RequestParam(value = "file", required = false) MultipartFile file)
            throws JsonParseException, JsonMappingException, IOException {
//        Map<String, Object> modelMap = new HashMap<String, Object>();
        String book_name = request.getParameter("book_name");
        String book_auth = request.getParameter("book_auth");
        String book_publish = request.getParameter("book_publish");
        String book_desc = request.getParameter("book_desc");
        String owner = request.getParameter("owner");
        Book book=new Book();
        book.setBookName(book_name);
        book.setBookAuth(book_auth);
        book.setBookPublish(book_publish);
        book.setBookDesc(book_desc);
        book.setOwner(owner);
        book.setCreateBy(owner);
        book.setCategory("P");

        if(!file.isEmpty()) {
            String fileName = file.getOriginalFilename();
            String path = null;
            String type = null;
            type = fileName.indexOf(".") != -1 ? fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()) : null;
            if (type != null) {
                if ("MOBI".equals(type.toUpperCase())||"TXT".equals(type.toUpperCase())
                        ||"PDF".equals(type.toUpperCase())||"XLS".equals(type.toUpperCase())
                        ||"XLSX".equals(type.toUpperCase())) {
                    // 项目在容器中实际发布运行的根路径
//                    String realPath =request.getSession().getServletContext().getRealPath("/");
                    String realPath = "/home/bkd_data/";
                    // 自定义的文件名称
                    String trueFileName = String.valueOf(System.currentTimeMillis()) + fileName;
                    // 设置存放图片文件的路径
                    path = realPath +  trueFileName;
                    file.transferTo(new File(path));
                    path=path.replace('/',' ');
                }
                if(path!=null){
                    book.setBookFilePath(path);
                }

            }
        }
        // 添加书籍信息
        Integer bookId= bookService.addBook(book);
        return bookId;
    }
     */
    //endregion

    @RequestMapping(value = "/insertBookCover")
//    private Map<String, Object> insertCover(HttpServletRequest request, @RequestParam(value = "file", required = false) MultipartFile file)
    private Map<String, Object> insertBookCover(HttpServletRequest request, @RequestParam(value = "image", required = false) MultipartFile image)
            throws JsonParseException, JsonMappingException, IOException {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        String path = "";
        String fileName = image.getOriginalFilename();
        String trueFileName = String.valueOf(System.currentTimeMillis()) + fileName;
        String realPath = request.getSession().getServletContext().getRealPath("/");
        path = realPath + "/" + trueFileName;
        image.transferTo(new File(path));
        if(request.getParameter("book_id")!=null) {
            Integer book_id = Integer.valueOf(request.getParameter("book_id"));
            Book book = new Book();
            book.setBookId(book_id);
            // 添加书籍信息
            modelMap.put("success", bookService.insertCover(book, path));
        }
        else{
            modelMap.put("fail", "bookId为空！");
        }
        return modelMap;
    }

    /**
     * 修改书籍信息，主要修改名字
     *
     * @param bookStr
     * @param request
     * @return
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    @RequestMapping(value = "/modifybook", method = RequestMethod.POST)
    private Map<String, Object> modifyBook(@RequestBody Book book)
            throws JsonParseException, JsonMappingException, IOException {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        // 修改书籍信息
        modelMap.put("success", bookService.updateBook(book));
        return modelMap;
    }

    @RequestMapping(value = "/removebook", method = RequestMethod.GET)
    private Map<String, Object> removeBook(Integer bookId) {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        // 修改书籍信息
        modelMap.put("success", bookService.deleteBook(bookId));
        return modelMap;
    }

    //region Mine
    @RequestMapping(value = "/queryMineBookList", method = RequestMethod.GET)
    private Map<String,List<Book>> queryMineBookList(HttpServletRequest request){
        Map<String,List<Book>> modelMap = new HashMap<String,List<Book>>();
        String owner = request.getParameter("owner");
        // 修改书籍信息
        modelMap=bookService.queryMineBookList(owner);
        return modelMap;
    }
    @RequestMapping(value = "/deleteMineBookList", method = RequestMethod.GET)
    private Map<String,List<Book>> deleteMineBookList(HttpServletRequest request){
        Map<String,List<Book>> modelMap = new HashMap<String,List<Book>>();
        String owner = request.getParameter("owner");
        String book_id = request.getParameter("book_id");
        // 修改书籍信息
        if(bookService.deleteMineBookList(owner,book_id))
        {
            modelMap=bookService.queryMineBookList(owner);
        }
        return modelMap;
    }
    @RequestMapping(value = "/shareResource", method = RequestMethod.GET)
    private Map<String, Object> shareResource(HttpServletRequest request){
        Map<String,Object> modelMap = new HashMap<String,Object>();
        String bookId = request.getParameter("bookId");
        // 修改书籍信息
        modelMap.put("success", bookService.changeCategory(bookId));
        return modelMap;
    }
    @RequestMapping(value = "/downloadResource", method = RequestMethod.GET)
    private byte[] downloadResource(String bookId){
        Integer book_id = Integer.valueOf(bookId);
        // 修改书籍信息
        return bookService.downloadResource(book_id);
    }

    @RequestMapping(value = "/readResource", method = RequestMethod.GET)
    private String readResource(Integer bookId){
        // 修改书籍信息
        String str="";
        try {
           str=bookService.getCLobData(bookId);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ;
        return str;
    }
    //endregion

    //region  search
    @RequestMapping(value = "/searchBook", method = RequestMethod.GET)
    private Map<String,Object> searchBook(HttpServletRequest request){
        String searchType = request.getParameter("searchType");
        String searchInput = request.getParameter("searchInput");
        Map<String,Object> modelMap=new HashMap<String,Object>();
        List<Book> list=bookService.searchBook(searchType,searchInput);
        list=bookService.convertBlobList(list);
        modelMap.put("bookList",list);
        return modelMap;
    }
    //endregion

    //region
    @RequestMapping(value="/getOpenId",method=RequestMethod.GET)
    private String getOpenId(String js_code) throws Exception {
        String APP_ID = "wx8d7b3920e3b568c3";//输入小程序appid
        String APP_SECRET = "bf9f52f8ac76fb940416a1fab4a32655";//输入小程序app_secret
        String reqUrl="https://api.weixin.qq.com/sns/jscode2session";
        String urlStr=new StringBuilder().append(reqUrl)
                .append("?appid="+ APP_ID)
                .append("&secret="+APP_SECRET)
                .append("&js_code="+js_code)
                .append("&grant_type=authorization_code")
                .toString();
        URL url = new URL(urlStr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.connect();
        int code = connection.getResponseCode();
        if (code == 404) {
            throw new Exception("认证无效，找不到此次认证的会话信息！");
        }
        if (code == 500) {
            throw new Exception("认证服务器发生内部错误！");
        }
        if (code != 200) {
            throw new Exception("发生其它错误，认证服务器返回 " + code);
        }
        InputStream is = connection.getInputStream();
        byte[] response = new byte[is.available()];
        is.read(response);
        is.close();
        if (response == null || response.length == 0) {
            throw new Exception("认证无效，找不到此次认证的会话信息！");
        }
        String userId = new String(response, "UTF-8");
        return userId;
    }
    //endregion
}
