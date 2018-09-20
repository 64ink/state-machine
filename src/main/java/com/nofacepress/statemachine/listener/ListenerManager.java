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
package com.nofacepress.statemachine.listener;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.nofacepress.statemachine.StateMachineInstance;
import com.nofacepress.statemachine.StateType;

/**
 * Container to track the various callback listeners.
 * 
 * @param <S> The state class
 * @param <E> The event class
 * @param <C> The context class
 */
public class ListenerManager<S, E, C> {

	private final Set<OnStateChangedListener<S, E, C>> onStateChangedListeners = new HashSet<OnStateChangedListener<S, E, C>>();

	private final Set<AfterStateChangedListener<S, E, C>> afterStateChangedListeners = new HashSet<AfterStateChangedListener<S, E, C>>();

	/**
	 * Adds a new AfterStateChangedListener.
	 * 
	 * @param listener the listener
	 */
	public void addListener(AfterStateChangedListener<S, E, C> listener) {
		afterStateChangedListeners.add(listener);
	}

	/**
	 * Adds a new OnStateChangedListener.
	 * 
	 * @param listener the listener
	 */
	public void addListener(OnStateChangedListener<S, E, C> listener) {
		onStateChangedListeners.add(listener);
	}

	/**
	 * Calls all AfterStateChangedListener listeners.
	 * 
	 * @param instance the instance of the state machine.
	 * @param from     the original state.
	 * @param to       the new state.
	 * @param event    the event that cause the transition.
	 */
	public void callAfterStateChangedListeners(StateMachineInstance<S, E, C> instance, StateType<S, E, C> source,
			StateType<S, E, C> target,
			E event) {
		// iterating in a way to allow removal
		for (Iterator<AfterStateChangedListener<S, E, C>> iterator = afterStateChangedListeners.iterator(); iterator
				.hasNext();) {
			AfterStateChangedListener<S, E, C> listener = iterator.next();
			listener.afterStateChanged(instance, source, target, event);
		}

	}

	/**
	 * Calls all OnStateChangedListener listeners.
	 * 
	 * @param instance the instance of the state machine.
	 * @param from     the original state.
	 * @param to       the new state.
	 * @param event    the event that cause the transition.
	 */
	public void callOnStateChangedListeners(StateMachineInstance<S, E, C> instance, StateType<S, E, C> source,
			StateType<S, E, C> target, E event) {
		// iterating in a way to allow removal
		for (Iterator<OnStateChangedListener<S, E, C>> iterator = onStateChangedListeners.iterator(); iterator
				.hasNext();) {
			OnStateChangedListener<S, E, C> listener = iterator.next();
			listener.onStateChanged(instance, source, target, event);
		}
	}

	/**
	 * Copies listeners from another listener instance.
	 * 
	 * @param listenerManager the instance to copy from.
	 */
	public void copyListeners(ListenerManager<S, E, C> listenerManager) {
		for (Iterator<OnStateChangedListener<S, E, C>> iterator = listenerManager.onStateChangedListeners
				.iterator(); iterator
						.hasNext();) {
			onStateChangedListeners.add(iterator.next());
		}
		for (Iterator<AfterStateChangedListener<S, E, C>> iterator = listenerManager.afterStateChangedListeners
				.iterator(); iterator
						.hasNext();) {
			afterStateChangedListeners.add(iterator.next());
		}
	}

	/**
	 * Removes an AfterStateChangedListener
	 * 
	 * @param listener the listener
	 */
	public void removeAfterStateChangedListener(AfterStateChangedListener<S, E, C> listener) {
		afterStateChangedListeners.remove(listener);
	}

	/**
	 * Removes an OnStateChangedListener
	 * 
	 * @param listener the listener
	 */
	public void removeOnStateChangedListener(OnStateChangedListener<S, E, C> listener) {
		onStateChangedListeners.remove(listener);
	}

}
