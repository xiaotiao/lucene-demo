package cn.jiguang.common;



import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;

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
}
