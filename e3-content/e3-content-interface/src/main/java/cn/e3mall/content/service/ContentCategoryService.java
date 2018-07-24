package cn.e3mall.content.service;

import cn.e3mall.common.pojo.EasyUITreeNode;

import java.util.List;

public interface ContentCategoryService {

    List<EasyUITreeNode> getContentCatList(long parentId);
}
