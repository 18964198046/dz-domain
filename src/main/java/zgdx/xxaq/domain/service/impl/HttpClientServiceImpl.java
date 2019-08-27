//package zgdx.xxaq.domain.service.impl;
//
//import lombok.extern.slf4j.Slf4j;
//import org.apache.http.client.methods.CloseableHttpResponse;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.client.protocol.HttpClientContext;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.stereotype.Service;
//import zgdx.xxaq.domain.entity.MonitorDomainEntity;
//import zgdx.xxaq.domain.repository.MonitorDomainRepository;
//import zgdx.xxaq.domain.service.HttpClientService;
//import zgdx.xxaq.domain.utils.HttpClientUtils;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.concurrent.CompletionService;
//import java.util.concurrent.LinkedBlockingQueue;
//import java.util.concurrent.atomic.AtomicInteger;
//import java.util.stream.Collectors;
//
//
//@Slf4j
////@Service
//public class HttpClientServiceImpl implements HttpClientService {
//
//    @Autowired
//    private CloseableHttpClient httpClient;
//
//    @Autowired
//    private MonitorDomainRepository monitorDomainRepository;
//
//    @Autowired
//    private CompletionService<MonitorDomainEntity> completionService;
//
//    private AtomicInteger handlingCount = new AtomicInteger(0);
//
//    private AtomicInteger handledCount = new AtomicInteger(0);
//
//    private LinkedBlockingQueue<MonitorDomainEntity> domainMonitorQueue = new LinkedBlockingQueue<>(5000);
//
//    private int pageNumber = 0;
//
//    private final static Sort sort = new Sort(Sort.Direction.ASC, "updatedTime");
//
//    @Override
//    public void getMonitorDomain() {
//        int pageSize = 1000;
//        try {
//            long beginTime = new Date().getTime();
//            Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
//            Page<MonitorDomainEntity> monitorDomainEntityPage = monitorDomainRepository.findAll(pageable);
//
//            if (monitorDomainEntityPage.isEmpty()) {
//                pageNumber = 0;
//            } else {
//                List<MonitorDomainEntity> monitorDomainEntities = monitorDomainEntityPage.get().collect(Collectors.toList());
//                for (int i = 0; i < monitorDomainEntities.size(); i++) {
//                    domainMonitorQueue.put(monitorDomainEntities.get(i));
//                }
//                pageNumber++;
//            }
//            long endTime = new Date().getTime();
//            log.info("Get Date Page Time: {}, DomainMointorQueue Size: {}", (endTime - beginTime) / 1000, domainMonitorQueue.size());
//        } catch(Exception e) {
//            log.info("GetMonitorDomain Exception:", e.getMessage());
//        }
//    }
//
//
//    @Override
//    public void saveMonitorResult() {
//        List<MonitorDomainEntity> monitorDomainEntityList = new ArrayList<>(1000);
//        while(true) {
//            try {
//                MonitorDomainEntity monitorDomainEntity = completionService.take().get();
//                monitorDomainEntityList.add(monitorDomainEntity);
//                if (monitorDomainEntityList.size() >= 1000) {
//                    long beginTime = new Date().getTime();
//                    monitorDomainRepository.saveAll(monitorDomainEntityList);
//                    monitorDomainEntityList.clear();
//                    long endTime = new Date().getTime();
//                    log.info("Save Date Page Time: {}", (endTime - beginTime) / 1000);
//                }
//            } catch(Exception e) {
//                log.info("saveMonitorResult exception: {}", e.getMessage());
//            }
//        }
//    }
//
//    @Override
//    public void monitorDomain() {
//        int i = 0;
//        long beginTime = new Date().getTime();
//        while(true) {
//            try {
//                if (domainMonitorQueue.size() < 2000) {
//                    getMonitorDomain();
//                }
//                if ((handlingCount.get()) < 100) {
//                    MonitorDomainEntity monitorDomainEntity = domainMonitorQueue.take();
//                    completionService.submit(() -> setResponseCode(monitorDomainEntity));
//                    handlingCount.getAndAdd(1);
//                    i++;
//                }
//                if (i % 1000 == 0) {
//                    long endTime = new Date().getTime();
//                    log.info("网站访问线程还在工作！SpendTime: {}, 当前DomainMoitorQueue Size: {}; HandingCount: {}", (endTime - beginTime) / 1000, domainMonitorQueue.size(), handlingCount.get());
//                    beginTime = new Date().getTime();
//                }
//            } catch (Exception e) {
//                log.info("Monitor Domain Exception: {}", e.getMessage());
//            }
//        }
//    }
//
//    private MonitorDomainEntity setResponseCode(MonitorDomainEntity monitorDomainEntity) {
//        String url = "http://" + monitorDomainEntity.getDomain();
//        HttpGet httpGet = new HttpGet(url);
//        HttpClientContext httpClientContext = HttpClientContext.create();
//        try (CloseableHttpResponse httpResponse = httpClient.execute(httpGet, httpClientContext)) {
//            String ip = HttpClientUtils.getServerIp(httpClientContext);
//            int statusCode = HttpClientUtils.getStatusCode(httpResponse);
//            monitorDomainEntity.setInfo(ip, statusCode);
//            handlingCount.getAndAdd(-1);
//            handledCount.getAndAdd(1);
//        } catch(Exception e) {
//            handlingCount.getAndAdd(-1);
//            monitorDomainEntity.setErrorReason(e.getClass().getSimpleName());
//        }
//        monitorDomainEntity.setUpdatedTime(new Date());
//        return monitorDomainEntity;
//    }
//
//}
