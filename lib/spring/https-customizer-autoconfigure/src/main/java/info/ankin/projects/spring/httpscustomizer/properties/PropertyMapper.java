package info.ankin.projects.spring.httpscustomizer.properties;

import info.ankin.projects.spring.httpscustomizer.HttpsCustomizerProperties;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PropertyMapper {
    PropertyMapper INSTANCE = Mappers.getMapper(PropertyMapper.class);

    HttpsCustomizerProperties toProps(HttpsCustomizerConfigurationProperties configurationProperties);
}
