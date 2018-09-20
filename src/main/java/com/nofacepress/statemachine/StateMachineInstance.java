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

public class StateMachineInstance<S, E, C> {

	private final StateMachineGraph<S, E, C> stateMachineGraph;
	private StateType<S, E, C> currentState;
	private boolean changeInProcess = false;
	private C context;
	private Map<String, Object> properties = null;
	

	public StateMachineInstance(StateMachineGraph<S, E, C> graph, C context) {
		this.stateMachineGraph = graph;
		this.currentState = graph.getStateInfo(graph.getInitialState());
		this.setContext(context);
	}

	public StateMachineInstance(StateMachineGraph<S, E, C> graph, S initialState, C context) {
		this.stateMachineGraph = graph;
		this.currentState = graph.getStateInfo(initialState);
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

	public boolean fireEvent(E event) throws StateMachineException {

		StateType<S, E, C> target = currentState.getTransition(event);
		if (target == null) {
			return false;
		}

		return changeState(target, event, true);
	}

	public void forceStateChange(S state, E event, boolean notifyListeners) throws StateMachineException {

		StateType<S, E, C> target = stateMachineGraph.getStateInfo(state);

		if (target == null) {
			throw new StateMachineException("Invalid state " + String.valueOf(state));
		}

		changeState(target, event, notifyListeners);

	}

	public C getContext() {
		return context;
	}

	public StateType<S, E, C> getCurrentState() {
		return currentState;
	}

	public StateMachineGraph<S, E, C> getStateMachineGraph() {
		return stateMachineGraph;
	}

	public void setContext(C context) {
		this.context = context;
	}

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

	public Object getProperty(String key) {
		return (properties == null)  ? null : properties.get(key);
	}

}
