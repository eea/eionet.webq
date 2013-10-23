<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output indent="yes"/>
    <xsl:param name="secondFileId"/>


    <xsl:template match="/">
        <derogations xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                     xsi:noNamespaceSchemaLocation="http://biodiversity.eionet.europa.eu/schemas/bernconvention/derogations.xsd">
            <xsl:attribute name="country">
                <xsl:value-of select="derogations/@country"/>
            </xsl:attribute>
            <xsl:attribute name="lang">
                <xsl:value-of select="derogations/@lang"/>
            </xsl:attribute>

            <xsl:copy-of select="derogations/derogation"/>
            <xsl:copy-of select="document($secondFileId)/derogations/derogation"/>
        </derogations>
    </xsl:template>
</xsl:stylesheet>