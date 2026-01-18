package com.unstampedpages;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(useMainMethod = SpringBootTest.UseMainMethod.ALWAYS)
class UnstampedpagesApplicationTests {
	@Test
	void contextLoads() throws Exception {
		assertTrue(true);
	}

}
