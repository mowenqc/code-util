package $dataInfo.controllerPackageName;

import com.ytx.common.web.Page;
import com.ytx.common.web.BaseController;
import ${dataInfo.iServicePackageName}.IRegionService;
import ${dataInfo.domainPackageName}.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Date;

/**
* @description: 地区管理，人员分地区
* @author: mayn
* @createTime: 2020-01-11 14:50:01
*/
@Controller
@RequestMapping("region")
public class RegionController extends BaseController{
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private IRegionService regionService;

    @RequestMapping("index.html")
    public String index(){
        return "region/index";
    }

    /**
     * 新增Region -> gie_region
     * @param region
     * @return
     */
    @RequestMapping("addRegion.html")
    @ResponseBody
    public String addRegion(Region region){
        try {
            regionService.addRegion(region);
            return buildSuccessResponse();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return this.buildErrorResponse(e.getMessage());
        }
    }
    /**
     * 删除 Region -> gie_region
     * @return
     */
    @RequestMapping("deleteRegionById.html")
    @ResponseBody
    public String deleteRegionById(Region region){
        try {
            regionService.deleteRegionById(region);
            return buildSuccessResponse();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return this.buildErrorResponse(e.getMessage());
        }
    }

    /***
     * 修改 Region -> gie_region
     * @param region
     * @return
     */
    @RequestMapping("updateRegion.html")
    @ResponseBody
    public String updateRegion(Region region){
        try {
            regionService.updateRegion(region);
            return buildSuccessResponse();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return this.buildErrorResponse(e.getMessage());
        }
    }

    /**
     * 根据ID查找
     * @return
     */
    @RequestMapping("findRegionById.html")
    @ResponseBody
    public String findRegionById(Region region){
        try {
            Region resultregion = regionService.findRegion(region);
            return buildSuccessResponse(resultregion);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return this.buildErrorResponse(e.getMessage());
        }
    }

    /**
     * 分页获取属性列表
     * @return
     */
    @RequestMapping("listRegion.html")
    @ResponseBody
    public String listRegion(Region region, Page page){
        try {
            List<Region> list = regionService.listRegion(region, page);
            int count = regionService.countRegion(region);
            return buildTableSuccessRespone(count, list);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return this.buildTableErrorRespone(e.getMessage());
        }
    }
}
