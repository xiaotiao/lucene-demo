package cn.jiguang.searcher;

import cn.jiguang.common.Base;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static cn.jiguang.common.Constant.*;

/**
 * 常见的Lucene内置Query查询器
 */
public class LuceneInternalQueryTest extends Base {

    IndexReader reader;
    IndexSearcher searcher;

    @Before
    public void setUp() throws IOException {
        reader = IndexReader.open(directory);
        searcher = new IndexSearcher(reader);
    }

    /**
     * 词项搜索
     * @throws IOException
     */
    @Test
    public void testTermQuery() throws IOException {
        Query query = new TermQuery(new Term("isbn","9781935182023"));
        TopDocs topDocs = searcher.search(query, 10);
        printDocumentList(topDocs);
    }


    /**
     *  按照字典编排顺序存储项
     *  只能对字符串进行范围搜索
     * @throws Exception
     */
    @Test
    public void testTermRangeQuery() throws Exception {
        TermRangeQuery query = new TermRangeQuery("title2","d","j",true,true);
        TopDocs topDocs = searcher.search(query, 10);
        printDocumentList(topDocs);
    }


    /**
     *  对数字进行范围搜索
     * @throws Exception
     */
    @Test
    public void testNumericRangeQuery() throws Exception{
        NumericRangeQuery query = NumericRangeQuery.newIntRange("pubmonth",200605,200609,true,true);
        TopDocs topDocs = searcher.search(query, 10);
        printDocumentList(topDocs);
    }


    /**
     * 前缀匹配
     * @throws Exception
     */
    @Test
    public void testPrefix() throws Exception {
        Term term = new Term(CATEGORY,"/technology/computers/programming");
        PrefixQuery query = new PrefixQuery(term);
        TopDocs prefixMatches = searcher.search(query, 10);
        TopDocs termMatches = searcher.search(new TermQuery(term), 10);

        System.out.println("prefixMatches hitsTotal = "+prefixMatches.totalHits);
        System.out.println("termMatches hitsTotal = "+termMatches.totalHits);
    }


    /**
     *  BooleanQuery and 查询
     * @throws Exception
     */
    @Test
    public void testAnd() throws Exception {
        TermQuery searchingBooks = new TermQuery(new Term(SUBJECT,"search"));
        Query books2010 = NumericRangeQuery.newIntRange("pubmonth",201001,201012,true,true);

        BooleanQuery searchingBooks2010  = new BooleanQuery();
        searchingBooks2010.add(searchingBooks,BooleanClause.Occur.MUST);
        searchingBooks2010.add(books2010,BooleanClause.Occur.MUST);

        TopDocs topDocs = searcher.search(searchingBooks2010, 10);

        printDocumentList(topDocs);
    }


    @Test
    public void testPhraseQuery() throws Exception {
        Directory dir = new RAMDirectory();
        IndexWriter writer = new IndexWriter(dir,new WhitespaceAnalyzer(),IndexWriter.MaxFieldLength.UNLIMITED);
        Document doc = new Document();
        doc.add(new Field("field","the quick brown fox jumped over the lazy dog",Field.Store.YES,Field.Index.ANALYZED));

        writer.addDocument(doc);
        writer.close();

        searcher = new IndexSearcher(dir);
        String[] phrase = new String[]{"quick","fox"};
        int slop = 1;

        PhraseQuery query = new PhraseQuery();
        query.setSlop(slop);
        Arrays.asList(phrase).forEach(p -> query.add(new Term("field",p)));
        TopDocs topDocs = searcher.search(query, 10);

        System.out.println("totalHits: "+topDocs.totalHits);
    }

    private void printDocumentList(TopDocs topDocs){
        try{
            System.out.println("totalHits = "+topDocs.totalHits);
            for (ScoreDoc sDoc : topDocs.scoreDocs){
                Document doc = searcher.doc(sDoc.doc);
                System.out.println("----------------");
                System.out.printf("%s: %s\n",ISBN,doc.get(ISBN));
                System.out.printf("%s: %s\n",CATEGORY,doc.get(CATEGORY));
                System.out.printf("%s: %s\n",TITLE,doc.get(TITLE));
                System.out.printf("%s: %s\n",TITLE2,doc.get(TITLE2));
                System.out.printf("%s: %s\n",AUTHOR,doc.get(AUTHOR));
                System.out.printf("%s: %s\n",URL,doc.get(URL));
                System.out.printf("%s: %s\n",SUBJECT,doc.get(SUBJECT));
                System.out.printf("%s: %s\n",PUBMONTH,doc.get(PUBMONTH));
                System.out.printf("%s: %s\n",CONTENTS,doc.get(CONTENTS));
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }

    }



    @After
    public void destory() throws IOException {
        searcher.close();
        directory.close();
    }
}
