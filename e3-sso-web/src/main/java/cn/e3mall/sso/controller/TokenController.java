package cn.e3mall.sso.controller;

import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.sso.service.TokenService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TokenController {

    @Autowired
    private TokenService tokenService;


/*    @RequestMapping(value = "/user/token/{token}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String getUserByToken(@PathVariable String token,String callback) {
        E3Result e3Result = tokenService.getUserByToken(token);
        if (StringUtils.isNotBlank(callback)) {
            return callback + "(" + JsonUtils.objectToJson(e3Result) + ")";
        }
        return JsonUtils.objectToJson(e3Result);
    }*/

    @RequestMapping(value = "/user/token/{token}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public Object getUserByToken(@PathVariable String token, String callback) {
        E3Result e3Result = tokenService.getUserByToken(token);
        if (StringUtils.isNotBlank(callback)) {
            MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(e3Result);
            mappingJacksonValue.setJsonpFunction(callback);
            return mappingJacksonValue;
        }
        return e3Result;
    }
}
