package cn.e3mall.sso.service.impl;

import cn.e3mall.common.jedis.JedisClient;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.sso.service.TokenService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TokenServiceImpl implements TokenService {

    @Autowired
    private JedisClient jedisClient;
    @Value("${SESSION_EXPIRE}")
    private Integer SESSION_EXPIRE;
    @Override
    public E3Result getUserByToken(String token) {

        String json = jedisClient.get("USER_INFO:" + token);
        if (StringUtils.isBlank(json)) {
            return E3Result.build(400, "用户信息已经过期");
        }
        jedisClient.expire("USER_INFO:" + token, SESSION_EXPIRE);
        TbUser tbUser = JsonUtils.jsonToPojo(json, TbUser.class);
        return E3Result.ok(tbUser);
    }
}
