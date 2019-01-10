package cn.jiguang.searcher;

import cn.jiguang.common.Base;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.util.Version;
import org.junit.After;
import org.junit.Test;
import java.io.IOException;
import static cn.jiguang.common.Constant.*;


public class QueryParserTest extends Base {

    IndexSearcher searcher;
    Analyzer analyzer;

    public QueryParserTest(){
        try{
            searcher = new IndexSearcher(directory);
            analyzer = new WhitespaceAnalyzer();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testQueryParser() throws Exception {
        QueryParser parser = new QueryParser(Version.LUCENE_30,"contents",new SimpleAnalyzer());
        Query query = parser.parse("+JUNIT +ANT -MOCK");
        TopDocs topDocs = searcher.search(query, 10);

        System.out.println("topHits = "+topDocs.totalHits);
        for(ScoreDoc doc : topDocs.scoreDocs){
            Document document = searcher.doc(doc.doc);
            System.out.println(document.get("subject"));
            System.out.println(document.get("contents"));
        }
    }

    @Test
    public void testToString() throws Exception {
        BooleanQuery query = new BooleanQuery();
        query.add(new FuzzyQuery(new Term("field","kountry")),BooleanClause.Occur.MUST);
        query.add(new TermQuery(new Term("title","western")),BooleanClause.Occur.SHOULD);

        System.out.println(query.toString("field"));
    }

    @Test
    public void testTermQuery() throws Exception {
        QueryParser parser = new QueryParser(Version.LUCENE_30,"subject",analyzer);

        Query query = parser.parse("computers");
        System.out.println("term: "+query);
    }

    @Test
    public void testTermRangeQuery() throws  Exception {
        // 包含在内
        Query query = new QueryParser(Version.LUCENE_30,"subject",analyzer).parse("title2:[Q TO V]");
        System.out.println("is TermRangeQuery: "+ Boolean.toString(query instanceof TermRangeQuery));

        Query query02 = new QueryParser(Version.LUCENE_30,"subject",analyzer).parse("title2:{Q TO V}");
        printDocumentList(searcher,searcher.search(query,10));
        printDocumentList(searcher,searcher.search(query02,10));
    }

    @After
    public void destory() throws IOException {
        searcher.close();
        directory.close();
    }
}
