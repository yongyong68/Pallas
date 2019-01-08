package com.vip.pallas.search.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElasticRestClient {

	private static Logger logger = LoggerFactory.getLogger(ElasticRestClient.class);
	private static ElasticRestClient instance = new ElasticRestClient();
	private Map<String, RestClient> clients = new ConcurrentHashMap<String, RestClient>();
	private Map<String, TransportClient> nativeClients = new ConcurrentHashMap<>();
	private Object mutex = new Object();

	/**
	 * 
	 * @param address e.g 192.168.200.78:9200,192.168.200.84:9200
	 * @return RestClient
	 */
	public static RestClient build(String address) {
		return instance.getClient(address);
	}


	public static TransportClient buildNative(String address) throws NumberFormatException, UnknownHostException {
		return instance.getNativeClient(address);
	}

	/**
	 * 
	 * @param address e.g 192.168.200.78:9200,192.168.200.84:9200
	 * @return RestClient
	 */
	public RestClient getClient(String address) {
		if (StringUtils.isEmpty(address)) {
			return null;
		}

		if (!clients.containsKey(address)) {
			return createClient(address);
		}

		return clients.get(address);
	}

	private RestClient createClient(String address) {
		synchronized (mutex) {
			if (!clients.containsKey(address)) {
				String[] addressList = StringUtils.split(address, ',');
				List<HttpHost> hosts = new ArrayList<HttpHost>();
				for (String addr : addressList) {
					hosts.add(HttpHost.create(addr));
				}
				RestClient restClient = RestClient.builder(hosts.toArray(new HttpHost[] {}))
						.setRequestConfigCallback((RequestConfig.Builder requestConfigBuilder) -> requestConfigBuilder.setConnectTimeout(5000).setSocketTimeout(60000)).setMaxRetryTimeoutMillis(30000).build();
				clients.put(address, restClient);
				logger.info("init restClient with address:{} successfully.", address);
				return restClient;
			}
		}
		return clients.get(address);
	}
	
	public TransportClient getNativeClient(String address) throws NumberFormatException, UnknownHostException {
		if (StringUtils.isEmpty(address)) {
			return null;
		}

		if (!nativeClients.containsKey(address)) {
			return createNativeClient(address);
		}

		return nativeClients.get(address);
	}

	private TransportClient createNativeClient(String address) throws NumberFormatException, UnknownHostException {
		synchronized (mutex) {
			if (!clients.containsKey(address)) {
				String[] addressList = StringUtils.split(address, ',');
				InetSocketTransportAddress[] hostList = new InetSocketTransportAddress[addressList.length];
				for (int i = 0; i < addressList.length; i++) {
					String addr = addressList[i];
					String[] ipPort = addr.split(":");
					hostList[i] = new InetSocketTransportAddress(InetAddress.getByName(ipPort[0]),
							Integer.parseInt(ipPort[1]));
				}
				Settings settings = Settings.builder().put("client.transport.ignore_cluster_name", true).build();

				TransportClient client = new PreBuiltTransportClient(settings).addTransportAddresses(hostList);
				nativeClients.put(address, client);
				logger.info("init nativeClient with address:{} successfully.", address);
				return client;
			}
		}
		return nativeClients.get(address);
	}

}
