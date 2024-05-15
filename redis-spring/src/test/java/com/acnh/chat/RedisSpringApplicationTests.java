package com.acnh.chat;

import org.junit.jupiter.api.RepeatedTest;
import org.redisson.api.RAtomicLongReactive;
import org.redisson.api.RedissonReactiveClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
class RedisSpringApplicationTests {

	@Autowired
	private ReactiveStringRedisTemplate reactiveStringRedisTemplate;

	@Autowired
	RedissonReactiveClient redissonReactiveClient;

	@RepeatedTest(3)
	void contextLoads() {
		ReactiveValueOperations<String, String> valueOperations = this.reactiveStringRedisTemplate.opsForValue();

		long before = System.currentTimeMillis();

		Mono<Void> mono = Flux.range(1, 500000)
				.flatMap(i -> valueOperations.increment("user:1:visit"))
				.then();

		StepVerifier.create(mono)
				.verifyComplete();

		long after = System.currentTimeMillis();
		System.out.println(after - before + " ms");
	}

	@RepeatedTest(3)
	void redissonTest() {
		RAtomicLongReactive atomicLong = this.redissonReactiveClient.getAtomicLong("user:2:visit");
		long before = System.currentTimeMillis();

		Mono<Void> mono = Flux.range(1, 500000)
				.flatMap(i -> atomicLong.incrementAndGet())
				.then();

		StepVerifier.create(mono)
				.verifyComplete();

		long after = System.currentTimeMillis();
		System.out.println(after - before + " ms");
	}

}
