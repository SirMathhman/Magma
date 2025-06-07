package magma.compile;

import magma.ast.StructureType;
/**
 * Frame that is capable of storing nested structure type definitions.
 */
public interface StructureContainerFrame extends Frame {
    StructureContainerFrame defineStructureType(StructureType structureType);
}
