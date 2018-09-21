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
package com.nofacepress.statemachine.plantxml;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.stream.XMLStreamException;

import com.nofacepress.statemachine.StateMachineGraph;
import com.nofacepress.statemachine.StateType;
import com.nofacepress.statemachine.typeconverters.EnumToStringConverter;
import com.nofacepress.statemachine.typeconverters.StringToStringConverter;
import com.nofacepress.statemachine.typeconverters.ToStringConverter;

/**
 * Creates a PlanetUML state chart based on information probed from a State
 * Machine. This was created to find errors when setting up the state machine.
 */
public class PlantUmlManager {

	private static final class PlanetUMLConstants {
		public static final String UP_ARROW = "-up->";
		public static final String DOWN_ARROW = "-down->";
		public static final String LEFT_ARROW = "-left->";
		public static final String RIGHT_ARROW = "-right->";
		public static final String START_UML = "@startuml";
		public static final String END_UML = "@enduml";
		public static final String STATE_PARAM = "state";
		public static final String AS = "as";
		public static final String END_STATE = "[*]";
		public static final String BEGIN_STATE = "[*]";
		public static final String TITLE_PARAM = "title";
		public static final String MONOCHROME = "skinparam monochrome true";
	}

	/**
	 * Creates a PlanetUML state chart assuming both the states and events are type
	 * String.
	 * 
	 * @param          <C> the context type
	 * @param graph    the graph to save
	 * @param title    the title to put on the chart, null is ok for no title.
	 * @param filename the output file name
	 * @throws XMLStreamException on XML error
	 * @throws IOException        on IO error
	 */
	public static <C> void saveStringGraph(StateMachineGraph<String, String, C> graph, String title, String filename)
			throws XMLStreamException, IOException {
		ToStringConverter<String> conv = new StringToStringConverter();
		saveGraph(graph, title, filename, conv, conv);
	}

	/**
	 * Creates a PlanetUML state chart assuming both the states and events are type
	 * String.
	 * 
	 * @param        <C> the context type
	 * @param graph  the graph to save
	 * @param title  the title to put on the chart, null is ok for no title.
	 * @param writer the output to write to
	 * @throws XMLStreamException on XML error
	 * @throws IOException        on IO error
	 */
	public static <C> void saveStringGraph(StateMachineGraph<String, String, C> graph, String title, Writer writer)
			throws XMLStreamException, IOException {
		ToStringConverter<String> conv = new StringToStringConverter();
		saveGraph(graph, title, writer, conv, conv);
	}

	/**
	 * Creates a PlanetUML state chart assuming both the states and events are enum
	 * types.
	 * 
	 * @param          <S> the state type
	 * @param          <E> the event type
	 * @param          <C> the context type
	 * @param graph    the graph to save
	 * @param title    the title to put on the chart, null is ok for no title.
	 * @param filename the output file name
	 * @throws XMLStreamException on XML error
	 * @throws IOException        on IO error
	 */
	public static <S extends Enum<S>, E extends Enum<E>, C> void saveEnumGraph(StateMachineGraph<S, E, C> graph,
			String title,
			String filename) throws XMLStreamException, IOException {
		ToStringConverter<S> Sconv = new EnumToStringConverter<S>();
		ToStringConverter<E> Econv = new EnumToStringConverter<E>();
		saveGraph(graph, title, filename, Sconv, Econv);
	}

	/**
	 * Creates a PlanetUML state chart assuming both the states and events are enum
	 * types.
	 * 
	 * @param        <S> the state type
	 * @param        <E> the event type
	 * @param        <C> the context type
	 * @param graph  the graph to save
	 * @param title  the title to put on the chart, null is ok for no title.
	 * @param writer the output to write to
	 * @throws XMLStreamException on XML error
	 * @throws IOException        on IO error
	 */
	public static <S extends Enum<S>, E extends Enum<E>, C> void saveEnumGraph(StateMachineGraph<S, E, C> graph,
			String title,
			Writer writer) throws XMLStreamException, IOException {
		ToStringConverter<S> Sconv = new EnumToStringConverter<S>();
		ToStringConverter<E> Econv = new EnumToStringConverter<E>();
		saveGraph(graph, title, writer, Sconv, Econv);
	}

	/**
	 * Creates a PlanetUML state chart
	 * 
	 * @param          <S> the state type
	 * @param          <E> the event type
	 * @param          <C> the context type
	 * @param graph    the Spring StateMachineGraph instance to probe.
	 * @param title    the title to put on the chart, null is ok for no title.
	 * @param filename the file to save too.
	 * @param Sconv    the object for converting from a state to a String
	 * @param Econv    the object for converting from an event to a String
	 * @throws IOException on file I/O errors
	 */
	public static <S, E, C> void saveGraph(StateMachineGraph<S, E, C> graph, String title, String filename,
			ToStringConverter<S> Sconv, ToStringConverter<E> Econv)
			throws IOException {

		OutputStreamWriter f;
		f = new OutputStreamWriter(new FileOutputStream(filename), StandardCharsets.UTF_8);
		saveGraph(graph, title, new BufferedWriter(f), Sconv, Econv);
		f.close();
	}

	/**
	 * Creates a PlanetUML state chart
	 * 
	 * @param        <S> the state type
	 * @param        <E> the event type
	 * @param        <C> the context type
	 * @param graph  the StateMachineGraph instance to probe.
	 * @param title  the title to put on the chart, null is ok for no title.
	 * @param writer the output to write to
	 * @param Sconv  the object for converting from a state to a String
	 * @param Econv  the object for converting from an event to a String
	 * @throws IOException on file I/O errors
	 */
	public static <S, E, C> void saveGraph(StateMachineGraph<S, E, C> graph, String title, Writer writer,
			ToStringConverter<S> Sconv, ToStringConverter<E> Econv) throws IOException {

		List<StateInfo> lstates = analyzeStateMachine(graph, Sconv, Econv);

		final String[] arrowsFromAbove = { PlanetUMLConstants.DOWN_ARROW, PlanetUMLConstants.RIGHT_ARROW,
				PlanetUMLConstants.LEFT_ARROW };
		final String[] arrowsFromBelow = { PlanetUMLConstants.UP_ARROW, PlanetUMLConstants.LEFT_ARROW,
				PlanetUMLConstants.RIGHT_ARROW };

		writer.append(PlanetUMLConstants.START_UML + "\n");
		writer.append(PlanetUMLConstants.MONOCHROME + "\n");

		if (title != null && !title.isEmpty()) {
			writer.append(String.format("%s %s\n", PlanetUMLConstants.TITLE_PARAM, title));
		}

		for (StateInfo state : lstates) {
			String label = "";
			String clazz = "";
			if (state.qualifier != null) {
				label = String.format("\\n[<i>%s</i>]", state.qualifier.name());
				clazz = String.format(" <<%s>>", state.qualifier.name());
			}
			writer.append(String.format("%s \"%s%s\" %s %s%s\n", PlanetUMLConstants.STATE_PARAM, state.name, label,
					PlanetUMLConstants.AS, state.id, clazz));
		}

		for (StateInfo source : lstates) {
			if (source.qualifier == StateQualifer.initial) {
				writer.append(String.format("%s %s %s\n", PlanetUMLConstants.BEGIN_STATE,
						PlanetUMLConstants.RIGHT_ARROW, source.id));
			}
			if (source.qualifier == StateQualifer.done) {
				writer.append(String.format("%s %s %s\n", source.id, PlanetUMLConstants.DOWN_ARROW,
						PlanetUMLConstants.END_STATE));
			} else {
				int aboveN = 0;
				int belowN = 0;
				for (TransitionInfo t : source.transitions) {
					if (t.target.index >= source.index) {
						writer.append(String.format("%s %s %s : %s\n", source.id, arrowsFromAbove[aboveN++ % 3],
								t.target.id, t.event));
					} else {
						writer.append(String.format("%s %s %s : %s\n", source.id, arrowsFromBelow[belowN++ % 3],
								t.target.id, t.event));
					}
				}
			}
		}

		writer.append(PlanetUMLConstants.END_UML + "\n");

		writer.flush();

	}

	private static class StateInfo extends StateInfoBase {
		public List<TransitionInfo> transitions = new ArrayList<TransitionInfo>();

		public void addTransition(StateInfo targetState, String event) {
			TransitionInfo t = new TransitionInfo();
			t.target = targetState;
			t.event = event;
			transitions.add(t);
		}

	}

	private static class StateInfoBase {
		public int index = 0;
		public String id;
		public String name;
		public StateQualifer qualifier = null;
		boolean targeted = false;
		int pathlength = -1;

		public static int compare(StateInfoBase a, StateInfoBase b) {
			if (a == b)
				return 0;
			if (a.qualifier == StateQualifer.initial)
				return -1;
			if (b.qualifier == StateQualifer.initial)
				return 1;
			int x = Integer.compare(b.pathlength, a.pathlength);
			return x == 0 ? a.name.compareTo(b.name) : x;
		}

	};

	private static enum StateQualifer {
		/// the starting state
		initial,
		/// not reachable in the expected flow, but can be jumped to explicitly
		alternate,
		/// a state that is not reachable nor connected to anything else
		orphan,
		/// end of state machine)
		done;
	}

	private static class TransitionInfo {
		public StateInfoBase target;
		public String event;

	}

	private static <S, E> int getPathLength(StateInfo info) {
		if (info.pathlength < 0) {

			info.pathlength = 0; // handles circular paths

			int length = 0;
			for (TransitionInfo t : info.transitions) {
				if (t.target.qualifier != StateQualifer.initial) {
					int l = getPathLength(StateInfo.class.cast(t.target));
					length = (l > length) ? l : length;
				}
			}
			info.pathlength = length + 1;

		}
		return info.pathlength;
	}

	private static <S, E, C> List<StateInfo> analyzeStateMachine(StateMachineGraph<S, E, C> graph,
			ToStringConverter<S> Sconv, ToStringConverter<E> Econv) {

		S initialState = graph.getInitialState();
		List<StateInfo> stateList = new ArrayList<StateInfo>();
		Map<S, StateInfo> stateMAP = new HashMap<S, StateInfo>();
		StateInfo initial = null;

		// go through all the states first as some of them may be missing from the
		// transitions

		for (StateType<S, E, C> s : graph.getStates()) {
			StateInfo info = new StateInfo();
			info.name = Sconv.convertToString(s.getId());
			stateMAP.put(s.getId(), info);
			stateList.add(info);
			if (s.getId() == initialState) {
				initial = info;
			}

//			for (Entry<E, ? extends StateType<S, E, C>> entry : state.getTransitions().entrySet()) {
//			}

		}

		// walk all the transitions
		for (StateType<S, E, C> sourceState : graph.getStates()) {
			for (Entry<E, ? extends StateType<S, E, C>> entry : sourceState.getTransitions().entrySet()) {
				StateInfo source = stateMAP.get(sourceState.getId());
				StateType<S, E, C> targetState = entry.getValue();
				StateInfo target = stateMAP.get(targetState.getId());
				String event = Econv.convertToString(entry.getKey());
				source.addTransition(target, event);
				target.targeted = true; // help of determine if this node is reachable in the normal flow
			}
		}

		// compute path lengths, starting with the initial
		initial.qualifier = StateQualifer.initial;
		getPathLength(initial);
		for (StateInfo s : stateList) {
			if (s.pathlength < 0) {
				getPathLength(s);
			}
		}

		// sort for a predictable output
		stateList.sort((a, b) -> StateInfoBase.compare(a, b));
		for (int i = 0; i < stateList.size(); i++) {
			StateInfo state = stateList.get(i);
			state.index = i;
			state.id = generateId(state.name, i + 1);

			if (state == initial) {
				state.qualifier = StateQualifer.initial;
			} else if (!state.targeted && state.transitions.isEmpty()) {
				state.qualifier = StateQualifer.orphan;
			} else if (state.transitions.isEmpty()) {
				state.qualifier = StateQualifer.done;
			} else if (!state.targeted) {
				state.qualifier = StateQualifer.alternate;
			}
			// sort for a predictable output
			state.transitions.sort((a, b) -> StateInfoBase.compare(a.target, b.target));
		}

		return stateList;
	}

	private static String generateId(String name, int index) {
		// making a readable id
		StringBuffer sb = new StringBuffer(name.length() + 3);
		for (int i = 0; i < name.length(); i++) {
			char ch = name.charAt(i);
			if ((ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || (ch == '_')) {
				sb.append(ch);
			} else if (ch == ' ' || ch == '-') {
				sb.append('_');
			}
		}
		sb.append('_');
		sb.append(Integer.toString(index));
		return sb.toString();
	}
}
