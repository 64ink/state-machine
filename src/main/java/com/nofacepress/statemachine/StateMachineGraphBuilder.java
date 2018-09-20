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
package com.nofacepress.statemachine;

import com.nofacepress.statemachine.impl.StateMachineGraphImpl;
import com.nofacepress.statemachine.listener.AfterStateChangedListener;
import com.nofacepress.statemachine.listener.OnStateChangedListener;

public class StateMachineGraphBuilder {

	public static class StateMachineGraphBuild<S, E, C> {

		protected StateMachineGraphImpl<S, E, C> graph = new StateMachineGraphImpl<S, E, C>();

		public StateMachineGraph<S, E, C> build() {
			return graph;
		}

		public StateMachineGraphBuild<S, E, C> initial(S state) {
			graph.setInitialState(state);
			return this;
		}

		public StateMachineGraphBuild<S, E, C> listener(AfterStateChangedListener<S, E, C> listener) {
			graph.getListenerManager().addListener(listener);
			return this;
		}

		public StateMachineGraphBuild<S, E, C> listener(OnStateChangedListener<S, E, C> listener) {
			graph.getListenerManager().addListener(listener);
			return this;
		}

		public StateMachineGraphBuild<S, E, C> listener(S state, AfterStateChangedListener<S, E, C> listener) {
			graph.addState(state).getListenerManager().addListener(listener);
			return this;
		}

		public StateMachineGraphBuild<S, E, C> listener(S state, OnStateChangedListener<S, E, C> listener) {
			graph.addState(state).getListenerManager().addListener(listener);
			return this;
		}

		public StateMachineGraphBuild<S, E, C> state(S state) {
			graph.addState(state);
			return this;
		}

		public StateMachineGraphBuild<S, E, C> states(S[] states) {
			for (S s : states) {
				graph.addState(s);
			}
			return this;
		}

		public StateMachineGraphBuild<S, E, C> transition(S source, S target, E event) {
			graph.addTransition(source, target, event);
			return this;
		}
	}

	public static <S, E, C> StateMachineGraphBuild<S, E, C> builder() {
		return new StateMachineGraphBuild<S, E, C>();
	}

}
