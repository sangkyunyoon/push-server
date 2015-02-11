package com.cosmicpush.plugins.gtalk;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.URL;

@Test
public class GoogleTalkPluginTest {

    private GoogleTalkPlugin plugin;

    @BeforeClass
    public void beforeClass() throws Exception {
    }

    @Test
    public void iconUrlTest() {
      URL url = getClass().getResource("/com/cosmicpush/plugins/gtalk/icon-enabled.png");
      Assert.assertNotNull(url);

      url = getClass().getResource("/com/cosmicpush/plugins/gtalk/icon-disabled.png");
      Assert.assertNotNull(url);
    }
}