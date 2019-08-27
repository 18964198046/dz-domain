package zgdx.xxaq.domain.entity;

import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import zgdx.xxaq.domain.enums.UserAgentTypeEnum;
import zgdx.xxaq.domain.enums.UserAgentTypeEnumConverter;

import javax.persistence.*;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@Entity
@Table(name = "monitor_domain")
@DynamicInsert
@DynamicUpdate
public class MonitorDomainEntity extends BaseEntity{

    @Column
    private String ip;

    @Column
    private String schema;

    @Column
    private String domain;

    @Column
    private String port;

    @Column
    private String path;

    @Column
    private Integer statusCode;

    @Column
    private String errorReason;

    @Column(length = 100)
    private String title;

    @Column(length = 100)
    private String keywords;

    @Column(length = 200)
    private String description;

    @Column
    @Convert(converter = UserAgentTypeEnumConverter.class)
    private UserAgentTypeEnum userAgentType;

    @Column(updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdTime;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedTime;

    public void setInfo(String ip, int statusCode) {
        this.ip = ip;
        this.statusCode = statusCode;
    }

    public void setInfo(String ip, int statusCode, String title, String keywords, String description) {
        this.ip = ip;
        this.statusCode = statusCode;
        setTitle(title);
        setKeywords(keywords);
        setDescription(description);
    }

    public void setTitle(String title) {
        if (StringUtils.isNotBlank(title) && title.length() > 50) {
            this.title = title.substring(0, 50);
        } else {
            this.title = title;
        }
    }

    public void setKeywords(String keywords) {
        if (StringUtils.isNotBlank(keywords) && keywords.length() > 25) {
            this.keywords = keywords.substring(0, 25);
        } else {
            this.keywords = keywords;
        }
    }

    public void setDescription(String description) {
        if (StringUtils.isNotBlank(description) && description.length() > 100) {
            this.description = description.substring(0, 100);
        } else {
            this.description = description;
        }
    }

    @PrePersist
    private void onUpdate() {
        updatedTime = new Date();
    }

}
