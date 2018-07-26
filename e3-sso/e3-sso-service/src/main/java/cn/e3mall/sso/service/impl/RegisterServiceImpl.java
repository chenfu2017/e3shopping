package cn.e3mall.sso.service.impl;

import cn.e3mall.common.utils.E3Result;
import cn.e3mall.mapper.TbUserMapper;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.pojo.TbUserExample;
import cn.e3mall.sso.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegisterServiceImpl implements RegisterService {

    @Autowired
    private TbUserMapper tbUserMapper;
    @Override
    public E3Result checkData(String param, int type) {
        TbUserExample userExample = new TbUserExample();
        TbUserExample.Criteria criteria = userExample.createCriteria();
        switch (type) {
            case 1: criteria.andUsernameEqualTo(param);break;
            case 2: criteria.andPhoneEqualTo(param);break;
            case 3: criteria.andEmailEqualTo(param);break;
            default:
                return E3Result.build(200, "参数异常");
        }
        List<TbUser> list = tbUserMapper.selectByExample(userExample);
        if (list!=null && list.size()>0) {
            return E3Result.ok(false);
        }
        return E3Result.ok(true);
    }
}
