export class Platform {
	 static TypeScript = "TypeScript";
	 static PlantUML = "PlantUML";
	static values(): Platform[] {
		return [Platform.TypeScript, Platform.PlantUML];
	}
}
