package robust.pc.api;

import java.util.List;

import robust.pc.util.Point2D;

/**
 * In fact this is a simple API for Pattern recognition system. 
 * Since vision data transmition usually takes significant amount memory 
 * it is almost impossible to transmit all raw visual data. 
 * Thus it is assumes that hardware (or more precisely) software running on hardware 
 * does some simple pattern recognition relies on extracting specific primitives. 
 * Robust visual api provides method to obtain scene given as a set of primitives.
 * 
 * @author Konrad Kulakowski
 */
public interface RobustAPIVision extends RobustAPI {
	interface Primitive2D {
		/** 
		 * It depends on specific vision system available
		 * This might be square (0), rectangle(1), triangle (3), etc.
		 */
		int getKind();
		/**
		 * @return it returns some specific point of figure (e.g. left upper corner)
		 */
		Point2D<Integer> getPosition();
		
		/**
		 * @return returns size of the figure. E.g. size of the area
		 */
		int getSizeOfPrimitive();
		
		/**
		 * @return primitive colors
		 */
		int getColorOfPrimitive();
	}
	
	List<Primitive2D> getScene2D();
	
	/**
	 * setting some specific color as a filter cause that vision subsystem 
	 * will perceive only objects having some specific color 
	 */
	void filterPrimitivesByColor(int colorNo);
	
	/** 
	 * setting objects' size as a filter cause that all the objects having size 
	 * smaller than the specified will be filtered out 
	 * 
	 * @param objectSize
	 */
	
	void filterPrimitivesBySize(int objectSize);
	/** 
	 * setting objects' kind as a filter cause that all the objects having kind 
	 * different than the specified will be filtered out 
	 * 
	 * @param objectKind
	 */	
	void filterPrimitivestByKind(int objectKind);
}