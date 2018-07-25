package cn.e3mall.search.service.impl;

import cn.e3mall.common.pojo.SearchResult;
import cn.e3mall.search.dao.SearchDao;
import cn.e3mall.search.service.SearchService;
import org.apache.solr.client.solrj.SolrQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private SearchDao searchDao;
    @Override
    public SearchResult search(String keyword, int page, int rows) throws Exception {
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setRows(rows);
        if (page <= 0) {
            page = 1;
        }
        solrQuery.setQuery(keyword);
        solrQuery.set("df", "item_title");
        solrQuery.setStart((page - 1) * rows);
        solrQuery.setHighlight(true);
        solrQuery.addHighlightField("item_title");
        solrQuery.setHighlightSimplePre("<em style=\"color:red\">");
        solrQuery.setHighlightSimplePost("</em>");
        SearchResult searchResult = searchDao.search(solrQuery);
        long recordCount = searchResult.getRecordCount();
        int totalPage = (int) Math.ceil(recordCount / rows);
        searchResult.setTotalPages(totalPage);
        return searchResult;
    }
}
