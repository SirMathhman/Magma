import { Context } from "../../../magma/app/compile/Context";
import { Platform } from "../../../magma/app/Platform";
import { Option } from "../../../magma/api/option/Option";
import { Location } from "../../../magma/app/Location";
import { Source } from "../../../magma/app/io/Source";
import { List } from "../../../magma/api/collect/list/List";
import { None } from "../../../magma/api/option/None";
import { Lists } from "../../../jvm/api/collect/list/Lists";
import { Iter } from "../../../magma/api/collect/Iter";
import { Some } from "../../../magma/api/option/Some";
export class ImmutableContext implements Context {
	maybePlatform: Option<Platform>;
	maybeLocation: Option<Location>;
	sources: List<Source>;
	constructor (maybePlatform: Option<Platform>, maybeLocation: Option<Location>, sources: List<Source>) {
		this.maybePlatform = maybePlatform;
		this.maybeLocation = maybeLocation;
		this.sources = sources;
	}
	static createEmpty(): Context {
		return new ImmutableContext(new None<Platform>(), new None<Location>(), Lists.empty())/*unknown*/;
	}
	iterSources(): Iter<Source> {
		return this.sources.iter()/*unknown*/;
	}
	hasPlatform(platform: Platform): boolean {
		return this.maybePlatform.filter((thisPlatform: Platform) => thisPlatform === platform/*unknown*/).isPresent()/*unknown*/;
	}
	findSource(name: string): Option<Source> {
		return this.iterSources().filter((source: Source) => source.createLocation().hasName(name)/*unknown*/).next()/*unknown*/;
	}
	withLocation(location: Location): Context {
		return new ImmutableContext(this.maybePlatform, new Some<Location>(location), this.sources)/*unknown*/;
	}
	addSource(source: Source): Context {
		return new ImmutableContext(this.maybePlatform, this.maybeLocation, this.sources.addLast(source))/*unknown*/;
	}
	findLocation(): Option<Location> {
		return this.maybeLocation/*unknown*/;
	}
	withPlatform(platform: Platform): Context {
		return new ImmutableContext(new Some<Platform>(platform), this.maybeLocation, this.sources)/*unknown*/;
	}
}
