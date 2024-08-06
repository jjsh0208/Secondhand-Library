package com.example.secondhandlibrary.Book.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync //비동기처리를 활성화한다. @Async 어노테이션이 붙은 메서드는 비동적 실행 가능
class BookAsyncConfig {
    @Bean(name = "bookApiTaskExecutor")
    public ThreadPoolTaskExecutor bookApiTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor(); //스레드 풀 관리 클래스 생성
        executor.setCorePoolSize(2);    //스레드 코어 풀 크기
        executor.setMaxPoolSize(4);     //스레드 풀이 생성할 수 있는 최대 스레드 수 (초과할 경우 큐에서 대기)
        executor.setQueueCapacity(500); //큐의 용량 설정
        executor.setThreadNamePrefix("APIClient-"); //스레드 접두사 설정 (log , debugging 확인용)
        executor.initialize();          //스레드 풀 초기화
        return executor;    //반환된 인스턴스는 Spring 컨테이너에 의해 관리된다.
    }
}