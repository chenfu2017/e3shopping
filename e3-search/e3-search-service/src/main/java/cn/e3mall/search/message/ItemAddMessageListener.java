package cn.e3mall.search.message;

import cn.e3mall.common.pojo.SearchItem;
import cn.e3mall.search.mapper.ItemMapper;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

public class ItemAddMessageListener implements MessageListener {

    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private SolrServer solrServer;
    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage) message;
        try {
            Thread.sleep(100);
            long itemId = new Long(textMessage.getText());
            SearchItem searchItem = itemMapper.getItemById(itemId);
            SolrInputDocument document = new SolrInputDocument();
            document.addField("id", searchItem.getId());
            document.addField("item_title", searchItem.getTitle());
            document.addField("item_sell_point", searchItem.getSell_point());
            document.addField("item_price", searchItem.getPrice());
            document.addField("item_image", searchItem.getImage());
            document.addField("item_category_name", searchItem.getCategory_name());
            solrServer.add(document);
            solrServer.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
