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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import org.junit.Test;
import org.mockito.Mockito;

import com.nofacepress.statemachine.StateMachineGraph;
import com.nofacepress.statemachine.StateMachineGraphBuilder;
import com.nofacepress.statemachine.StateMachineGraphBuilder.StateMachineGraphBuild;
import com.nofacepress.statemachine.StateMachineInstance;
import com.nofacepress.statemachine.StateType;
import com.nofacepress.statemachine.listener.AfterStateChangedListener;

public class AfterStateChangedListenerTest {

	@Test
	public void test_GraphWide() throws IOException {
		StateMachineGraphBuild<TestStates, TestEvents, String> build = StateMachineGraphBuilder.builder();

		@SuppressWarnings("unchecked")
		AfterStateChangedListener<TestStates, TestEvents, String> listener = Mockito
				.mock(AfterStateChangedListener.class);

		StateMachineGraph<TestStates, TestEvents, String> graph = build
				.initial(TestStates.STATE_1)
				.transition(TestStates.STATE_1, TestStates.STATE_2, TestEvents.EVENT_1)
				.listener(listener)
				.build();

		StateMachineInstance<TestStates, TestEvents, String> instance = new StateMachineInstance<TestStates, TestEvents, String>(
				graph, "Context");

		assertFalse(instance.fireEvent(TestEvents.EVENT_2));
		verify(listener, times(0)).afterStateChanged(instance, graph.getStateInfo(TestStates.STATE_1),
				graph.getStateInfo(TestStates.STATE_2), TestEvents.EVENT_1);
		assertTrue(instance.fireEvent(TestEvents.EVENT_1));
		verify(listener, times(1)).afterStateChanged(instance, graph.getStateInfo(TestStates.STATE_1),
				graph.getStateInfo(TestStates.STATE_2), TestEvents.EVENT_1);

	}

	@Test
	public void test_GraphWideLoopBack() throws IOException {
		StateMachineGraphBuild<TestStates, TestEvents, String> build = StateMachineGraphBuilder.builder();

		@SuppressWarnings("unchecked")
		AfterStateChangedListener<TestStates, TestEvents, String> listener = Mockito
				.mock(AfterStateChangedListener.class);

		StateMachineGraph<TestStates, TestEvents, String> graph = build
				.initial(TestStates.STATE_1)
				.transition(TestStates.STATE_1, TestStates.STATE_1, TestEvents.EVENT_1)
				.listener(listener)
				.build();

		StateMachineInstance<TestStates, TestEvents, String> instance = new StateMachineInstance<TestStates, TestEvents, String>(
				graph, "Context");

		assertTrue(instance.fireEvent(TestEvents.EVENT_1));
		verify(listener, times(1)).afterStateChanged(instance, graph.getStateInfo(TestStates.STATE_1),
				graph.getStateInfo(TestStates.STATE_1), TestEvents.EVENT_1);

	}

	@Test
	public void test_PerStatee() throws IOException {
		StateMachineGraphBuild<TestStates, TestEvents, String> build = StateMachineGraphBuilder.builder();

		@SuppressWarnings("unchecked")
		AfterStateChangedListener<TestStates, TestEvents, String> listener1 = Mockito
				.mock(AfterStateChangedListener.class);
		@SuppressWarnings("unchecked")
		AfterStateChangedListener<TestStates, TestEvents, String> listener2 = Mockito
				.mock(AfterStateChangedListener.class);

		StateMachineGraph<TestStates, TestEvents, String> graph = build
				.initial(TestStates.STATE_1)
				.transition(TestStates.STATE_1, TestStates.STATE_2, TestEvents.EVENT_1)
				.listener(TestStates.STATE_1, listener1)
				.listener(TestStates.STATE_2, listener2)
				.build();

		StateMachineInstance<TestStates, TestEvents, String> instance = new StateMachineInstance<TestStates, TestEvents, String>(
				graph, "Context");

		assertFalse(instance.fireEvent(TestEvents.EVENT_2));
		verify(listener1, times(0)).afterStateChanged(instance, graph.getStateInfo(TestStates.STATE_1),
				graph.getStateInfo(TestStates.STATE_2), TestEvents.EVENT_1);
		verify(listener2, times(0)).afterStateChanged(instance, graph.getStateInfo(TestStates.STATE_1),
				graph.getStateInfo(TestStates.STATE_2), TestEvents.EVENT_1);
		assertTrue(instance.fireEvent(TestEvents.EVENT_1));
		verify(listener2, times(1)).afterStateChanged(instance, graph.getStateInfo(TestStates.STATE_1),
				graph.getStateInfo(TestStates.STATE_2), TestEvents.EVENT_1);
		verify(listener1, times(0)).afterStateChanged(instance, graph.getStateInfo(TestStates.STATE_1),
				graph.getStateInfo(TestStates.STATE_2), TestEvents.EVENT_1);

	}

	class FireListener implements AfterStateChangedListener<TestStates, TestEvents, String> {
		@Override
		public void afterStateChanged(StateMachineInstance<TestStates, TestEvents, String> instance,
				StateType<TestStates, TestEvents, String> from, StateType<TestStates, TestEvents, String> to,
				TestEvents event) {
				System.out.println("From:" + from.getId() + " To:" + to.getId() + " Event:" + event  );
				if (!to.isEnd()) {
					assertTrue(instance.fireEvent(TestEvents.EVENT_1));
				}
		}
	}
	
	@Test
	public void test_ImmediateFireEvent() throws IOException {
		StateMachineGraphBuild<TestStates, TestEvents, String> build = StateMachineGraphBuilder.builder();

		AfterStateChangedListener<TestStates, TestEvents, String> listener = new FireListener();;

		StateMachineGraph<TestStates, TestEvents, String> graph = build
				.initial(TestStates.STATE_1)
				.transition(TestStates.STATE_1, TestStates.STATE_2, TestEvents.EVENT_1)
				.transition(TestStates.STATE_2, TestStates.STATE_3, TestEvents.EVENT_1)
				.transition(TestStates.STATE_3, TestStates.STATE_4, TestEvents.EVENT_1)
				.listener(listener)
				.build();

		StateMachineInstance<TestStates, TestEvents, String> instance = new StateMachineInstance<TestStates, TestEvents, String>(
				graph, "Context");

		assertTrue(instance.fireEvent(TestEvents.EVENT_1));
		assertEquals(instance.getCurrentState().getId(), TestStates.STATE_4);
	

	}


}
