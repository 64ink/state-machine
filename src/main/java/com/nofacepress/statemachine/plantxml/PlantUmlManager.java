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

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import com.nofacepress.statemachine.StateMachineGraph;
import com.nofacepress.statemachine.typeconverters.ToStringConverter;

/**
 * Creates a PlanetUML state chart based on information probed from a State
 * Machine. This was created to find errors when setting up the state machine.
 */
public class PlantUmlManager extends BaseManager {

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
	@Override
	public <S, E, C> void saveGraph(StateMachineGraph<S, E, C> graph, String title, Writer writer,
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
	
}
