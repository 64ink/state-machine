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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Test;

import com.nofacepress.statemachine.StateMachineGraph;
import com.nofacepress.statemachine.StateMachineGraphBuilder;
import com.nofacepress.statemachine.StateMachineGraphBuilder.StateMachineGraphBuild;
import com.nofacepress.statemachine.StateType;

public class StateMachineGraphBuilderTest {

	@Test
	public void testBuild_BasicTransition() throws IOException {
		StateMachineGraphBuild<TestStates, TestEvents, String> build = StateMachineGraphBuilder.builder();

		StateMachineGraph<TestStates, TestEvents, String> graph = build
				.transition(TestStates.STATE_1, TestStates.STATE_2, TestEvents.EVENT_1)
				.transition(TestStates.STATE_2, TestStates.STATE_3, TestEvents.EVENT_2)
				.transition(TestStates.STATE_3, TestStates.STATE_4, TestEvents.EVENT_3)
				.build();

		List<TestStates> expectedStates = Arrays.asList(TestStates.values());

		Collection<? extends StateType<TestStates, TestEvents, String>> states = graph.getStates();

		assertEquals(states.size(), expectedStates.size());

		for (StateType<TestStates, TestEvents, String> item : graph.getStates()) {
			assertTrue(expectedStates.contains(item.getId()));
		}

		StateType<TestStates, TestEvents, String> state;

		state = graph.getStateType(TestStates.STATE_1);
		assertNotNull(state);
		assertFalse(state.isEnd());
		assertEquals(state.getTransitions().size(), 1);
		assertNotNull(state.getTransition(TestEvents.EVENT_1));
		assertEquals(state.getTransition(TestEvents.EVENT_1).getId(), TestStates.STATE_2);

		state = graph.getStateType(TestStates.STATE_2);
		assertNotNull(state);
		assertFalse(state.isEnd());
		assertEquals(state.getTransitions().size(), 1);
		assertNotNull(state.getTransition(TestEvents.EVENT_2));
		assertEquals(state.getTransition(TestEvents.EVENT_2).getId(), TestStates.STATE_3);

		state = graph.getStateType(TestStates.STATE_3);
		assertNotNull(state);
		assertFalse(state.isEnd());
		assertEquals(state.getTransitions().size(), 1);
		assertNotNull(state.getTransition(TestEvents.EVENT_3));
		assertEquals(state.getTransition(TestEvents.EVENT_3).getId(), TestStates.STATE_4);

		state = graph.getStateType(TestStates.STATE_4);
		assertNotNull(state);
		assertTrue(state.isEnd());
		assertEquals(state.getTransitions().size(), 0);
		assertNull(state.getTransition(TestEvents.EVENT_3));

	}

	@Test
	public void testBuild_BranchEventTransition() throws IOException {
		StateMachineGraphBuild<TestStates, TestEvents, String> build = StateMachineGraphBuilder.builder();

		StateMachineGraph<TestStates, TestEvents, String> graph = build
				.transition(TestStates.STATE_1, TestStates.STATE_2, TestEvents.EVENT_1)
				.transition(TestStates.STATE_1, TestStates.STATE_3, TestEvents.EVENT_2)
				.transition(TestStates.STATE_1, TestStates.STATE_4, TestEvents.EVENT_3)
				.build();

		List<TestStates> expectedStates = Arrays.asList(TestStates.values());

		Collection<? extends StateType<TestStates, TestEvents, String>> states = graph.getStates();

		assertEquals(states.size(), expectedStates.size());

		for (StateType<TestStates, TestEvents, String> item : graph.getStates()) {
			assertTrue(expectedStates.contains(item.getId()));
		}

		StateType<TestStates, TestEvents, String> state;

		state = graph.getStateType(TestStates.STATE_1);
		assertNotNull(state);
		assertFalse(state.isEnd());
		assertEquals(state.getTransitions().size(), 3);
		assertNotNull(state.getTransition(TestEvents.EVENT_1));
		assertEquals(state.getTransition(TestEvents.EVENT_1).getId(), TestStates.STATE_2);
		assertNotNull(state.getTransition(TestEvents.EVENT_2));
		assertEquals(state.getTransition(TestEvents.EVENT_2).getId(), TestStates.STATE_3);
		assertNotNull(state.getTransition(TestEvents.EVENT_3));
		assertEquals(state.getTransition(TestEvents.EVENT_3).getId(), TestStates.STATE_4);

	}

	@Test
	public void testBuild_initial() throws IOException {
		StateMachineGraphBuild<TestStates, TestEvents, String> build = StateMachineGraphBuilder.builder();

		StateMachineGraph<TestStates, TestEvents, String> graph = build.state(TestStates.STATE_1)
				.initial(TestStates.STATE_2).state(TestStates.STATE_3).build();

		Collection<? extends StateType<TestStates, TestEvents, String>> states = graph.getStates();

		assertEquals(states.size(), 3);
		assertEquals(graph.getInitialState(), TestStates.STATE_2);

	}

	@Test
	public void testBuild_noinitial() throws IOException {
		StateMachineGraphBuild<TestStates, TestEvents, String> build = StateMachineGraphBuilder.builder();

		StateMachineGraph<TestStates, TestEvents, String> graph = build.state(TestStates.STATE_1)
				.state(TestStates.STATE_2).state(TestStates.STATE_3).build();

		Collection<? extends StateType<TestStates, TestEvents, String>> states = graph.getStates();

		assertEquals(states.size(), 3);
		assertEquals(graph.getInitialState(), TestStates.STATE_1);

	}

	@Test
	public void testBuild_state() throws IOException {
		StateMachineGraphBuild<TestStates, TestEvents, String> build = StateMachineGraphBuilder.builder();

		StateMachineGraph<TestStates, TestEvents, String> graph = build.state(TestStates.STATE_1)
				.state(TestStates.STATE_1).state(TestStates.STATE_2).build();

		List<TestStates> expectedStates = new ArrayList<TestStates>();
		expectedStates.add(TestStates.STATE_1);
		expectedStates.add(TestStates.STATE_2);

		Collection<? extends StateType<TestStates, TestEvents, String>> states = graph.getStates();

		assertEquals(states.size(), expectedStates.size());

		for (StateType<TestStates, TestEvents, String> item : graph.getStates()) {
			assertTrue(expectedStates.contains(item.getId()));
		}

	}

	@Test
	public void testBuild_states() throws IOException {
		StateMachineGraphBuild<TestStates, TestEvents, String> build = StateMachineGraphBuilder.builder();

		StateMachineGraph<TestStates, TestEvents, String> graph = build.states(TestStates.values()).build();

		List<TestStates> expectedStates = Arrays.asList(TestStates.values());

		Collection<? extends StateType<TestStates, TestEvents, String>> states = graph.getStates();

		assertEquals(states.size(), expectedStates.size());

		for (StateType<TestStates, TestEvents, String> item : graph.getStates()) {
			assertTrue(expectedStates.contains(item.getId()));
		}

	}

}
