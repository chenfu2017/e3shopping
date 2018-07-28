package cn.e3mall.cart.service.impl;

import cn.e3mall.cart.service.CartService;
import cn.e3mall.common.jedis.JedisClient;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.mapper.TbItemMapper;
import cn.e3mall.pojo.TbItem;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private JedisClient jedisClient;
    @Override
    public E3Result addCart(long userId, long itemId, int num) {
        String key = "CART:"+userId;
        String field = itemId + "";
        Boolean hexists = jedisClient.hexists(key, field);
        if (hexists) {
            String hget = jedisClient.hget(key, field);
            TbItem tbItem = JsonUtils.jsonToPojo(hget, TbItem.class);
            tbItem.setNum(tbItem.getNum() + num);
            jedisClient.hset(key, field, JsonUtils.objectToJson(tbItem));
            return E3Result.ok();
        }
        TbItem tbItem = itemMapper.selectByPrimaryKey(itemId);
        tbItem.setNum(num);
        String image = tbItem.getImage();
        if (StringUtils.isNotBlank(image)) {
            tbItem.setImage(image.split(",")[0]);
        }
        jedisClient.hset(key, field, JsonUtils.objectToJson(tbItem));
        return E3Result.ok();
    }

    @Override
    public E3Result mergeCart(long userId, List<TbItem> list) {
        for (TbItem t :list) {
            addCart(userId, t.getId(), t.getNum());
        }
        return E3Result.ok();
    }

    @Override
    public List<TbItem> getCartList(long userId) {
        List<String> stringList = jedisClient.hvals("CART:" + userId);
        List<TbItem> items = new ArrayList<>();
        for (String s : stringList) {
            TbItem tbItem = JsonUtils.jsonToPojo(s, TbItem.class);
            items.add(tbItem);
        }
        return items;
    }

    @Override
    public E3Result updateCart(long userId, long itemId, int num) {
        String key = "CART:"+userId;
        String field = itemId + "";
        String s = jedisClient.hget(key, field);
        TbItem tbItem = JsonUtils.jsonToPojo(s, TbItem.class);
        tbItem.setNum(num);
        jedisClient.hset(key, field, JsonUtils.objectToJson(tbItem));
        return E3Result.ok();
    }

    @Override
    public E3Result deleteCartItem(long userId, long itemId) {
        String key = "CART:"+userId;
        String field = itemId + "";
        jedisClient.hdel(key, field);
        return E3Result.ok();
    }
}
