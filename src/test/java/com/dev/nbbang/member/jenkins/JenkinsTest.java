package com.dev.nbbang.member.jenkins;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class JenkinsTest {
    @Test
    @DisplayName("프로젝트 PR시 Webhook 발생 테스트 (Jenkins 자동 빌드 및 배포)")
    void 젠킨스_자동빌드용_테스트() {
        System.out.println("Jenkins Webhook generate auto build Test");
    }
}
