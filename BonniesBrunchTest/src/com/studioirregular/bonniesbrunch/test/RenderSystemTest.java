package com.studioirregular.bonniesbrunch.test;

import android.test.InstrumentationTestCase;

import com.studioirregular.bonniesbrunch.ContextParameters;
import com.studioirregular.bonniesbrunch.Game;
import com.studioirregular.bonniesbrunch.GameRenderer;
import com.studioirregular.bonniesbrunch.RenderSystem;
import com.studioirregular.bonniesbrunch.RenderSystem.RenderObject;

public class RenderSystemTest extends InstrumentationTestCase {

	public void testRenderer() {
		GameRenderer renderer = new GameRenderer(getInstrumentation().getContext(), null);
		
		// screen resolution: 800 X 480
		renderer.setupViewport(null, 800, 480);
		
		ContextParameters params = ContextParameters.getInstance();
		assertEquals(40.0f, params.viewportOffsetX);
		assertEquals(0.0f, params.viewportOffsetY);
		assertEquals(720.0f, params.viewportWidth);
		assertEquals(480.0f, params.viewportHeight);
		
		// screen resolution: 1024 X 600
		renderer.setupViewport(null, 1024, 600);
		
		assertEquals(62.0f, params.viewportOffsetX);
		assertEquals(0.0f, params.viewportOffsetY);
		assertEquals(900.0f, params.viewportWidth);
		assertEquals(600.0f, params.viewportHeight);
	}
	
	public void testRenderSystem() {
		RenderSystem system = RenderSystem.getInstance();
		assertNotNull(system);
		assertEquals(RenderSystem.QUEUE_COUNT, system.drawQueue.length);
		
		system.reset();
		
		GameRenderer renderer = new GameRenderer(getInstrumentation().getContext(), null);
		
		RenderObject render = new RenderObject(720, 480);
		
		final int TEST_ROUND = 600;
		int index = 0;
		
		for (int i = 0; i < TEST_ROUND; i++) {
			index = system.index;
			
			system.scheduleRenderObject(render);
			
			system.swap(renderer);
			assertEquals("after schedule one render object, queue size should be 1", 1, system.drawQueue[index].size());
			
			index = (index + 1) % RenderSystem.QUEUE_COUNT;
			assertEquals(index, system.index);
			assertEquals("after swat queue size should be 0", 0, system.drawQueue[index].size());
		}
	}
}
