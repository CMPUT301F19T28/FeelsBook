public class Hexagon extends Shape{
	private Shape color = "blue"
	
	public Hexagon(){
		super();
	}

	public Hexagon(int x, int y){
		super(x,y);
	}

	public int getX(){ return this.x;}

	public void setX(int x){ this.x = x;}

	public int getY(){ return this.y;}

	public void setY(int y){ this.y = y;}


