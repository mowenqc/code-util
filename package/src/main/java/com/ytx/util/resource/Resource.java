package com.ytx.util.resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by chutao on 2017/8/10.
 */
public interface Resource{

    InputStream getInputStream() throws IOException;

    List<Resource> children() throws IOException;

}