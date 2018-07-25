package cn.e3mall.content.service.impl;

import cn.e3mall.common.jedis.JedisClient;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.content.service.ContentService;
import cn.e3mall.mapper.TbContentMapper;
import cn.e3mall.pojo.TbContent;
import cn.e3mall.pojo.TbContentExample;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ContentServiceImpl implements ContentService {

    @Autowired
    private TbContentMapper contentMapper;
    @Autowired
    private JedisClient jedisClient;

    @Value("${CONTENT_LIST}")
    private String CONTENT_LIST;

    @Override
    public E3Result addContent(TbContent content) {
        content.setCreated(new Date());
        content.setUpdated(new Date());
        contentMapper.insertSelective(content);
        return E3Result.ok();
    }

    @Override
    public List<TbContent> getContentListByCid(long cid) {
        try {
            String json = jedisClient.hget(CONTENT_LIST, cid + "");
            if (StringUtils.isNoneBlank(json)) {
                List<TbContent> list = JsonUtils.jsonToList(json, TbContent.class);
                return list;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        TbContentExample example = new TbContentExample();
        TbContentExample.Criteria criteria = example.createCriteria();
        criteria.andCategoryIdEqualTo(cid);
        List<TbContent> contentList = contentMapper.selectByExampleWithBLOBs(example);
        try {
            String s = JsonUtils.objectToJson(contentList);
            jedisClient.hset(CONTENT_LIST, cid+"", s);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contentList;
    }
}
