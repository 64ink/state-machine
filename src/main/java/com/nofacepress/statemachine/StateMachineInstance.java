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

import com.nofacepress.statemachine.exceptions.StateMachineException;
import java.util.HashMap;
import java.util.Map;

/**
 * Class representing an context related instance of a state machine.
 *
 * @param <S> The state class
 * @param <E> The event class
 * @param <C> The context class
 */
public class StateMachineInstance<S, E, C> {

	private final StateMachineGraph<S, E, C> stateMachineGraph;
	private StateType<S, E, C> currentState;
	private boolean changeInProcess = false;
	private C context;
	private Map<String, Object> properties = null;

	/**
	 * Constructor using default initial state from graph configuration.
	 * 
	 * @param graph   model to use
	 * @param context the context associated with this instance
	 */
	public StateMachineInstance(StateMachineGraph<S, E, C> graph, C context) {
		this.stateMachineGraph = graph;
		this.currentState = graph.getStateType(graph.getInitialState());
		this.setContext(context);
	}

	/**
	 * Constructor with a specific initial state
	 * 
	 * @param graph        model to use
	 * @param initialState the initial state
	 * @param context      the context associated with this instance
	 */
	public StateMachineInstance(StateMachineGraph<S, E, C> graph, S initialState, C context) {
		this.stateMachineGraph = graph;
		this.currentState = graph.getStateType(initialState);
		this.setContext(context);
	}

	protected synchronized boolean changeState(StateType<S, E, C> target, E event, boolean notifyListeners)
			throws StateMachineException {

		if (changeInProcess) {
			throw new StateMachineException(
					"Change in process, must use AfterStateChangedListener's to be able to change states.");
		}

		// BEGIN STATE CHANGED
		StateType<S, E, C> previous = currentState;
		currentState = target;

		if (!notifyListeners) {
			return true;
		}

		try {
			changeInProcess = true;
			stateMachineGraph.getListenerManager().callOnStateChangedListeners(this, previous, target, event);
			target.getListenerManager().callOnStateChangedListeners(this, previous, target, event);

		} catch (Throwable t) {
			throw new StateMachineException(t.getMessage(), t);
		} finally {
			changeInProcess = false;
		}
		// END STATE CHANGED

		stateMachineGraph.getListenerManager().callAfterStateChangedListeners(this, previous, target, event);
		target.getListenerManager().callAfterStateChangedListeners(this, previous, target, event);

		return true;
	}

	/**
	 * Fires an event to cause a state change. This is the primary method.
	 * 
	 * @param event the event to fire
	 * @return true of event was valid, false otherwise
	 * @throws StateMachineException if called from an OnStateChangeListener or if a
	 *                               listener through an exception.
	 */
	public boolean fireEvent(E event) throws StateMachineException {

		StateType<S, E, C> target = currentState.getTransition(event);
		if (target == null) {
			return false;
		}

		return changeState(target, event, true);
	}

	/**
	 * Forces a state change. Normally fireEvent() should be used to enforce proper
	 * work flow.
	 * 
	 * @param state           the new state
	 * @param event           the event to pass to notified listeners, may be null
	 * @param notifyListeners if true listeners are notified like a normal
	 *                        transition, false otherwise
	 * @throws StateMachineException if called from an OnStateChangeListener or if a
	 *                               listener through an exception.
	 */
	public void forceStateChange(S state, E event, boolean notifyListeners) throws StateMachineException {

		StateType<S, E, C> target = stateMachineGraph.getStateType(state);

		if (target == null) {
			throw new StateMachineException("Invalid state " + String.valueOf(state));
		}

		changeState(target, event, notifyListeners);

	}

	/**
	 * Returns the context associated with this state machine instance.
	 * 
	 * @return the associated context
	 */
	public C getContext() {
		return context;
	}

	/**
	 * Returns the current state as a StateType.
	 * 
	 * @return the current StateType
	 */
	public StateType<S, E, C> getCurrentState() {
		return currentState;
	}

	/**
	 * Returns the associated StateMachineGraph
	 * 
	 * @return a StateMachineGraph
	 */
	public StateMachineGraph<S, E, C> getStateMachineGraph() {
		return stateMachineGraph;
	}

	/**
	 * Sets the associated context.
	 * 
	 * @param context the context
	 */
	public void setContext(C context) {
		this.context = context;
	}

	/**
	 * Adds an application defined property to the instance. Used to bundle other
	 * arbitrary data to the instance.
	 * 
	 * Passing value=null will remove the property.
	 * 
	 * @param key   the property key
	 * @param value the property value
	 */
	public void setProperty(String key, Object value) {
		if (value == null) {
			if (properties != null) {
				properties.remove(key);
			}
		} else {
			if (properties == null) {
				properties = new HashMap<String, Object>();
			}
			properties.put(key, value);
		}
	}

	/**
	 * Returns an application defined property from the instance. Properties are
	 * added and removed with setPropery().
	 * 
	 * @param key the property key
	 * @return the property value or null if not found
	 */
	public Object getProperty(String key) {
		return (properties == null) ? null : properties.get(key);
	}

}
