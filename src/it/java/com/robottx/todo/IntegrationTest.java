package com.robottx.todo;

import org.junit.jupiter.api.Tag;
import org.springframework.boot.test.context.SpringBootTest;

@Tag("IntegrationTest")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class IntegrationTest {
}
