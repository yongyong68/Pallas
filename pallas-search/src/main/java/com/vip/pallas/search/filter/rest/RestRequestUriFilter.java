package com.vip.pallas.search.filter.rest;

import com.vip.pallas.search.filter.base.AbstractFilter;
import com.vip.pallas.search.filter.base.AbstractFilterContext;
import com.vip.pallas.search.filter.common.SessionContext;
import com.vip.pallas.search.filter.route.RouteFilter;
import com.vip.pallas.search.http.PallasRequest;
import com.vip.pallas.search.model.ServiceInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestRequestUriFilter extends AbstractFilter {
	public static String DEFAULT_NAME = PRE_FILTER_NAME + RestRequestUriFilter.class.getSimpleName().toUpperCase();
	private static final Logger logger = LoggerFactory.getLogger(RestRequestUriFilter.class);

	@Override
	public String name() {
		return DEFAULT_NAME;
	}

	@Override
	public void run(AbstractFilterContext filterContext, SessionContext sessionContext) throws Exception {
		PallasRequest req = sessionContext.getRequest();
		ServiceInfo si = sessionContext.getServiceInfo();
		String uri = req.getModifiedUri();
		// #427 pallas-search支持别名灰度切换
		// http://gitlab.tools.vipshop.com/platform/pallas/issues/427
		// 方案是通过检测 Target group 的title， 如果类似 {{index:msearch_rampup}} 这在当期那URI中把当前索引切换成转换的索引
		if (req.isIndexSearch() && si != null && si.getTargetGroupTitle().startsWith("{{") && si.getTargetGroupTitle().endsWith("}}")) {
			try {
				String indexName = req.getIndexName();
				String targetGroupTitle = si.getTargetGroupTitle();
				String rampupIndexName = targetGroupTitle.substring(targetGroupTitle.indexOf("index:")+6, targetGroupTitle.lastIndexOf("}}")).trim();
				uri = uri.replaceFirst("/" + indexName + "/", "/" + rampupIndexName + "/");
			} catch (Exception ignore) {
				logger.error(ignore.getMessage());
			}
		}
		sessionContext.setRestRequestUri(uri);
		super.run(filterContext, sessionContext);
	}

}
