<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output indent="yes"/>
    <xsl:param name="secondFile"/>

    <xsl:template match="/">
        <beans>
            <xsl:attribute name="version">
                <xsl:value-of select="document($secondFile)/beans/@version"/>
            </xsl:attribute>

            <xsl:copy-of select="beans/bean"/>
            <xsl:copy-of select="document($secondFile)/beans/bean"/>
        </beans>
    </xsl:template>

</xsl:stylesheet>