package io.study.tdd.tddforall.step2;

import java.io.IOException;
import javax.annotation.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@SpringBootTest
class Step2ControlerTest {

	@Autowired
	private Step2ControlerTest controller;

	private MockMvc mockMvc;

	@BeforeEach
	void setup(){
		mockMvc = MockMvcBuilders
			.standaloneSetup(controller)
			.build();
	}

	@Test
	@DisplayName("MockMvc 테스트 #1")
	void testStep2HomeTest() throws Exception {

		mockMvc
			// perform 은 RequestBuilders 를 인자로 받는다.
			// 일반적으로 MockMvc 를 접두사로 하는 MockMvcRequestBuilders 의 get 메서드를 사용한다.
			.perform(MockMvcRequestBuilders.get("/step1/home"))
			// andDo 는 ResultHandlers 를 인자로 받는다.
			// 일반적으로 MockMvc 를 접두사로 하는 MockMvcResultHandlers 의 print() 를 사용하는 편이다.
			.andDo(MockMvcResultHandlers.print())
			// andExpect 는 ResultMatcher 를 인자로 받는다.
			// 일반적으로 MockMvc를 접두사로 하는 MockMvcResultMatchers 의 메서드들을 사용하는 편이다.
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.model().attributeExists("serverTime"));
	}
}
