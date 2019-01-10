package cn.jiguang.common;



import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.util.Arrays;

import static cn.jiguang.common.Constant.*;
import static cn.jiguang.common.Constant.CONTENTS;
import static cn.jiguang.common.Constant.PUBMONTH;

public class Base {

    protected File indexDir;
    protected File dataDir;
    protected Directory directory;

    public Base() {
        try{
            indexDir = new File(System.getProperty("user.dir")+"/doc/index_dir");
            dataDir = new File(System.getProperty("user.dir")+"/doc/data_dir");
            directory = FSDirectory.open(indexDir);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public Directory getBookIndexDirectory(){
        return directory;
    }

    public void printDocumentList(IndexSearcher searcher, TopDocs topDocs){
        try{
            System.out.println("totalHits = "+topDocs.totalHits);
            for (ScoreDoc sDoc : topDocs.scoreDocs){
                Document doc = searcher.doc(sDoc.doc);
                System.out.println("----------------");
                System.out.printf("%s: %s\n",ISBN,doc.get(ISBN));
                System.out.printf("%s: %s\n",CATEGORY,doc.get(CATEGORY));
                System.out.printf("%s: %s\n",TITLE,doc.get(TITLE));
                System.out.printf("%s: %s\n",TITLE2,doc.get(TITLE2));
                System.out.printf("%s: %s\n",AUTHOR,Arrays.toString(Arrays.stream(doc.getFields(AUTHOR)).map(Field::stringValue).toArray()));
                System.out.printf("%s: %s\n",URL,doc.get(URL));
                System.out.printf("%s: %s\n",SUBJECT,doc.get(SUBJECT));
                System.out.printf("%s: %s\n",PUBMONTH,doc.get(PUBMONTH));
                System.out.printf("%s: %s\n",CONTENTS,Arrays.toString(Arrays.stream(doc.getFields(CONTENTS)).map(Field::stringValue).toArray()));
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }

    }
}
