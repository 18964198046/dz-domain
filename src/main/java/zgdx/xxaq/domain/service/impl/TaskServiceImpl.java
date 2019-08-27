package zgdx.xxaq.domain.service.impl;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zgdx.xxaq.domain.service.HttpClientService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class TaskServiceImpl implements InitializingBean {

    @Autowired
    private HttpClientService httpClientService;

    @Override
    public void afterPropertiesSet() {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        executor.submit(()->httpClientService.getMonitorDomain());
        executor.submit(()->httpClientService.saveMonitorResult());
        executor.submit(()->httpClientService.monitorDomain());
    }
}
