package cn.e3mall.controller;

import cn.e3mall.common.pojo.EasyUITreeNode;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.content.service.ContentCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class ContentCatController {

    @Autowired
    private ContentCategoryService contentCategoryService;

    @RequestMapping("/content/category/list")
    @ResponseBody
    public List<EasyUITreeNode> getContentCatList(
            @RequestParam(name = "id", defaultValue = "0") Long parentId) {
        List<EasyUITreeNode> list = contentCategoryService.getContentCatList(parentId);
        return list;
    }

    @RequestMapping(value = "/content/category/create", method = RequestMethod.POST)
    @ResponseBody
    public E3Result addContentCategory(Long parentId, String name) {
        E3Result e3Result = contentCategoryService.addContentCategory(parentId, name);
        return e3Result;
    }

    @RequestMapping(value = "/content/category/update", method = RequestMethod.POST)
    @ResponseBody
    public E3Result updateContentCategory(Long id, String name) {
        E3Result e3Result = contentCategoryService.updateContentcategory(id, name);
        return e3Result;
    }

    @RequestMapping(value = "/content/category/delete/{id}")
    @ResponseBody
    public E3Result deleteContentCategory(@PathVariable Long id) {
        E3Result e3Result = contentCategoryService.deleteContentcategory(id);
        return e3Result;
    }
}
