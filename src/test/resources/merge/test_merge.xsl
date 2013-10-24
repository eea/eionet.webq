<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output indent="yes"/>

    <xsl:template match="/">
        <beans>
            <xsl:attribute name="version">
                <xsl:value-of select="document('current_document')/beans/@version"/>
            </xsl:attribute>

            <xsl:copy-of select="beans/bean"/>
            <xsl:copy-of select="document('current_document')/beans/bean"/>
        </beans>
    </xsl:template>

</xsl:stylesheet>