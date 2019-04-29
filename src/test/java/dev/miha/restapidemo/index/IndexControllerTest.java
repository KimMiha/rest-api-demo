package dev.miha.restapidemo.index;

import dev.miha.restapidemo.common.BaseControllerTest;
import org.junit.Test;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class IndexControllerTest extends BaseControllerTest {

  @Test
  public void index() throws Exception{
    ResultActions resultActions = this.mockMvc.perform(get("/api/"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("_links.events").exists());
  }
}
