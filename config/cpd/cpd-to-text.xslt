<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="text" encoding="UTF-8"/>

  <xsl:template match="/pmd-cpd">
    <xsl:choose>
      <xsl:when test="duplication">
        <xsl:for-each select="duplication">
Duplication: lines=<xsl:value-of select="@lines"/>, tokens=<xsl:value-of select="@tokens"/>
Occurrences:
<xsl:for-each select="file">
  <xsl:text>  Occurrence #</xsl:text><xsl:value-of select="position()"/> <xsl:text> - </xsl:text>
  <xsl:text>File: </xsl:text><xsl:value-of select="@path"/>
  <xsl:text> (lines </xsl:text><xsl:value-of select="@line"/><xsl:text> - </xsl:text><xsl:value-of select="@endline"/><xsl:text>)</xsl:text>
  <xsl:text>
</xsl:text>
</xsl:for-each>

Code fragment (normalized):
<xsl:value-of select="normalize-space(codefragment)"/>

------------------------------------------------------------

    </xsl:for-each>
      </xsl:when>
      <xsl:otherwise>
No duplications found.
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

</xsl:stylesheet>
