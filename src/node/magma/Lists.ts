
class Lists {
	constructor () {
	}
	public static empty<String>() : ListLike<string> {
		return new JavaList<>();
	}
}

