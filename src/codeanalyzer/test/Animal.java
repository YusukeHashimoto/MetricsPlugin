package codeanalyzer.test;

abstract class Animal {
	
	public void eat(Food food) {
		if(canEat(food)) {
			
		}
	}
	
	public void sleep() {
		
	}
	
	public void walk() {
		
	}
	
	public boolean canEat(Food food) {
		return true;
	}
}
