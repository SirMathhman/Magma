import { Source } from "../../../magma/app/io/Source";
import { Iter } from "../../../magma/api/collect/Iter";
import { Platform } from "../../../magma/app/Platform";
import { Option } from "../../../magma/api/option/Option";
import { Location } from "../../../magma/app/Location";
import { List } from "../../../magma/api/collect/list/List";
export interface Context {
	iterSources(): Iter<Source>;
	hasPlatform(platform: Platform): boolean;
	findSource(name: string): Option<Source>;
	withLocation(location: Location): Context;
	addSource(source: Source): Context;
	findNamespaceOrEmpty(): List<string>;
	findNameOrEmpty(): string;
	withPlatform(platform: Platform): Context;
	maybeLocation(): Option<Location>;
	sources(): List<Source>;
}
