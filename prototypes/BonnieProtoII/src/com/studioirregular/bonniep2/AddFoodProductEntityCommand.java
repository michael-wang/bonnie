package com.studioirregular.bonniep2;

public class AddFoodProductEntityCommand implements Command {

	private Scene scene;
	private FoodProductEntity food;
	
	public AddFoodProductEntityCommand(Scene scene, FoodProductEntity food) {
		this.scene = scene;
		this.food = food;
	}
	
	@Override
	public void execute() {
		scene.addEntity(food);
	}

}
