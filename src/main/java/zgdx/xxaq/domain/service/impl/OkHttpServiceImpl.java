package zgdx.xxaq.domain.service.impl;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import zgdx.xxaq.domain.entity.MonitorDomainEntity;
import zgdx.xxaq.domain.repository.MonitorDomainRepository;
import zgdx.xxaq.domain.service.HttpClientService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OkHttpServiceImpl implements HttpClientService {

    @Autowired
    private OkHttpClient okHttpClient;

    @Autowired
    private MonitorDomainRepository monitorDomainRepository;

    @Autowired
    private CompletionService<MonitorDomainEntity> completionService;

    private AtomicInteger handlingCount = new AtomicInteger(0);

    private AtomicInteger handledCount = new AtomicInteger(0);

    private LinkedBlockingQueue<MonitorDomainEntity> domainMonitorQueue = new LinkedBlockingQueue<>(5000);

    private int pageNumber = 0;

    private final static Sort sort = new Sort(Sort.Direction.ASC, "updatedTime");

    @Override
    public void getMonitorDomain() {
        final int pageSize = 1000;
        while (true) {
            try {
                long beginTime = new Date().getTime();
                Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
                Page<MonitorDomainEntity> monitorDomainEntityPage = monitorDomainRepository.findAll(pageable);

                if (monitorDomainEntityPage.isEmpty()) {
                    pageNumber = 0;
                } else {
                    List<MonitorDomainEntity> monitorDomainEntities = monitorDomainEntityPage.get().collect(Collectors.toList());
                    for (int i = 0; i < monitorDomainEntities.size(); i++) {
                        domainMonitorQueue.put(monitorDomainEntities.get(i));
                    }
                    pageNumber++;
                }
                long endTime = new Date().getTime();
                log.info("Get Date Page Time: {}, DomainMointorQueue Size: {}", (endTime - beginTime) / 1000, domainMonitorQueue.size());
            } catch (Exception e) {
                log.info("GetMonitorDomain Exception:", e.getMessage());
            }
        }
    }

    @Override
    public void saveMonitorResult() {
        List<MonitorDomainEntity> monitorDomainEntityList = new ArrayList<>(1000);
        while(true) {
            try {
                MonitorDomainEntity monitorDomainEntity = completionService.take().get();
                monitorDomainEntityList.add(monitorDomainEntity);
                if (monitorDomainEntityList.size() >= 1000) {
                    long beginTime = new Date().getTime();
                    monitorDomainRepository.saveAll(monitorDomainEntityList);
                    monitorDomainEntityList.clear();
                    long endTime = new Date().getTime();
                    log.info("Save Date Page Time: {}", (endTime - beginTime) / 1000);
                }
            } catch(Exception e) {
                log.info("saveMonitorResult exception: {}", e.getMessage());
            }
        }
    }

    @Override
    public void monitorDomain() {
        int i = 0;
        long beginTime = new Date().getTime();
        while(true) {
            try {
                /*if (domainMonitorQueue.size() < 2000) {
                    getMonitorDomain();
                }*/
                if ((handlingCount.get()) < 100) {
                    MonitorDomainEntity monitorDomainEntity = domainMonitorQueue.take();
                    completionService.submit(() -> setResponseValue(monitorDomainEntity));
                    handlingCount.getAndAdd(1);
                    i++;
                }
                if (i % 1000 == 0) {
                    long endTime = new Date().getTime();
                    log.info("网站访问线程还在工作！SpendTime: {}, 当前DomainMoitorQueue Size: {}; HandingCount: {}", (endTime - beginTime) / 1000, domainMonitorQueue.size(), handlingCount.get());
                    beginTime = new Date().getTime();
                }
            } catch (Exception e) {
                log.info("Monitor Domain Exception: {}", e.getMessage());
            }
        }
    }

    private MonitorDomainEntity setResponseValue(MonitorDomainEntity monitorDomainEntity) {
        Request request = createRequest(monitorDomainEntity);
        try (Response response =  okHttpClient.newCall(request).execute()) {
            setResponseValue(monitorDomainEntity, response);
            handlingCount.getAndAdd(-1);
            handledCount.getAndAdd(1);
        } catch(Exception e) {
            handlingCount.getAndAdd(-1);
            monitorDomainEntity.setErrorReason(e.getClass().getSimpleName());
        }
        monitorDomainEntity.setUpdatedTime(new Date());
        return monitorDomainEntity;
    }

    private MonitorDomainEntity setResponseValue(MonitorDomainEntity monitorDomainEntity, Response response) throws IOException {
        String ip = response.exchange().connection().socket().getInetAddress().getHostAddress();
        int statusCode = response.code();
        if (statusCode == 200) {
            Document doc = Jsoup.parse(response.body().string());
            Elements head = doc.getElementsByTag("head");
            Elements meta = head.select("meta");
            String title = head.select("title").text();
            String keywords = meta.select("meta[name=keywords]").attr("content");
            String description = meta.select("meta[name=description]").attr("content");
            monitorDomainEntity.setInfo(ip, statusCode, title, keywords, description);
            monitorDomainEntity.setErrorReason("");
        } else {
            monitorDomainEntity.setInfo(ip, statusCode);
        }
        return monitorDomainEntity;
    }

    private Request createRequest(MonitorDomainEntity monitorDomainEntity) {
        String url = monitorDomainEntity.getSchema() + "://" + monitorDomainEntity.getDomain() + ":" + monitorDomainEntity.getPort() + (monitorDomainEntity.getPath()==null ? "" : "/"+monitorDomainEntity.getPath());
        return new Request.Builder()
                .header("User-Agent", monitorDomainEntity.getUserAgentType().getUserAgent())
                .addHeader("Accept", "*****")
                .url(url)
                .get()
                .build();
    }

}
