package cn.e3mall.service.impl;

import cn.e3mall.common.jedis.JedisClient;
import cn.e3mall.common.pojo.EasyUIDataGridResult;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.IDUtils;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.mapper.TbItemDescMapper;
import cn.e3mall.mapper.TbItemMapper;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbItemDesc;
import cn.e3mall.pojo.TbItemExample;
import cn.e3mall.service.ItemService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.jms.*;
import java.util.Date;
import java.util.List;
@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private TbItemDescMapper itemDescMapper;
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private JedisClient jedisClient;
    @Resource
    private Destination topicDestination;

    @Value("${REDIS_ITEM_PRE}")
    private String REDIS_ITEM_PRE;
    @Value("${ITEM_CACHE_EXPIRE}")
    private Integer ITEM_CACHE_EXPIRE;
    @Override
    public TbItem getTbItemById(long itemId) {
        try {
            String json = jedisClient.get(REDIS_ITEM_PRE + ":" + itemId + ":BASE");
            if(StringUtils.isNotBlank(json)) {
                TbItem tbItem = JsonUtils.jsonToPojo(json, TbItem.class);
                return tbItem;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        TbItemExample tbItemExample = new TbItemExample();
        TbItemExample.Criteria criteria = tbItemExample.createCriteria();
        criteria.andIdEqualTo(itemId);
        List<TbItem> list = itemMapper.selectByExample(tbItemExample);
        if(list!=null&&list.size()>0){
            //把结果添加到缓存
            try {
                jedisClient.set(REDIS_ITEM_PRE + ":" + itemId + ":BASE", JsonUtils.objectToJson(list.get(0)));
                //设置过期时间
                jedisClient.expire(REDIS_ITEM_PRE + ":" + itemId + ":BASE", ITEM_CACHE_EXPIRE);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return list.get(0);
        }
        return null;
    }

    @Override
    public EasyUIDataGridResult getItemList(int page, int rows) {

        PageHelper.startPage(page,rows);
        TbItemExample tbItemExample = new TbItemExample();
        List<TbItem> tbItems = itemMapper.selectByExample(tbItemExample);
        EasyUIDataGridResult easyUIDataGridResult = new EasyUIDataGridResult();
        easyUIDataGridResult.setRows(tbItems);
        PageInfo<TbItem> pageInfo = new PageInfo<>(tbItems);
        easyUIDataGridResult.setTotal(pageInfo.getTotal());

        return easyUIDataGridResult;
    }

    @Override
    public E3Result addItem(TbItem item, String desc) {
        long itemId = IDUtils.genItemId();
        item.setId(itemId);
        //1 正常  2 下架  3 删除
        item.setStatus((byte) 1);
        item.setCreated(new Date());
        item.setUpdated(new Date());
        itemMapper.insert(item);
        TbItemDesc itemDesc = new TbItemDesc();
        itemDesc.setItemId(itemId);
        itemDesc.setCreated(new Date());
        itemDesc.setUpdated(new Date());
        itemDesc.setItemDesc(desc);
        itemDescMapper.insert(itemDesc);
        jmsTemplate.send(topicDestination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                TextMessage textMessage = session.createTextMessage(itemId + "");
                return textMessage;
            }
        });
        return E3Result.ok();
    }

    @Override
    public E3Result updateItem(TbItem item, String desc) {
        itemMapper.updateByPrimaryKeySelective(item);
        TbItemDesc itemDesc = itemDescMapper.selectByPrimaryKey(item.getId());
        itemDesc.setItemDesc(desc);
        itemDescMapper.updateByPrimaryKeySelective(itemDesc);
        return E3Result.ok();
    }

    @Override
    public E3Result deleteItems(String ids) {

        String[] strIds = ids.split(",");
        for (String sid:strIds) {
            long id = Long.parseLong(sid);
            itemMapper.deleteByPrimaryKey(id);
        }
        return E3Result.ok();
    }

    @Override
    public TbItemDesc getItemDesc(long id) {
        try {
            String json = jedisClient.get(REDIS_ITEM_PRE + ":" + id + ":DESC");
            if(StringUtils.isNotBlank(json)) {
                TbItemDesc itemDesc = JsonUtils.jsonToPojo(json, TbItemDesc.class);
                return itemDesc;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        TbItemDesc itemDesc = itemDescMapper.selectByPrimaryKey(id);
        try {
            jedisClient.set(REDIS_ITEM_PRE + ":" + id + ":DESC", JsonUtils.objectToJson(itemDesc));
            //设置过期时间
            jedisClient.expire(REDIS_ITEM_PRE + ":" + id + ":DESC", ITEM_CACHE_EXPIRE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return itemDesc;
    }
}
