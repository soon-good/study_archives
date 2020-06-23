package com.study.application_event;


import com.study.application_event.config.TestSchemaInitConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Import(TestSchemaInitConfig.class)
@SpringBootTest
@ActiveProfiles("testdocker")
public class DataInsertTest {

	@Test
	public void test1(){

	}
}
