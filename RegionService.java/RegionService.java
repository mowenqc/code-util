package $servicePackageName;

import com.ytx.common.web.Page;
import $dataInfo.daoPackageName.RegionMapper;
import ${dataInfo.domainPackageName}.Region;
import ${dataInfo.iServicePackageName}.IRegionService;
import org.springframework.stereotype.Service;
import org.apache.ibatis.session.RowBounds;

import javax.annotation.Resource;
import java.util.List;
import java.util.Date;

/**
* @description: 地区管理，人员分地区
* @author: mayn
* @createTime: 2020-01-11 14:50:01
*/

@Service
public class RegionService implements IRegionService {
    @Resource
    private RegionMapper regionMapper;

    /**
    * 新增
    * @param region
    * @return
    */
    @Override
    public int addRegion(Region region) {
        return regionMapper.addRegion(region);
    }

    /**
    * 删除
    * @param region
    * @return
    */
    @Override
    public void deleteRegionById(Region region) {
        regionMapper.deleteRegion(region);
    }

    /***
    * 修改
    * @param region
    * @return
    */
    @Override
    public void updateRegion(Region region) {
        regionMapper.updateRegion(region);
    }

    /**
    * 根据ID查找
    * @param region
    * @return
    */
    @Override
    public Region findRegion(Region region) {
        return regionMapper.findRegion(region);
    }

    /***
    * 分页查找
    * @param page
    * @return
    */
    @Override
    public List<Region> listRegion(Region region, Page page) {
        return regionMapper.listRegion(region, page);
    }

    /***
    * count总数
    * @return
    */
    @Override
    public int countRegion(Region region) {
        return regionMapper.countRegion(region);
    }
}
