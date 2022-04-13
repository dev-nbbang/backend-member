package com.dev.nbbang.member.domain.user.api.config;

import com.dev.nbbang.member.domain.user.api.entity.SocialLoginType;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

import java.util.Locale;

@Configuration
public class SocialLoginTypeConverter implements Converter<String, SocialLoginType> {
    @Override
    public SocialLoginType convert(String source) {
        return SocialLoginType.valueOf(source.toUpperCase(Locale.ROOT));
    }
}
