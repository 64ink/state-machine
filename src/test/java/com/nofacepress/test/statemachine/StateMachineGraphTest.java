/*
 * Copyright 2018 No Face Press, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nofacepress.test.statemachine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import com.nofacepress.statemachine.StateMachineGraph;
import com.nofacepress.statemachine.StateMachineGraphBuilder;
import com.nofacepress.statemachine.StateMachineGraphBuilder.StateMachineGraphBuild;
import com.nofacepress.statemachine.StateMachineInstance;

public class StateMachineGraphTest {

	@Test
	public void testBuild_BranchEventTransition() throws IOException {
		StateMachineGraphBuild<TestStates, TestEvents, String> build = StateMachineGraphBuilder.builder();

		StateMachineGraph<TestStates, TestEvents, String> graph = build
				.initial(TestStates.STATE_1)
				.transition(TestStates.STATE_1, TestStates.STATE_2, TestEvents.EVENT_1)
				.transition(TestStates.STATE_1, TestStates.STATE_3, TestEvents.EVENT_2)
				.transition(TestStates.STATE_1, TestStates.STATE_4, TestEvents.EVENT_3)
				.build();

		StateMachineInstance<TestStates, TestEvents, String> instance = new StateMachineInstance<TestStates, TestEvents, String>(
				graph, "Context");

		assertEquals(instance.getCurrentState().getId(), TestStates.STATE_1);
		assertTrue(instance.fireEvent(TestEvents.EVENT_1));
		assertEquals(instance.getCurrentState().getId(), TestStates.STATE_2);
		assertFalse(instance.fireEvent(TestEvents.EVENT_1));
		assertEquals(instance.getCurrentState().getId(), TestStates.STATE_2);
		assertTrue(instance.getCurrentState().isEnd());

	}

	@Test
	public void testBuild_LinearTransition() throws IOException {
		StateMachineGraphBuild<TestStates, TestEvents, String> build = StateMachineGraphBuilder.builder();

		StateMachineGraph<TestStates, TestEvents, String> graph = build
				.initial(TestStates.STATE_1)
				.transition(TestStates.STATE_1, TestStates.STATE_2, TestEvents.EVENT_1)
				.transition(TestStates.STATE_2, TestStates.STATE_3, TestEvents.EVENT_2)
				.transition(TestStates.STATE_3, TestStates.STATE_4, TestEvents.EVENT_3)
				.build();

		StateMachineInstance<TestStates, TestEvents, String> instance = new StateMachineInstance<TestStates, TestEvents, String>(
				graph, "Context");

		assertEquals(instance.getCurrentState().getId(), TestStates.STATE_1);
		assertFalse(instance.getCurrentState().isEnd());
		assertFalse(instance.fireEvent(TestEvents.EVENT_2));

		assertTrue(instance.fireEvent(TestEvents.EVENT_1));
		assertEquals(instance.getCurrentState().getId(), TestStates.STATE_2);
		assertFalse(instance.getCurrentState().isEnd());

		assertTrue(instance.fireEvent(TestEvents.EVENT_2));
		assertEquals(instance.getCurrentState().getId(), TestStates.STATE_3);
		assertFalse(instance.getCurrentState().isEnd());

		assertTrue(instance.fireEvent(TestEvents.EVENT_3));
		assertEquals(instance.getCurrentState().getId(), TestStates.STATE_4);
		assertTrue(instance.getCurrentState().isEnd());

	}

}
