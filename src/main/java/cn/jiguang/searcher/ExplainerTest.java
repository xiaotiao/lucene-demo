package cn.jiguang.searcher;

import cn.jiguang.common.Base;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import java.io.File;

/**
 * 类似于MySQL的explain命令，通过explain得到
 * Lucene具体的打分细节信息
 */
public class ExplainerTest extends Base {

    @Test
    public void testExplanation() throws Exception {
        IndexReader reader = IndexReader.open(directory);

        Query query = new TermQuery(new Term("subject","education"));

        IndexSearcher searcher = new IndexSearcher(reader);
        TopDocs topDocs = searcher.search(query, 10);

        for(ScoreDoc doc : topDocs.scoreDocs){
            Explanation explanation = searcher.explain(query,doc.doc);

            System.out.println("-----------");
            Document document = searcher.doc(doc.doc);
            System.out.println(document.get("title"));
            System.out.println(explanation.toString());
        }
        searcher.close();
        directory.close();
    }
}
