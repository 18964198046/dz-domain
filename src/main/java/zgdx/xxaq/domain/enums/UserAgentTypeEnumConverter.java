package zgdx.xxaq.domain.enums;

import javax.persistence.AttributeConverter;

public class UserAgentTypeEnumConverter implements AttributeConverter<UserAgentTypeEnum, Integer> {

    @Override
    public Integer convertToDatabaseColumn(UserAgentTypeEnum userAgentTypeEnum) {
        return userAgentTypeEnum.getCode();
    }

    @Override
    public UserAgentTypeEnum convertToEntityAttribute(Integer code) {
        for (UserAgentTypeEnum userAgentTypeEnum:  UserAgentTypeEnum.values()) {
            if (code == userAgentTypeEnum.getCode()) {
                return userAgentTypeEnum;
            }
        }
        throw new EnumConstantNotPresentException(UserAgentTypeEnum.class, String.valueOf(code));
    }
}
