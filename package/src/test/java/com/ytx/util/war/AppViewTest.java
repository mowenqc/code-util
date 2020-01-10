package com.ytx.util.war;

import com.google.common.collect.Lists;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by  on 2018/4/17.
 */
public class AppViewTest {
    @Test
    public void batchModifyDomain() throws Exception {
        File f1 = getDomainFile("OrderGoodsComment.java");
        AppView appView = new AppView();
        appView.batchModifyDomain(Lists.newArrayList(f1));
    }

    private File getDomainFile(String fileName) throws Exception {
        File f = new File("D:\\ytx_workspace\\learning\\client\\src\\main\\java\\com\\ytx\\learning\\domain\\" + fileName);
        return f;


    }

}