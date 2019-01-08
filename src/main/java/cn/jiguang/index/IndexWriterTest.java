package cn.jiguang.index;

import org.apache.lucene.document.Document;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class IndexWriterTest {



    private static Document createDocument(String rootDir, File file) throws Exception {
        Properties props = new Properties();
        props.load(new FileInputStream(file));

        Document doc = new Document();

        String category = file.getParent().substring(rootDir.length());
        category = category.replace(File.separatorChar, '/');
        String isbn = props.getProperty("isbn");
        String title = props.getProperty("title");
        String author = props.getProperty("author");
        String url = props.getProperty("url");
        String subject = props.getProperty("subject");
        String pubmonth = props.getProperty("pubmonth");

        System.out.println(title + "\n" + author + "\n" + subject + "\n" + pubmonth + "\n" + category + "\n---------");

        return null;
    }


}
