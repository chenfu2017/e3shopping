package cn.e3mall.content.service.impl;

import cn.e3mall.common.pojo.EasyUIDataGridResult;
import cn.e3mall.common.pojo.EasyUITreeNode;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.content.service.ContentCategoryService;
import cn.e3mall.mapper.TbContentCategoryMapper;
import cn.e3mall.mapper.TbContentMapper;
import cn.e3mall.pojo.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Service
public class ContentCategoryServiceImpl implements ContentCategoryService {

    @Autowired
    private TbContentCategoryMapper contentCategoryMapper;
    @Autowired
    private TbContentMapper tbContentMapper;
    @Override
    public List<EasyUITreeNode> getContentCatList(long parentId) {
        TbContentCategoryExample example= new TbContentCategoryExample();
        TbContentCategoryExample.Criteria criteria = example.createCriteria();
        criteria.andParentIdEqualTo(parentId);
        List<TbContentCategory> categoryList = contentCategoryMapper.selectByExample(example);
        ArrayList<EasyUITreeNode> list = new ArrayList<>();
        for (TbContentCategory contentCategory:categoryList) {
            EasyUITreeNode treeNode = new EasyUITreeNode();
            treeNode.setId(contentCategory.getId());
            treeNode.setText(contentCategory.getName());
            treeNode.setState(contentCategory.getIsParent()?"closed":"open");
            list.add(treeNode);
        }
        return list;
    }

    @Override
    public E3Result addContentCategory(long parentId, String name) {
        TbContentCategory contentCategory = new TbContentCategory();
        contentCategory.setName(name);
        contentCategory.setParentId(parentId);
        //1 正常 2 删除
        contentCategory.setStatus(1);
        //排序方法默认为1
        contentCategory.setSortOrder(1);
        contentCategory.setIsParent(false);
        contentCategory.setCreated(new Date());
        contentCategory.setUpdated(new Date());
        contentCategoryMapper.insert(contentCategory);
        TbContentCategory parent = contentCategoryMapper.selectByPrimaryKey(parentId);
        if (!parent.getIsParent()) {
            parent.setIsParent(true);
        }
        contentCategoryMapper.updateByPrimaryKey(parent);

        return E3Result.ok(contentCategory);
    }

    @Override
    public E3Result updateContentcategory(long id,String name) {

        TbContentCategory contentCategory = contentCategoryMapper.selectByPrimaryKey(id);
        contentCategory.setName(name);
        contentCategoryMapper.updateByPrimaryKey(contentCategory);
        return E3Result.ok();
    }

    @Override
    public E3Result deleteContentcategory(long id) {
        TbContentCategory contentCategory = contentCategoryMapper.selectByPrimaryKey(id);
        if (contentCategory.getIsParent()) {
            System.out.println("contentCategory 是父节点");
            return new E3Result(304,"父节点不能删除",null);
        }
        long parentId = contentCategory.getParentId();
        contentCategoryMapper.deleteByPrimaryKey(id);
        TbContentCategoryExample example = new TbContentCategoryExample();
        TbContentCategoryExample.Criteria criteria = example.createCriteria();
        criteria.andParentIdEqualTo(parentId);
        int count = contentCategoryMapper.countByExample(example);
        if (count == 0) {
            TbContentCategory parent = contentCategoryMapper.selectByPrimaryKey(parentId);
            parent.setIsParent(false);
            contentCategoryMapper.updateByPrimaryKey(parent);
        }
        return E3Result.ok();
    }

    @Override
    public EasyUIDataGridResult getContentCatList(int page, int rows) {
        PageHelper.startPage(page,rows);
        TbItemExample tbItemExample = new TbItemExample();
        TbContentExample example = new TbContentExample();
        List<TbContent> tbContents = tbContentMapper.selectByExample(example);
        EasyUIDataGridResult easyUIDataGridResult = new EasyUIDataGridResult();
        easyUIDataGridResult.setRows(tbContents);
        PageInfo<TbContent> pageInfo = new PageInfo<>(tbContents);
        easyUIDataGridResult.setTotal(pageInfo.getTotal());

        return easyUIDataGridResult;
    }
}
