
public class Ellipse extends Shape{
	private int minorAxis;
	private int majorAxis;
	
	Ellipse(int minorAxis, int majorAxis){
		this.minorAxis = minorAxis;
		this.majorAxis = majorAxis;
	}
	
	public void setEllipse(minorAxis,majorAxis){
		this.majorAxis = minorAxis;
		this.minorAxis = majorAxis;
	}
	
	public Ellipse getEllipse(){
		return this.Ellipse;
	}

}
