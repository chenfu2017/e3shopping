package cn.e3mall.item.listener;

import cn.e3mall.item.pojo.Item;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbItemDesc;
import cn.e3mall.service.ItemService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class HtmlGenListener implements MessageListener {

    @Autowired
    private FreeMarkerConfig  freeMarkerConfig;
    @Autowired
    private ItemService itemService;
    @Value("${HTML_GEN_PATH}")
    private String HTML_GEN_PATH;
    @Override
    public void onMessage(Message message) {

        TextMessage textMessage = (TextMessage) message;
        try {
            Long itemId = new Long(textMessage.getText());
            //等待事务提交
            Thread.sleep(1000);
            TbItem tbItem = itemService.getTbItemById(itemId);
            Item item = new Item(tbItem);
            TbItemDesc itemDesc = itemService.getItemDesc(itemId);
            Map map = new HashMap();
            map.put("item", item);
            map.put("itemDesc", itemDesc);
            Configuration configuration = freeMarkerConfig.getConfiguration();
            Template template = configuration.getTemplate("item.ftl");
            Writer writer = new FileWriter(HTML_GEN_PATH + itemId + ".html");
            template.process(map,writer);
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
