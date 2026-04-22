package cn.qihangerp.sys.controller;

import cn.qihangerp.common.AjaxResult;
import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.TableDataInfo;
import cn.qihangerp.model.entity.SysOpenAuth;
import cn.qihangerp.security.common.BaseController;
import cn.qihangerp.service.SysOpenAuthService;
import lombok.AllArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Date;

/**
 * 店铺Controller
 * 
 * @author qihang
 * @date 2023-12-31
 */
@AllArgsConstructor
@RestController
@RequestMapping("/openAuth")
public class SysOpenAuthController extends BaseController {
    private final SysOpenAuthService sysOpenAuthService;
    /**
     * 查询列表
     */
    @GetMapping("/list")
    public TableDataInfo list(SysOpenAuth bo, PageQuery pageQuery) {
        PageResult<SysOpenAuth> list = sysOpenAuthService.queryPageList(bo,pageQuery);
        return getDataTable(list);
    }


    /**
     * 获取详细信息
     */
    @GetMapping(value = "/detail/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(sysOpenAuthService.getById(id));
    }

    /**
     * 新增
     */
    @PostMapping("/add")
    public AjaxResult add(@RequestBody SysOpenAuth bo) {
        SysOpenAuth sysOpenAuth = sysOpenAuthService.queryByAppKey(bo.getAppKey());
        if(sysOpenAuth!=null){
            return AjaxResult.error("appkey已存在");
        }
        if(StringUtils.hasText(bo.getWhiteList())){
            bo.setWhiteList(bo.getWhiteList().replace("\r\n",",").replace("\n",","));
        }
        bo.setCreateTime(new Date());
        bo.setCreateBy(getUsername());
        return toAjax(sysOpenAuthService.save(bo));
    }

    /**
     * 修改
     */
    @PutMapping("/edit")
    public AjaxResult edit(@RequestBody SysOpenAuth bo) {
        if(bo.getId()==null) return AjaxResult.error("缺少参数：id");

        if(StringUtils.hasText(bo.getWhiteList())){
            bo.setWhiteList(bo.getWhiteList().replace("\r\n",",").replace("\n",","));
        }
        bo.setUpdateTime(new Date());
        bo.setUpdateBy(getUsername());
        sysOpenAuthService.updateById(bo);
        return toAjax(1);
    }

    /**
     * 删除
     */
    @DeleteMapping("/del/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        sysOpenAuthService.removeByIds(Arrays.asList(ids));
        return AjaxResult.success();
    }

}
