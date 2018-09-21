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

import java.util.List;

import com.nofacepress.statemachine.impl.StateMachineGraphImpl;
import com.nofacepress.statemachine.listener.AfterStateChangedListener;
import com.nofacepress.statemachine.listener.OnStateChangedListener;

/**
 * Class to simplify configuration of a StateMachineGraph using the builder
 * pattern.
 */
public class StateMachineGraphBuilder {

	/**
	 * Class to simplify configuration of a StateMachineGraph using the builder
	 * pattern.
	 * 
	 * @param <S> The state class
	 * @param <E> The event class
	 * @param <C> The context class
	 */
	public static class StateMachineGraphBuild<S, E, C> {

		protected StateMachineGraphImpl<S, E, C> graph = new StateMachineGraphImpl<S, E, C>();

		/**
		 * Generates the final StateMachineGraph
		 * 
		 * @return the final StateMachineGraph
		 */
		public StateMachineGraph<S, E, C> build() {
			return graph;
		}

		/**
		 * Sets the default initial state.
		 * 
		 * @param state the initial state
		 * @return the builder for chaining
		 */
		public StateMachineGraphBuild<S, E, C> initial(S state) {
			graph.setInitialState(state);
			return this;
		}

		/**
		 * Adds an AfterStateChangedListener for all states.
		 * 
		 * @param listener the listener to add
		 * @return the builder for chaining
		 */
		public StateMachineGraphBuild<S, E, C> listener(AfterStateChangedListener<S, E, C> listener) {
			graph.getListenerManager().addListener(listener);
			return this;
		}

		/**
		 * Adds an OnStateChangedListener for all states.
		 * 
		 * @param listener the listener to add
		 * @return the builder for chaining
		 */
		public StateMachineGraphBuild<S, E, C> listener(OnStateChangedListener<S, E, C> listener) {
			graph.getListenerManager().addListener(listener);
			return this;
		}

		/**
		 * Adds an AfterStateChangedListener for a specific state.
		 * 
		 * @param state    state to add to.
		 * @param listener the listener
		 * @return the builder for chaining
		 */
		public StateMachineGraphBuild<S, E, C> listener(S state, AfterStateChangedListener<S, E, C> listener) {
			graph.addState(state).getListenerManager().addListener(listener);
			return this;
		}

		/**
		 * Adds an OnStateChangedListener for a specific state.
		 * 
		 * @param state    state to add to.
		 * @param listener the listener
		 * @return the builder for chaining
		 */
		public StateMachineGraphBuild<S, E, C> listener(S state, OnStateChangedListener<S, E, C> listener) {
			graph.addState(state).getListenerManager().addListener(listener);
			return this;
		}

		/**
		 * Adds a state to the graph.
		 * 
		 * @param state the state to add
		 * @return the builder for chaining
		 */
		public StateMachineGraphBuild<S, E, C> state(S state) {
			graph.addState(state);
			return this;
		}

		/**
		 * Adds a list of states to the graph.
		 * 
		 * @param states the states to add
		 * @return the builder for chaining
		 */
		public StateMachineGraphBuild<S, E, C> states(S[] states) {
			for (S s : states) {
				graph.addState(s);
			}
			return this;
		}

		/**
		 * Adds a list of states to the graph.
		 * 
		 * @param states the states to add
		 * @return the builder for chaining
		 */
		public StateMachineGraphBuild<S, E, C> states(List<S> states) {
			for (S s : states) {
				graph.addState(s);
			}
			return this;
		}

		/**
		 * Adds a transition to the graph.
		 * 
		 * @param source the state before the transition
		 * @param target the state after the transition
		 * @param event  the event that causes the transition
		 * @return the builder for chaining
		 */
		public StateMachineGraphBuild<S, E, C> transition(S source, S target, E event) {
			graph.addTransition(source, target, event);
			return this;
		}
	}

	/**
	 * Static method to create a type appropriate builder.
	 * 
	 * @param <S> The state class
	 * @param <E> The event class
	 * @param <C> The context class
	 * @return the builder
	 */
	public static <S, E, C> StateMachineGraphBuild<S, E, C> builder() {
		return new StateMachineGraphBuild<S, E, C>();
	}

}
