package cn.jiguang.searcher;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;

public class NearRealTimeTest {

    @Test
    public void testNearRealTime() throws Exception {
        Directory dir = new RAMDirectory();
        IndexWriter writer = new IndexWriter(dir,new StandardAnalyzer(Version.LUCENE_30),IndexWriter.MaxFieldLength.UNLIMITED);

        // 往索引中添加10个文档
        for(int i=0; i<10;i++){
            Document doc = new Document();
            doc.add(new Field("id",""+i,Field.Store.NO,Field.Index.NOT_ANALYZED_NO_NORMS));
            doc.add(new Field("text","aaa",Field.Store.NO,Field.Index.ANALYZED));

            writer.addDocument(doc);
        }

        // 通过reader来构造searcher,这儿使用writer.getReader使还未提交的段也能被搜索
        IndexReader reader = writer.getReader();
        IndexSearcher searcher = new IndexSearcher(reader);

        Query query = new TermQuery(new Term("text","aaa"));
        TopDocs topDocs = searcher.search(query, 1);

        System.out.println("topDocs = "+topDocs.totalHits);
        System.out.println("topDocs.ScoreDocs = "+topDocs.scoreDocs.length);

        writer.deleteDocuments(new Term("id","7"));

        // 新添加id=11的文档
        Document doc = new Document();
        doc.add(new Field("id","11",Field.Store.NO,Field.Index.NOT_ANALYZED_NO_NORMS));
        doc.add(new Field("text","bbb",Field.Store.NO,Field.Index.ANALYZED));

        writer.addDocument(doc);

        IndexReader newReader = reader.reopen();
        if(newReader != reader){
            reader.close();
            System.out.println("reader已关闭!");
            searcher = new IndexSearcher(newReader);
        }

        TopDocs hits = searcher.search(query, 10);
        System.out.println("hits="+hits.totalHits);

        query = new TermQuery(new Term("text","bbb"));
        hits = searcher.search(query,1);

        System.out.println("bbb hits = "+hits.totalHits);

        newReader.close();
        writer.close();

    }
}
