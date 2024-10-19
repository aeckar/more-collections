package io.github.aeckar.iteration

/**
 * A position in some source.
 * @param line the line number at this position, starting after each newline
 * @param column the column number at this position
 */
public data class SourcePosition(val line: Int, val column: Int) : Comparable<SourcePosition> {
    /**
     * Returns 0 if the positions are the same. If the lines are the same, returns the column difference.
     * Otherwise, returns the line difference.
     */
    override fun compareTo(other: SourcePosition): Int {
        val lineDiff = line - other.line
        if (lineDiff != 0) {
            return lineDiff
        }
        return column - other.column
    }
}