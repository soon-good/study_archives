package com.study.junit5.extension;

import com.study.junit5.extension.timer.TimerTestExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.boot.test.context.SpringBootTest;

//@ExtendWith(TimerTestExtension.class)
@SpringBootTest
public class TimerTest {

	@RegisterExtension
	TimerTestExtension timeoutExtension = new TimerTestExtension(1000L);

	@DisplayName("슬로우 메서드 #1 ")
	@Test
	public void slow_test_1() throws Exception {
		Thread.sleep(500);
		System.out.println(this);
	}

}
