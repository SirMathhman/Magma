package magma;

/**
 * Frame that is capable of storing nested structure type definitions.
 */
public interface StructureContainerFrame extends Frame {
    StructureContainerFrame defineStructureType(StructureType structureType);
}
