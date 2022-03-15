package client.test.map;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import client.model.Figure;
import client.model.FigureType;
import client.model.MapElement;
import client.model.MapElementType;

class T_MapElement {

	// extensively tests the containsFigure() method with several different lists of figures
	// should return false if list is empty or doesnt contain figure, true if list contains figure
	@Test
	void listOfFigures_containsFigure_boolean() {
		// ARRANGE: create lists of figures and add respective figures to it
		List<Figure> noFigures = new ArrayList<>();
		List<Figure> oneFigure = new ArrayList<>();
		List<Figure> twoFigures = new ArrayList<>();
		MapElementType testType = MapElementType.GRASS; 
		
		oneFigure.add(new Figure(FigureType.MY_TREASURE));
		
		twoFigures.add(new Figure(FigureType.MY_AVATAR));
		twoFigures.add(new Figure(FigureType.MY_TREASURE));
		
		// ACT: execute containsFigures functions for various setups
		MapElement testOne = new MapElement(testType, noFigures);
		boolean containsInNoFigures = testOne.containsFigure(FigureType.MY_AVATAR);
		
		MapElement testTwo = new MapElement(testType, oneFigure);
		boolean containsInOneFigureWrongElement = testTwo.containsFigure(FigureType.MY_AVATAR);
		boolean containsInOneFigureRightElement = testTwo.containsFigure(FigureType.MY_TREASURE);
		
		MapElement testThree = new MapElement(testType, twoFigures);
		boolean containsInTwoFiguresWrongElement = testThree.containsFigure(FigureType.MY_CASTLE);
		boolean containsInTwoFiguresRightElement = testThree.containsFigure(FigureType.MY_TREASURE);
		
		// ASSERT: check whether the computes values return the expected value
		assertEquals(false, containsInNoFigures);
		assertEquals(false, containsInOneFigureWrongElement);
		assertEquals(true, containsInOneFigureRightElement);
		assertEquals(false, containsInTwoFiguresWrongElement);
		assertEquals(true, containsInTwoFiguresRightElement);
	}
	
	// this test checks the removeFigure method
	// expected results: trying to remove figure from an empty list should return false
	// trying to remove a figure that is not in list should return false
	// removing a figure that is in list should return true
	@Test
	void listOfFigures_removeFigure_boolean() {
		// ARRANGE: create various types of lists with figures
		List<Figure> noFigures = new ArrayList<>();
		List<Figure> oneFigure = new ArrayList<>();
		List<Figure> twoFigures = new ArrayList<>();
		MapElementType testType = MapElementType.GRASS; 
		
		oneFigure.add(new Figure(FigureType.MY_TREASURE));
		
		twoFigures.add(new Figure(FigureType.MY_AVATAR));
		twoFigures.add(new Figure(FigureType.MY_TREASURE));
		
		// ACT: execute tested function on given figures to be removed
		MapElement testOne = new MapElement(testType, noFigures);
		boolean removeInNoFigures = testOne.removeFigure(FigureType.MY_AVATAR);
		
		MapElement testTwo = new MapElement(testType, oneFigure);
		boolean removeInOneFigureWrongElement = testTwo.removeFigure(FigureType.MY_AVATAR);
		boolean removeInOneFigureRightElement = testTwo.removeFigure(FigureType.MY_TREASURE);
		
		MapElement testThree = new MapElement(testType, twoFigures);
		boolean removeInTwoFiguresWrongElement = testThree.removeFigure(FigureType.MY_CASTLE);
		boolean removeInTwoFiguresRightElement = testThree.removeFigure(FigureType.MY_TREASURE);
		
		
		// ASSERT: check whether the computed values return the expected value
		assertEquals(false, removeInNoFigures);
		assertEquals(false, removeInOneFigureWrongElement);
		assertEquals(true, removeInOneFigureRightElement);
		assertEquals(false, removeInTwoFiguresWrongElement);
		assertEquals(true, removeInTwoFiguresRightElement);
	}
	
	

}
