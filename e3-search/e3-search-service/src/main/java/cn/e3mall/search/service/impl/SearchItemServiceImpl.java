package cn.e3mall.search.service.impl;

import cn.e3mall.common.pojo.SearchItem;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.search.mapper.ItemMapper;
import cn.e3mall.search.service.SearchItemService;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchItemServiceImpl implements SearchItemService {

    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private SolrServer solrServer;
    @Override
    public E3Result importAllItems() {
        try {
            List<SearchItem> list = itemMapper.getItemList();
            for (SearchItem searchItem:list) {
                SolrInputDocument document = new SolrInputDocument();
                document.addField("id", searchItem.getId());
                document.addField("item_title", searchItem.getTitle());
                document.addField("item_sell_point", searchItem.getSell_point());
                document.addField("item_price", searchItem.getPrice());
                document.addField("item_image", searchItem.getImage());
                document.addField("item_category_name", searchItem.getCategory_name());
                solrServer.add(document);
                return E3Result.ok();
            }
            solrServer.commit();
        } catch (Exception e) {
            e.printStackTrace();
            return E3Result.build(500, "数据导入时发生异常");
        }
        return null;
    }
}
