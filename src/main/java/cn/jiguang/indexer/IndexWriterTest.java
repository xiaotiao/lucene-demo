package cn.jiguang.indexer;

import cn.jiguang.common.Base;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.io.File;
import java.io.FileInputStream;
import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.Properties;

public class IndexWriterTest extends Base {

    IndexWriter writer;

    @Before
    public void setUp() throws Exception {
        writer = new IndexWriter(directory, new StandardAnalyzer(Version.LUCENE_30), true,
                IndexWriter.MaxFieldLength.UNLIMITED);
    }

    @Test
    public void indexDocument() throws Exception {
        // 使用多文件索引结构
//        writer.setUseCompoundFile(false);
        Collection<File> fileCollection = FileUtils.listFiles(dataDir, new String[]{"properties"}, true);
        for(File file: fileCollection){
            Document doc = createDocument(dataDir.getAbsolutePath(), file);
            System.out.println("indexer file: "+file.getAbsolutePath());
            writer.addDocument(doc);
        }
        int numDocs = writer.numDocs();
        System.out.println("numDocs: "+numDocs);
    }

    /**
     * 创建文档
     * @param rootDir
     * @param file
     * @return
     * @throws Exception
     */
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

        doc.add(new Field("isbn",
                isbn,
                Field.Store.YES,
                Field.Index.NOT_ANALYZED));
        doc.add(new Field("category",
                category,
                Field.Store.YES,
                Field.Index.NOT_ANALYZED));
        doc.add(new Field("title",
                title,
                Field.Store.YES,
                Field.Index.ANALYZED,
                Field.TermVector.WITH_POSITIONS_OFFSETS));
        doc.add(new Field("title2",
                title.toLowerCase(),
                Field.Store.YES,
                Field.Index.NOT_ANALYZED_NO_NORMS,
                Field.TermVector.WITH_POSITIONS_OFFSETS));

        // split multiple authors into unique field instances
        String[] authors = author.split(",");
        for (String a : authors) {
            doc.add(new Field("author",
                    a,
                    Field.Store.YES,
                    Field.Index.NOT_ANALYZED,
                    Field.TermVector.WITH_POSITIONS_OFFSETS));
        }

        doc.add(new Field("url",
                url,
                Field.Store.YES,
                Field.Index.NOT_ANALYZED_NO_NORMS));
        doc.add(new Field("subject",
                subject,
                Field.Store.YES,
                Field.Index.ANALYZED,
                Field.TermVector.WITH_POSITIONS_OFFSETS));

        doc.add(new NumericField("pubmonth",
                Field.Store.YES,
                true).setIntValue(Integer.parseInt(pubmonth)));

        Date d;
        try {
            d = DateTools.stringToDate(pubmonth);
        } catch (ParseException pe) {
            throw new RuntimeException(pe);
        }
        doc.add(new NumericField("pubmonthAsDay")
                .setIntValue((int) (d.getTime()/(1000*3600*24))));

        for(String text : new String[] {title, subject, author, category}) {
            doc.add(new Field("contents", text,
                    Field.Store.YES, Field.Index.ANALYZED,
                    Field.TermVector.WITH_POSITIONS_OFFSETS));
        }

        return doc;
    }

    @After
    public void destory() throws Exception {
        writer.close();
        directory.close();
    }


}
