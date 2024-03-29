const { IOException } = require("java.io");
const { Path } = require("java.nio.file");
const { Paths } = require("java.nio.file");
function Main(){
	function main(args){
        try {
            var javaSource = Paths.get(".", "magmac", "src", "java").toAbsolutePath();
            var magmaSource = Paths.get(".", "magmac", "src", "magma").toAbsolutePath();
            var jsSource = Paths.get(".", "magmac", "src", "js").toAbsolutePath();
            var cSource = Paths.get(".", "magmac", "src", "c").toAbsolutePath();

            runImpl(javaSource, magmaSource, ".java", ".mgs");
            runImpl(magmaSource, jsSource, ".mgs", ".js");
            runImpl(magmaSource, cSource, ".mgs", ".c", ".h");

            var javaTest = Paths.get(".", "magmac", "test", "java").toAbsolutePath();
            var magmaTest = Paths.get(".", "magmac", "test", "magma").toAbsolutePath();
            var jsTest = Paths.get(".", "magmac", "test", "js").toAbsolutePath();
            var cTest = Paths.get(".", "magmac", "test", "c").toAbsolutePath();

            runImpl(javaTest, magmaTest, ".java", ".mgs");
            runImpl(magmaTest, jsTest, ".mgs", ".js");
            runImpl(magmaTest, cTest, ".mgs", ".c", ".h");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
	function runImpl(javaSource, magmaSource, sourceExtension, ...destinationExtension){
        new Application(new DirectorySourceSet(javaSource, sourceExtension), magmaSource, destinationExtension).run();
    }
	return {main};
}
module.exports = {
	Main
};