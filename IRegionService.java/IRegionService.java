package $dataInfo.iServicePackageName;

import com.ytx.common.web.Page;
import ${dataInfo.domainPackageName}.Region;

import java.util.List;

/**
* @description: 地区管理，人员分地区
* @author: mayn
* @createTime: 2020-01-11 14:50:01
*/
public interface IRegionService {

    /**
    * 新增
    * @param region
    * @return
    */
    int addRegion(Region region);

    /**
    * 删除
    * @param region 对象中包含Id
    * @return
    */
    void deleteRegionById(Region region);

    /***
    * 修改
    * @param region
    * @return
    */
    void updateRegion(Region region);

    /**
    * 根据ID查找
    * @param region
    * @return
    */
    Region findRegion(Region region);

    /***
    * 分页查找
    * @param page
    * @param region 参数数据
    * @return region list
    */
    List<Region> listRegion(Region region, Page page);

    /***
    * count总数
    * @return
    */
    int countRegion(Region region);

}
