package zgdx.xxaq.domain.read;

import au.com.bytecode.opencsv.CSVReader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import zgdx.xxaq.domain.entity.MonitorDomainEntity;
import zgdx.xxaq.domain.repository.MonitorDomainRepository;

import java.io.FileReader;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class CSVReadService implements InitializingBean {

    @Value("${csv.isRead}")
    private Boolean csvIsRead;

    @Value("${csv.filePath}")
    private String csvFilePath;

    @Autowired
    private MonitorDomainRepository monitorDomainRepository;

    private void csvRead(String csvFilePath) throws Exception {
        try(CSVReader csvReader = new CSVReader(new FileReader(csvFilePath))) {
            Collection<MonitorDomainEntity> monitorDomainEntities =
                    csvReader.readAll().stream().map(strs -> MonitorDomainEntity
                            .builder().domain(strs[0]).build()).collect(Collectors.toList());
            monitorDomainRepository.saveAll(monitorDomainEntities);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (csvIsRead) {
            csvRead(csvFilePath);
        }
    }

}
