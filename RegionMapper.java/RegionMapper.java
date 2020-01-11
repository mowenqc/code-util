package $dataInfo.daoPackageName;


import ${dataInfo.domainPackageName}.Region;
import org.springframework.stereotype.Repository;
import com.ytx.common.web.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds

import java.util.List;
/**
* @description: 地区管理，人员分地区
* @author: mayn
* @createTime: 2020-01-11 14:50:01
*/
@Repository
public interface RegionMapper{

    /**
    * 新增region -> gie_region
    * @param region
    * @return
    */
    int addRegion(Region region);

    /**
    * 删除region
    * @param region 对象中包含Id
    * @return
    */
    void deleteRegion(Region region);

    /***
    * 修改region -> gie_region
    * @param region
    * @return
    */
    void updateRegion(Region region);

    /**
    * 根据ID查找
    * @param region
    * @return region
    */
    Region findRegion(Region region);

    /***
    * 分页查找
    * @param page
    * @param region 参数数据
    * @return region list
    */
    List<Region> listRegion(@Param("obj")Region region,@Param("page") RowBounds page);

    /***
    * count总数
    *
    * @return
    */
    int countRegion(Region region);
}
