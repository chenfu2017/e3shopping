package cn.e3mall.cart.controller;

import cn.e3mall.cart.service.CartService;
import cn.e3mall.common.utils.CookieUtils;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.service.ItemService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CartController {

    @Autowired
    private ItemService itemService;
    @Autowired
    private CartService cartService;
    @Value("${COOKIE_CART_REPIRE}")
    private int COOKIE_CART_REPIRE;

    @RequestMapping("/cart/add/{itemId}")
    public String addCart(@PathVariable Long itemId, Integer num,
                          HttpServletRequest request, HttpServletResponse response) {
        TbUser user = (TbUser) request.getAttribute("user");
        if (user != null) {
            cartService.addCart(user.getId(), itemId, num);
            return "cartSuccess";
        }
        List<TbItem> list = getCartListFromCookie(request);
        boolean flag = false;
        for (TbItem item : list) {
            if (item.getId() == itemId.longValue()) {
                item.setNum(item.getNum() + num);
                flag = true;
                break;
            }
        }
        if (!flag) {
            TbItem tbItem = itemService.getTbItemById(itemId);
            tbItem.setNum(num);
            String image = tbItem.getImage();
            if (StringUtils.isNotBlank(image)) {
                tbItem.setImage(image.split(",")[0]);
            }
            list.add(tbItem);
        }
        CookieUtils.setCookie(request, response, "cart", JsonUtils.objectToJson(list), COOKIE_CART_REPIRE, true);

        return "cartSuccess";
    }

    private List<TbItem> getCartListFromCookie(HttpServletRequest request) {
        String cart = CookieUtils.getCookieValue(request, "cart", true);
        if (StringUtils.isBlank(cart)) {
            return new ArrayList<>();
        }
        List<TbItem> tbItems = JsonUtils.jsonToList(cart, TbItem.class);
        return tbItems;
    }

    @RequestMapping("/cart/cart")
    public String showCartList(HttpServletRequest request,HttpServletResponse response) {
        List<TbItem> list = getCartListFromCookie(request);
        TbUser user = (TbUser) request.getAttribute("user");
        if (user != null) {
            cartService.mergeCart(user.getId(), list);
            CookieUtils.deleteCookie(request,response,"cart");
            list=cartService.getCartList(user.getId());
        }

        request.setAttribute("cartList", list);
        return "cart";
    }

    @RequestMapping("/cart/update/num/{itemId}/{num}")
    @ResponseBody
    public E3Result updateCartNum(@PathVariable Long itemId, @PathVariable Integer num
            , HttpServletRequest request ,HttpServletResponse response) {
        TbUser user = (TbUser) request.getAttribute("user");
        if (user != null) {
            cartService.updateCart(user.getId(), itemId, num);
            return E3Result.ok();
        }
        List<TbItem> cartList = getCartListFromCookie(request);
        for (TbItem tbItem : cartList) {
            if (tbItem.getId().longValue() == itemId) {
                tbItem.setNum(num);
                break;
            }
        }
        CookieUtils.setCookie(request, response, "cart", JsonUtils.objectToJson(cartList), COOKIE_CART_REPIRE, true);
        return E3Result.ok();
    }

    @RequestMapping("/cart/delete/{itemId}")
    public String deleteCartItem(@PathVariable Long itemId, HttpServletRequest request, HttpServletResponse response) {
        TbUser user= (TbUser) request.getAttribute("user");
        if (user != null) {
            cartService.deleteCartItem(user.getId(), itemId);
            return "redirect:/cart/cart.html";
        }
        List<TbItem> list = getCartListFromCookie(request);
        for (TbItem t : list) {
            if (t.getId() == itemId.longValue()) {
                list.remove(t);
                break;
            }
        }
        CookieUtils.setCookie(request, response, "cart", JsonUtils.objectToJson(list), COOKIE_CART_REPIRE, true);
        return "redirect:/cart/cart.html";
    }
}