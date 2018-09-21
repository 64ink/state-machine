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
package com.nofacepress.statemachine.scxml;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.Map.Entry;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import com.nofacepress.statemachine.StateMachineGraph;
import com.nofacepress.statemachine.StateMachineGraphBuilder;
import com.nofacepress.statemachine.StateMachineGraphBuilder.StateMachineGraphBuild;
import com.nofacepress.statemachine.StateType;

import com.nofacepress.statemachine.typeconverters.EnumFromStringConverter;
import com.nofacepress.statemachine.typeconverters.EnumToStringConverter;
import com.nofacepress.statemachine.typeconverters.FromStringConverter;
import com.nofacepress.statemachine.typeconverters.StringFromStringConverter;
import com.nofacepress.statemachine.typeconverters.StringToStringConverter;
import com.nofacepress.statemachine.typeconverters.ToStringConverter;

/**
 * Utility class to load a StateMachineGraph from as will as save to a a file or
 * a stream in SCXML format.
 */
public class SCXMLManager {

	private static final String XML_ELEMENT_SCXML = "scxml";
	private static final String XML_ELEMENT_STATE = "state";
	private static final String XML_ELEMENT_TRANSITION = "transition";
	private static final String XML_ATTRIBUTE_SCXML_INITIAL = "initial";
	private static final String XML_ATTRIBUTE_STATE_ID = "id";
	private static final String XML_ATTRIBUTE_TRANSITION_EVENT = "event";
	private static final String XML_ATTRIBUTE_TRANSITION_TARGET = "target";

	/**
	 * Creates a StateMachineGraph assuming both the states and events are type
	 * String.
	 * 
	 * @param          <C> the context type
	 * @param filename file to read SCXML from.
	 * @return the resulting graph
	 * @throws XMLStreamException    on XML error
	 * @throws FileNotFoundException if file is not found
	 */
	public static <C> StateMachineGraph<String, String, C> loadStringGraph(String filename)
			throws XMLStreamException, FileNotFoundException {
		FromStringConverter<String> conv = new StringFromStringConverter();
		return loadGraph(filename, conv, conv);
	}

	/**
	 * Creates a StateMachineGraph assuming both the states and events are type
	 * String.
	 * 
	 * @param        <C> the context type
	 * @param stream the input stream
	 * @return the resulting graph
	 * @throws XMLStreamException on XML error
	 */
	public static <C> StateMachineGraph<String, String, C> loadStringGraph(InputStream stream)
			throws XMLStreamException {
		FromStringConverter<String> conv = new StringFromStringConverter();
		return loadGraph(stream, conv, conv);
	}

	/**
	 * Creates a StateMachineGraph assuming both the states and events are enums.
	 * 
	 * @param          <S> the state type
	 * @param          <E> the event type
	 * @param          <C> the context type
	 * @param filename file to read SCXML from.
	 * @param Sclazz   the state enum class
	 * @param Eclazz   the event enum class
	 * @return the resulting graph
	 * @throws XMLStreamException    on XML error
	 * @throws FileNotFoundException if file is not found
	 */
	public static <S extends Enum<S>, E extends Enum<E>, C> StateMachineGraph<S, E, C> loadEnumGraph(String filename,
			Class<S> Sclazz, Class<E> Eclazz) throws XMLStreamException, FileNotFoundException {
		FromStringConverter<S> Sconv = new EnumFromStringConverter<S>(Sclazz);
		FromStringConverter<E> Econv = new EnumFromStringConverter<E>(Eclazz);
		return loadGraph(filename, Sconv, Econv);
	}

	/**
	 * Creates a StateMachineGraph assuming both the states and events are enums.
	 * 
	 * @param        <S> the state type
	 * @param        <E> the event type
	 * @param        <C> the context type
	 * @param stream the input stream
	 * @param Sclazz the state enum class
	 * @param Eclazz the event enum class
	 * @return the resulting graph
	 * @throws XMLStreamException on XML error
	 */
	public static <S extends Enum<S>, E extends Enum<E>, C> StateMachineGraph<S, E, C> loadEnumGraph(InputStream stream,
			Class<S> Sclazz, Class<E> Eclazz) throws XMLStreamException {
		FromStringConverter<S> Sconv = new EnumFromStringConverter<S>(Sclazz);
		FromStringConverter<E> Econv = new EnumFromStringConverter<E>(Eclazz);
		return loadGraph(stream, Sconv, Econv);
	}

	/**
	 * Creates a StateMachineGraph
	 * 
	 * @param          <S> the state type
	 * @param          <E> the event type
	 * @param          <C> the context type
	 * @param filename file to read SCXML from.
	 * @param Sconv    the object to use to convert from as String to the state type
	 * @param Econv    the object to use to convert from as String to the event type
	 * @return the resulting graph
	 * @throws XMLStreamException    on XML error
	 * @throws FileNotFoundException if file is not found
	 */
	public static <S, E, C> StateMachineGraph<S, E, C> loadGraph(String filename, FromStringConverter<S> Sconv,
			FromStringConverter<E> Econv) throws XMLStreamException, FileNotFoundException {
		StateMachineGraph<S, E, C> graph = null;
		InputStream stream = new BufferedInputStream(new FileInputStream(filename));
		try {
			graph = loadGraph(stream, Sconv, Econv);
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
			}
		}
		return graph;
	}

	/**
	 * Creates a StateMachineGraph
	 * 
	 * @param        <S> the state type
	 * @param        <E> the event type
	 * @param        <C> the context type
	 * @param stream the input stream
	 * @param Sconv  the object to use to convert from as String to the state type
	 * @param Econv  the object to use to convert from as String to the event type
	 * @return the resulting graph
	 * @throws XMLStreamException on XML error
	 */
	public static <S, E, C> StateMachineGraph<S, E, C> loadGraph(InputStream stream, FromStringConverter<S> Sconv,
			FromStringConverter<E> Econv) throws XMLStreamException {

		StateMachineGraphBuild<S, E, C> build = StateMachineGraphBuilder.builder();

		XMLInputFactory xif = XMLInputFactory.newInstance();
		XMLStreamReader xsr = xif.createXMLStreamReader(stream);

		boolean inScxml = false;
		S currentState = null;

		while (xsr.hasNext()) {
			xsr.next();
			if (!inScxml) {
				if (xsr.isStartElement()) {
					String name = xsr.getLocalName().toLowerCase();
					if (name.equals(XML_ELEMENT_SCXML)) {
						inScxml = true;
						String text = xsr.getAttributeValue(null, XML_ATTRIBUTE_SCXML_INITIAL);
						if (text != null) {
							S initial = Sconv.convertFromString(text);
							build.initial(initial);
						}
					}
				}
				continue;
			}

			if (xsr.isStartElement()) {
				String name = xsr.getLocalName().toLowerCase();
				if (currentState == null && name.equals(XML_ELEMENT_STATE)) {
					String text = xsr.getAttributeValue(null, XML_ATTRIBUTE_STATE_ID);
					if (text != null) {
						currentState = Sconv.convertFromString(text);
						build.state(currentState);
					}
				} else if (currentState != null && name.equals(XML_ELEMENT_TRANSITION)) {
					String text1 = xsr.getAttributeValue(null, XML_ATTRIBUTE_TRANSITION_EVENT);
					String text2 = xsr.getAttributeValue(null, XML_ATTRIBUTE_TRANSITION_TARGET);
					if (text1 != null && text2 != null) {
						E event = Econv.convertFromString(text1);
						S target = Sconv.convertFromString(text2);
						build.transition(currentState, target, event);
					}
				}
			} else if (xsr.isEndElement()) {
				String name = xsr.getLocalName().toLowerCase();
				if (name.equals(XML_ELEMENT_STATE)) {
					currentState = null;
				} else if (name.equals(XML_ELEMENT_SCXML)) {
					inScxml = false;
				}
			}
		}

		return build.build();
	}

	/**
	 * Saves a StateMachineGraph assuming both the states and events are type
	 * String.
	 * 
	 * @param          <C> the context type
	 * @param graph    the graph to save
	 * @param filename the output file name
	 * @throws XMLStreamException on XML error
	 * @throws IOException        on IO error
	 */
	public static <C> void saveStringGraph(StateMachineGraph<String, String, C> graph, String filename)
			throws XMLStreamException, IOException {
		ToStringConverter<String> conv = new StringToStringConverter();
		saveGraph(graph, filename, conv, conv);
	}

	/**
	 * Saves a StateMachineGraph assuming both the states and events are type
	 * String.
	 * 
	 * @param        <C> the context type
	 * @param graph  the graph to save
	 * @param stream the output stream
	 * @throws XMLStreamException on XML error
	 * @throws IOException        on IO error
	 */
	public static <C> void saveStringGraph(StateMachineGraph<String, String, C> graph, OutputStream stream)
			throws XMLStreamException, IOException {
		ToStringConverter<String> conv = new StringToStringConverter();
		saveGraph(graph, stream, conv, conv);
	}

	/**
	 * Saves a StateMachineGraph assuming both the states and events are enum types.
	 * 
	 * @param          <S> the state type
	 * @param          <E> the event type
	 * @param          <C> the context type
	 * @param graph    the graph to save
	 * @param filename the output file name
	 * @throws XMLStreamException on XML error
	 * @throws IOException        on IO error
	 */
	public static <S extends Enum<S>, E extends Enum<E>, C> void saveEnumGraph(StateMachineGraph<S, E, C> graph,
			String filename) throws XMLStreamException, IOException {
		ToStringConverter<S> Sconv = new EnumToStringConverter<S>();
		ToStringConverter<E> Econv = new EnumToStringConverter<E>();
		saveGraph(graph, filename, Sconv, Econv);
	}

	/**
	 * Saves a StateMachineGraph assuming both the states and events are enum types.
	 * 
	 * @param        <S> the state type
	 * @param        <E> the event type
	 * @param        <C> the context type
	 * @param graph  the graph to save
	 * @param stream the output stream
	 * @throws XMLStreamException on XML error
	 * @throws IOException        on IO error
	 */
	public static <S extends Enum<S>, E extends Enum<E>, C> void saveEnumGraph(StateMachineGraph<S, E, C> graph,
			OutputStream stream) throws XMLStreamException, IOException {
		ToStringConverter<S> Sconv = new EnumToStringConverter<S>();
		ToStringConverter<E> Econv = new EnumToStringConverter<E>();
		saveGraph(graph, stream, Sconv, Econv);
	}

	/**
	 * Saves a StateMachineGraph
	 * 
	 * @param          <S> the state type
	 * @param          <E> the event type
	 * @param          <C> the context type
	 * @param graph    the graph to save
	 * @param filename the output file name
	 * @param Sconv    the object for converting from a state to a String
	 * @param Econv    the object for converting from an event to a String
	 * @throws XMLStreamException on XML error
	 * @throws IOException        on IO error
	 */
	public static <S, E, C> void saveGraph(StateMachineGraph<S, E, C> graph, String filename,
			ToStringConverter<S> Sconv,
			ToStringConverter<E> Econv) throws XMLStreamException, IOException {
		OutputStream stream = new BufferedOutputStream(new FileOutputStream(filename));
		try {
			saveGraph(graph, stream, Sconv, Econv);
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
			}
		}
	}

	/**
	 * Saves a StateMachineGraph
	 * 
	 * @param        <S> the state type
	 * @param        <E> the event type
	 * @param        <C> the context type
	 * @param graph  the graph to save
	 * @param stream the output stream
	 * @param Sconv  the object for converting from a state to a String
	 * @param Econv  the object for converting from an event to a String
	 * @throws XMLStreamException on XML error
	 * @throws IOException        on IO error
	 */
	public static <S, E, C> void saveGraph(StateMachineGraph<S, E, C> graph, OutputStream stream,
			ToStringConverter<S> Sconv,
			ToStringConverter<E> Econv)
			throws IOException, XMLStreamException {

		XMLOutputFactory factory = XMLOutputFactory.newInstance();
		XMLStreamWriter writer = factory.createXMLStreamWriter(stream, "UTF-8");

		writer.writeStartDocument("UTF-8", "1.0");
		writer.writeCharacters("\n");
		writer.writeStartElement(XML_ELEMENT_SCXML);
		writer.writeAttribute("xmlns", "http://www.w3.org/2005/07/scxml");
		writer.writeAttribute("version", "1.0");
		writer.writeAttribute(XML_ATTRIBUTE_SCXML_INITIAL, Sconv.convertToString(graph.getInitialState()));

		for (StateType<S, E, C> state : graph.getStates()) {
			writer.writeCharacters("\n  ");
			writer.writeStartElement(XML_ELEMENT_STATE);
			writer.writeAttribute(XML_ATTRIBUTE_STATE_ID, Sconv.convertToString(state.getId()));

			for (Entry<E, ? extends StateType<S, E, C>> entry : state.getTransitions().entrySet()) {
				writer.writeCharacters("\n    ");
				writer.writeStartElement(XML_ELEMENT_TRANSITION);
				writer.writeAttribute(XML_ATTRIBUTE_TRANSITION_EVENT, Econv.convertToString(entry.getKey()));
				writer.writeAttribute(XML_ATTRIBUTE_TRANSITION_TARGET, Sconv.convertToString(entry.getValue().getId()));
				writer.writeEndElement();
			}

			writer.writeEndElement();
		}

		writer.writeEndElement();
		writer.writeCharacters("\n");
		writer.writeEndDocument();
		writer.flush();

	}

}
