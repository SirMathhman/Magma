import { Source } from "../../../magma/app/io/Source";
import { Iter } from "../../../magma/api/collect/Iter";
import { Platform } from "../../../magma/app/Platform";
import { Option } from "../../../magma/api/option/Option";
import { Location } from "../../../magma/app/Location";
export interface Context {
	iterSources(): Iter<Source>;
	hasPlatform(platform: Platform): boolean;
	findSource(name: string): Option<Source>;
	withLocation(location: Location): Context;
	addSource(source: Source): Context;
	findLocation(): Option<Location>;
	withPlatform(platform: Platform): Context;
}
