package cn.e3mall.content.service;

import cn.e3mall.common.pojo.EasyUIDataGridResult;
import cn.e3mall.common.pojo.EasyUITreeNode;
import cn.e3mall.common.utils.E3Result;

import java.util.List;

public interface ContentCategoryService {

    List<EasyUITreeNode> getContentCatList(long parentId);

    E3Result addContentCategory(long parentId, String name);

    E3Result updateContentcategory(long id,String name);

    E3Result deleteContentcategory(long id);

    EasyUIDataGridResult getContentCatList(int  page, int rows);
}
